package im.tox.core.dht

/**
 * The DHT module takes care of finding the ip/port of peers and establishing
 * a route to them directly with UDP using hole punching if necessary. The DHT
 * only runs on UDP and so is only used if UDP works.
 *
 * The DHT public key of a friend is found
 * using the onion module. Once the DHT public key of a friend is known, the DHT
 * is used to find them and connect directly to them via UDP.
 *
 * The DHT is a self-organizing swarm of all peers in the Tox network which
 * uses the UDP protocol. Self-organizing occurs through each peer in the DHT
 * connecting to the X number of peers, 32 in the toxcore implementation,
 * closest to their own DHT public key. This closeness is defined by a distance
 * function.
 *
 * If each peer in the network knows the peers with the DHT public key closest
 * to its DHT public key, then to find a specific peer with public key X a peer
 * just needs to recursively ask peers in the DHT for known peers that have the
 * DHT public keys closest to X. Eventually the peer will find the peers in the
 * DHT that are the closest to that peer and, if that peer is online, they will
 * find them.
 *
 *
 * The encrypted message is encrypted using the reciever's DHT Public key, the
 * sender's DHT private key and the nonce (randomly generated 24 bytes).
 *
 * NAT ping packets (This sits inside the DHT request packet):
 * [uint8_t with a value of 254][char with 0][8 byte random number]
 * [uint8_t with a value of 254][char with 1][8 byte random number (The same that was sent in the request)]
 *
 * NAT ping packets are used to see if a friend we are not connected to directly
 * is online and ready to do the hole punching.
 *
 * Hole punching:
 *
 * For holepunching we assume that people using Tox are on one of 3 types of
 * NAT:
 *
 * Cone NATs: Assign one whole port to each UDP socket behind the NAT, any
 * packet from any ip/port sent to that assigned port from the internet will be
 * forwarded to the socket behind it.
 *
 * Restricted Cone NATs: Assign one whole port to each UDP socket behind the
 * NAT. However, it will only forward packets from ips that the UDP socket has
 * sent a packet to.
 *
 * Symmetric NATs: The worst kind of NAT, they assign a new port for each
 * ip/port a packet is sent to. They treat each new peer you send a UDP packet
 * to as a 'connection' and will only forward packets from the ip/port of that
 * 'connection'.
 *
 *
 *
 * Holepunching on normal cone NATs is achieved simply through the way in which
 * the DHT functions.
 *
 * If more than half of the 8 peers closest to the friend in the DHT return an
 * ip/port for the friend and we send a ping request to each of the returned
 * ip/ports but get no response. If we have sent 4 ping requests to 4 ip/ports
 * that supposedly belong to the friend and get no response, then this is enough
 * for toxcore to start the hole punching. The numbers 8 and 4 are used in
 * toxcore and where chosen based on feel alone and so may not be the best
 * numbers.
 *
 * Before starting the hole punching, the peer will send a NAT ping packet
 * to the friend via the peers that say they know the friend. If a NAT ping
 * response with the same random number is received the hole punching will
 * start.
 *
 * If a NAT ping request is received, we will first check if it is from a
 * friend. If it is not from a friend it will be dropped. If it is from a
 * friend, a response with the same 8 byte number as in the request will be sent
 * back via the nodes that know the friend sending the request. If no nodes from
 * the friend are known, the packet will be dropped.
 *
 * Receiving a NAT ping response therefore means that the friend is both online
 * and actively searching for us, as that is the only way they would know nodes
 * that know us. This is important because hole punching will work only if the
 * friend is actively trying to connect to us.
 *
 * NAT ping requests are sent every 3 seconds in toxcore, if no response is
 * received for 6 seconds, the hole punching will stop. Sending them in longer
 * intervals might increase the possibility of the other node going offline and
 * ping packets sent in the hole punching being sent to a dead peer but decrease
 * bandwidth usage. Decreasing the intervals will have the opposite effect.
 *
 * There are 2 cases that toxcore handles for the hole punching. The first case
 * is if each 4+ peers returned the same ip and port. The second is if the 4+
 * peers returned same ips but different ports.
 *
 * A third case that may occur is the peers returning different ips and ports.
 * This can only happen if the friend is behind a very restrictive NAT that
 * cannot be hole punched or if the peer recently connected to another internet
 * connection and some peers still have the old one stored. Since there is
 * nothing we can do for the first option it is recommended to just use the most
 * common ip returned by the peers and to ignore the other ip/ports.
 *
 * In the case where the peers return the same ip and port it means that the
 * other friend is on a restricted cone NAT. These kind of NATs can be hole
 * punched by getting the friend to send a packet to our public IP/port. This
 * means that hole punching can be achieved easily and that we should just
 * continue sending DHT ping packets regularly to that ip/port until we get a
 * ping response. This will work because the friend is searching for us in the
 * DHT and will find us and will send us a packet to our public IP/port (or try
 * to with the hole punching), thereby establishing a connection.
 *
 * For the case where peers do not return the same ports, this means that the
 * other peer is on a symmetric NAT. Some symmetric NATs open ports in sequences
 * so the ports returned by the other peers might be something like: 1345, 1347,
 * 1389, 1395. The method to hole punch these NATs is to try to guess which
 * ports are more likely to be used by the other peer when they try sending us
 * ping requests and send some ping requests to these ports. Toxcore just tries
 * all the ports beside each returned port (ex: for the 4 ports previously it
 * would try: 1345, 1347, 1389, 1395, 1346, 1348, 1390, 1396, 1344, 1346...)
 * getting gradually further and further away and, although this works, the
 * method could be improved. When using this method toxcore will try up to 48
 * ports every 3 seconds until both connect. After 5 tries toxcore doubles this
 * and starts trying ports from 1024 (48 each time) along with the previous port
 * guessing. This is because I have noticed that this seemed to fix it for some
 * symmetric NATs, most likely because a lot of them restart their count at
 * 1024.
 *
 * Increasing the amount of ports tried per second would make the hole punching
 * go faster but might DoS NATs due to the large number of packets being sent to
 * different ips in a short amount of time. Decreasing it would make the hole
 * punching slower.
 *
 * This works in cases where both peers have different NATs. For example, if
 * A and B are trying to connect to each other: A has a symmetric NAT and B a
 * restricted cone NAT. A will detect that B has a restricted cone NAT and keep
 * sending ping packets to his one ip/port. B will detect that A has a symmetric
 * NAT and will send packets to it to try guessing his ports. If B manages to
 * guess the port A is sending packets from they will connect together.
 */
object DhtHandler
