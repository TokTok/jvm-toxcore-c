Core protocol
=============

This is a detailed description of the Core network protocol, which establishes
the secure communications channel. It sits on top of a low-level UDP- or
TCP-based protocol.


Node format
-----------

Various packets in the Core protocol send information about nodes. They share
the following packet layout:

    private packet IPv4 {
        enum Protocol<uint1> {
            UDP = 0;
            TCP = 1;
        }
        protocol	: Protocol;
        family		: uint7 const = 0b10;
        address		: uint8[4];
    }

    private packet IPv6 {
        enum Protocol<uint1> {
            UDP = 0;
            TCP = 1;
        }
        protocol	: Protocol;
        family		: uint7 const = 0b1010;
        address		: uint8[16];
    }

    packet Node {
        ip		: one_of<IPv4, IPv6>;
        port		: uint16;
        dht_public_key	: uint8[32];
    }


Initialisation
--------------

The first thing a client does on start is creating a temporary key pair with a
lifetime equal to the client's lifetime. This key pair is used for communicating
with other DHT nodes.

    // A key pair consisting of a public and private key, each 32 bytes long.
    type key_pair {
        public_key	: uint8[32];
        private_key	: uint8[32];
    }

    // Type for a node in the DHT network.
    type node {
        dht		: key_pair;
    }

    // The two participants in most communications. Alice and Bob, here called
    // sender and receiver. Sender is always the node that is sending a packet,
    // and may be either Alice or Bob.
    operator sender : node;
    operator receiver : node;


DHT bootstrap
-------------

A DHT network is a set of clients participating in the same DHT storage and
onion routing. There can be any number of disjoint networks. There are no
permanent connections and no handshakes between DHT nodes, just requests and
responses.

On bootstrap, the new node sends a packet to an existing node, the IP address it
can obtain from a central place, or a hard-coded value.

    packet NodesRequest {
        type			: uint8 const = 0x02;
        // The temporary DHT public key of the requesting node.
        public_key		: uint8[32] = sender.dht.public_key;
        nonce			: uint8[24];
        // private_key is the private key of the requesting node.
        encrypted(nonce, sender.dht.private_key) {
            // The Node ID for which we want the IP address.
            requested_node_id	: uint8[32];
            // A 64 bit number. The exact format is implementation defined.
            ping_id		: uint64;
        }
    }

Upon receiving this packet, the bootstrap node replies with a nodes response
packet.

    packet NodesResponse {
        type			: uint8 const = 0x04;
        // The temporary DHT public key of the responding node.
        public_key		: uint8[32] = sender.dht.public_key;
        nonce			: uint8[24];
        // private_key is the private key of the responding node.
        encrypted(nonce, sender.dht.private_key) {
           // Ranges between 1 and 4.
           nodes_size		: uint8;
           nodes		: Node[nodes_size];
           // Equal to the request's `NodesRequest.ping_id`.
           ping_id		: uint64;
        }
    }

If the responding node did not have the requested node, it sends information
about up to 4 nodes whose DHT public keys have the smallest distance from the
requested node DHT public key. The requesting node can then send requests to the
newly discovered nodes, recursively, until it finds the node it was looking for.

If a responding node returned a result with the requested node in it, it can
additionally return up to 3 nodes whose DHT public keys are closest to the
requested node's DHT public key.

Nodes will regularly ping the nodes they are aware of with an echo request
packet, and will receive an echo response packet.

    packet EchoRequest {
        type			: uint8 const = 0x00;
        // The temporary DHT public key of the requesting node.
        public_key		: uint8[32] = sender.dht.public_key;
        nonce			: uint8[24];
        // private_key is the private key of the requesting node.
        encrypted(nonce, sender.dht.private_key) {
            enc_type		: uint8 const = 0x00;
            ping_id		: uint64;
        }
    }

The layout of the echo response packet is exactly the same as the echo request,
except that the two type values are 1 instead of 0.

    packet EchoResponse {
        type			: uint8 const = 0x01;
        // The temporary DHT public key of the responding node.
        public_key		: uint8[32] = sender.dht.public_key;
        nonce			: uint8[24];
        // private_key is the private key of the responding node.
        encrypted(nonce, sender.dht.private_key) {
            enc_type		: uint8 const = 0x01;
            ping_id		: uint64;
        }
    }

The `enc_type` exists to prevent replay.

Using these packet types, we can now specify the DHT node as a service.

    service DHTNode {
        Nodes : ~req:NodesRequest -> ~res:NodesResponse {
            constraint req.ping_id = res.ping_id;
            constraint 1 <= res.nodes_size <= 4;
        }
        Echo : ~req:EchoRequest -> ~res:EchoResponse {
            constraint req.ping_id = res.ping_id;
        }
    }
