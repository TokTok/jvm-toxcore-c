#include <memory>

#include "ToxCore.h"

using namespace core;


template<typename Message>
static void
set_connection_status (Message &msg, TOX_CONNECTION connection_status)
{
  using proto::Connection;
  switch (connection_status)
    {
    case TOX_CONNECTION_NONE:
      msg->set_connection_status (Connection::NONE);
      break;
    case TOX_CONNECTION_TCP:
      msg->set_connection_status (Connection::TCP);
      break;
    case TOX_CONNECTION_UDP:
      msg->set_connection_status (Connection::UDP);
      break;
    }
}

static void
tox4j_self_connection_status_cb (TOX_CONNECTION connection_status, Events *events)
{
  auto msg = events->add_self_connection_status ();
  set_connection_status (msg, connection_status);
}

static void
tox4j_friend_name_cb (uint32_t friend_number, uint8_t const *name, size_t length, Events *events)
{
  auto msg = events->add_friend_name ();
  msg->set_friend_number (friend_number);
  msg->set_name (name, length);
}

static void
tox4j_friend_status_message_cb (uint32_t friend_number, uint8_t const *message, size_t length, Events *events)
{
  auto msg = events->add_friend_status_message ();
  msg->set_friend_number (friend_number);
  msg->set_message (message, length);
}

static void
tox4j_friend_status_cb (uint32_t friend_number, TOX_USER_STATUS status, Events *events)
{
  auto msg = events->add_friend_status ();
  msg->set_friend_number (friend_number);

  using proto::UserStatus;
  switch (status)
    {
    case TOX_USER_STATUS_NONE:
      msg->set_status (UserStatus::NONE);
      break;
    case TOX_USER_STATUS_AWAY:
      msg->set_status (UserStatus::AWAY);
      break;
    case TOX_USER_STATUS_BUSY:
      msg->set_status (UserStatus::BUSY);
      break;
    }
}

static void
tox4j_friend_connection_status_cb (uint32_t friend_number, TOX_CONNECTION connection_status, Events *events)
{
  auto msg = events->add_friend_connection_status ();
  msg->set_friend_number (friend_number);
  set_connection_status (msg, connection_status);
}

static void
tox4j_friend_typing_cb (uint32_t friend_number, bool is_typing, Events *events)
{
  auto msg = events->add_friend_typing ();
  msg->set_friend_number (friend_number);
  msg->set_is_typing (is_typing);
}

static void
tox4j_friend_read_receipt_cb (uint32_t friend_number, uint32_t message_id, Events *events)
{
  auto msg = events->add_friend_read_receipt ();
  msg->set_friend_number (friend_number);
  msg->set_message_id (message_id);
}

static void
tox4j_friend_request_cb (uint8_t const *public_key, /*uint32_t time_delta, */ uint8_t const *message, size_t length, Events *events)
{
  auto msg = events->add_friend_request ();
  msg->set_public_key (public_key, TOX_PUBLIC_KEY_SIZE);
  msg->set_time_delta (0);
  msg->set_message (message, length);
}

static void
tox4j_friend_message_cb (uint32_t friend_number, TOX_MESSAGE_TYPE type, /*uint32_t time_delta, */ uint8_t const *message, size_t length, Events *events)
{
  auto msg = events->add_friend_message ();
  msg->set_friend_number (friend_number);

  using proto::MessageType;
  switch (type)
    {
    case TOX_MESSAGE_TYPE_NORMAL:
      msg->set_type (MessageType::NORMAL);
      break;
    case TOX_MESSAGE_TYPE_ACTION:
      msg->set_type (MessageType::ACTION);
      break;
    }

  msg->set_time_delta (0);
  msg->set_message (message, length);
}

static void
tox4j_file_recv_control_cb (uint32_t friend_number, uint32_t file_number, TOX_FILE_CONTROL control, Events *events)
{
  auto msg = events->add_file_recv_control ();
  msg->set_friend_number (friend_number);
  msg->set_file_number (file_number);

  using proto::FileControl;
  switch (control)
    {
    case TOX_FILE_CONTROL_RESUME:
      msg->set_control (FileControl::RESUME);
      break;
    case TOX_FILE_CONTROL_PAUSE:
      msg->set_control (FileControl::PAUSE);
      break;
    case TOX_FILE_CONTROL_CANCEL:
      msg->set_control (FileControl::CANCEL);
      break;
    }
}

static void
tox4j_file_chunk_request_cb (uint32_t friend_number, uint32_t file_number, uint64_t position, size_t length, Events *events)
{
  auto msg = events->add_file_chunk_request ();
  msg->set_friend_number (friend_number);
  msg->set_file_number (file_number);
  msg->set_position (position);
  msg->set_length (length);
}

static void
tox4j_file_recv_cb (uint32_t friend_number, uint32_t file_number, uint32_t kind, uint64_t file_size, uint8_t const *filename, size_t filename_length, Events *events)
{
  auto msg = events->add_file_recv ();
  msg->set_friend_number (friend_number);
  msg->set_file_number (file_number);
  msg->set_kind (kind);
  msg->set_file_size (file_size);
  msg->set_filename (filename, filename_length);
}

