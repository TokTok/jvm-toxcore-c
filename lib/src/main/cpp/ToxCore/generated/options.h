static bool
set_options_from_proto (JNIEnv *env, Tox_Options *opts, im::tox::tox4j::core::proto::Options const &msg)
{
  bool ok = true;
  tox_options_set_ipv6_enabled (opts, msg.ipv6_enabled ());
  tox_options_set_udp_enabled (opts, msg.udp_enabled ());
  tox_options_set_local_discovery_enabled (opts, msg.local_discovery_enabled ());
  tox_options_set_dht_announcements_enabled (opts, msg.dht_announcements_enabled ());
  tox_options_set_proxy_type (opts, Enum::valueOf<Tox_Proxy_Type> (env, msg.proxy_type ()));
  ok = tox_options_set_proxy_host (opts, msg.proxy_host ().c_str ()) && ok;
  tox_options_set_proxy_port (opts, static_cast<uint16_t> (msg.proxy_port ()));
  tox_options_set_start_port (opts, static_cast<uint16_t> (msg.start_port ()));
  tox_options_set_end_port (opts, static_cast<uint16_t> (msg.end_port ()));
  tox_options_set_tcp_port (opts, static_cast<uint16_t> (msg.tcp_port ()));
  tox_options_set_hole_punching_enabled (opts, msg.hole_punching_enabled ());
  tox_options_set_savedata_type (opts, Enum::valueOf<Tox_Savedata_Type> (env, msg.savedata_type ()));
  ok = tox_options_set_savedata_data (opts, reinterpret_cast<uint8_t const *> (msg.savedata ().data ()), msg.savedata ().size ()) && ok;
  tox_options_set_experimental_owned_data (opts, msg.experimental_owned_data ());
  tox_options_set_experimental_thread_safety (opts, msg.experimental_thread_safety ());
  tox_options_set_experimental_groups_persistence (opts, msg.experimental_groups_persistence ());
  tox_options_set_experimental_disable_dns (opts, msg.experimental_disable_dns ());
  return ok;
}