static void
tox4j_file_recv_chunk_cb (uint32_t friend_number, uint32_t file_number, uint64_t position, uint8_t const *data, size_t length, Events *events)
{
  auto msg = events->add_file_recv_chunk ();
  msg->set_friend_number (friend_number);
  msg->set_file_number (file_number);
  msg->set_position (position);
  msg->set_data (data, length);
}

static void
tox4j_friend_lossy_packet_cb (uint32_t friend_number, uint8_t const *data, size_t length, Events *events)
{
  auto msg = events->add_friend_lossy_packet ();
  msg->set_friend_number (friend_number);
  msg->set_data (data, length);
}

static void
tox4j_friend_lossless_packet_cb (uint32_t friend_number, uint8_t const *data, size_t length, Events *events)
{
  auto msg = events->add_friend_lossless_packet ();
  msg->set_friend_number (friend_number);
  msg->set_data (data, length);
}


static auto
tox_options_new_unique ()
{
  struct Tox_Options_Deleter
  {
    void operator () (Tox_Options *options)
    {
      tox_options_free (options);
    }
  };

  return std::unique_ptr<Tox_Options, Tox_Options_Deleter> (tox_options_new (nullptr));
}


static tox::core_ptr
tox_new_unique (Tox_Options const *options, TOX_ERR_NEW *error)
{
  return tox::core_ptr (tox_new (options, error));
}


/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxNew
 * Signature: (ZZILjava/lang/String;IIII)I
 */
TOX_METHOD (jint, New,
  jboolean ipv6Enabled, jboolean udpEnabled, jboolean localDiscoveryEnabled,
  jint proxyType, jstring proxyHost, jint proxyPort,
  jint startPort, jint endPort, jint tcpPort,
  jint saveDataType, jbyteArray saveData)
{
#if 0
  scope_guard {
    [&]{ printf ("creating new instance"); },
  };
#endif

  auto opts = tox_options_new_unique ();
  if (!opts)
    {
      throw_tox_exception<Tox> (env, TOX_ERR_NEW_MALLOC);
      return 0;
    }

  tox_options_set_ipv6_enabled (opts.get (), ipv6Enabled);
  tox_options_set_udp_enabled (opts.get (), udpEnabled);
  tox_options_set_local_discovery_enabled (opts.get (), localDiscoveryEnabled);

  tox_options_set_proxy_type (opts.get (), Enum::valueOf<TOX_PROXY_TYPE> (env, proxyType));
  UTFChars proxy_host (env, proxyHost);
  tox_options_set_proxy_host (opts.get (), proxy_host.data ());
  tox_options_set_proxy_port (opts.get (), proxyPort);

  tox_options_set_start_port (opts.get (), startPort);
  tox_options_set_end_port (opts.get (), endPort);
  tox_options_set_tcp_port (opts.get (), tcpPort);

  auto assert_valid_uint16 = [env](int port) {
    tox4j_assert (port >= 0);
    tox4j_assert (port <= 65535);
  };
  if (tox_options_get_proxy_type (opts.get ()) != TOX_PROXY_TYPE_NONE)
    assert_valid_uint16 (proxyPort);
  assert_valid_uint16 (startPort);
  assert_valid_uint16 (endPort);
  assert_valid_uint16 (tcpPort);

  auto save_data = fromJavaArray (env, saveData);
  tox_options_set_savedata_type (opts.get (), Enum::valueOf<TOX_SAVEDATA_TYPE> (env, saveDataType));
  tox_options_set_savedata_data (opts.get (), save_data.data (), save_data.size ());

  return instances.with_error_handling (env,
    [env] (tox::core_ptr tox)
      {
        tox4j_assert (tox != nullptr);

        // Create the master events object and set up our callbacks.
        auto events = tox::callbacks<Tox> (std::make_unique<Events> ())
#define CALLBACK(NAME)   .set<tox::callback_##NAME, tox4j_##NAME##_cb> ()
#include "tox/generated/core.h"
#undef CALLBACK
          .set (tox.get ());

        // We can create the new instance outside instance_manager's critical section.
        // This call locks the instance manager.
        return instances.add (
          env,
          std::move (tox),
          std::move (events)
        );
      },
    tox_new_unique, opts.get ()
  );
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxKill
 * Signature: (I)I
 */
TOX_METHOD (void, Kill,
  jint instanceNumber)
{
  instances.kill (env, instanceNumber);
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxFinalize
 * Signature: (I)V
 */
TOX_METHOD (void, Finalize,
  jint instanceNumber)
{
  instances.finalize (env, instanceNumber);
}

/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxGetSavedata
 * Signature: (I)[B
 */
TOX_METHOD (jbyteArray, GetSavedata,
  jint instanceNumber)
{
  return instances.with_instance_noerr (env, instanceNumber,
    get_vector<uint8_t,
      tox_get_savedata_size,
      tox_get_savedata>::make
  );
}
