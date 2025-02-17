#include <memory>

#include "ToxCore.h"

using namespace core;


template<typename Message>
static void
set_connection_status (Message *msg, Tox_Connection connection_status)
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
tox4j_self_connection_status_cb (Tox_Connection connection_status, Events *events)
{
  auto msg = events->add_events()->mutable_self_connection_status();
  set_connection_status(msg, connection_status);
}

static void
tox4j_friend_name_cb (Tox_Friend_Number friend_number, uint8_t const *name, size_t length, Events *events)
{
  auto msg = events->add_events()->mutable_friend_name ();
  msg->set_friend_number (friend_number);
  msg->set_name (name, length);
}

static void
tox4j_friend_status_message_cb (Tox_Friend_Number friend_number, uint8_t const *message, size_t length, Events *events)
{
  auto msg = events->add_events()->mutable_friend_status_message ();
  msg->set_friend_number (friend_number);
  msg->set_message (message, length);
}

template<typename Message>
static void set_user_status(Message *msg, Tox_User_Status status) {
  using proto::UserStatus;
  switch (status) {
    case TOX_USER_STATUS_NONE:
      msg->set_status(UserStatus::NONE);
      break;
    case TOX_USER_STATUS_AWAY:
      msg->set_status(UserStatus::AWAY);
      break;
    case TOX_USER_STATUS_BUSY:
      msg->set_status(UserStatus::BUSY);
      break;
  }
}

static void
tox4j_friend_status_cb (Tox_Friend_Number friend_number, Tox_User_Status status, Events *events)
{
  auto msg = events->add_events()->mutable_friend_status ();
  msg->set_friend_number (friend_number);
  set_user_status(msg, status);
}

static void
tox4j_friend_connection_status_cb (Tox_Friend_Number friend_number, Tox_Connection connection_status, Events *events)
{
  auto msg = events->add_events()->mutable_friend_connection_status ();
  msg->set_friend_number (friend_number);
  set_connection_status (msg, connection_status);
}

static void
tox4j_friend_typing_cb (Tox_Friend_Number friend_number, bool is_typing, Events *events)
{
  auto msg = events->add_events()->mutable_friend_typing ();
  msg->set_friend_number (friend_number);
  msg->set_is_typing (is_typing);
}

static void
tox4j_friend_read_receipt_cb (Tox_Friend_Number friend_number, Tox_Friend_Message_Id message_id, Events *events)
{
  auto msg = events->add_events()->mutable_friend_read_receipt ();
  msg->set_friend_number (friend_number);
  msg->set_message_id (message_id);
}

static void
tox4j_friend_request_cb (uint8_t const *public_key, uint8_t const *message, size_t length, Events *events)
{
  auto msg = events->add_events()->mutable_friend_request ();
  msg->set_public_key (public_key, TOX_PUBLIC_KEY_SIZE);
  msg->set_message (message, length);
}

template<typename Message>
static void set_message_type(Message *msg, Tox_Message_Type type) {
  using proto::MessageType;
  switch (type) {
    case TOX_MESSAGE_TYPE_NORMAL:
      msg->set_message_type(MessageType::NORMAL);
      break;
    case TOX_MESSAGE_TYPE_ACTION:
      msg->set_message_type(MessageType::ACTION);
      break;
  }
}

static void tox4j_friend_message_cb(Tox_Friend_Number friend_number, Tox_Message_Type type, uint8_t const* message, size_t length, Events* events) {
  auto msg = events->add_events()->mutable_friend_message();
  msg->set_friend_number(friend_number);
  set_message_type(msg, type);
  msg->set_message(message, length);
}

static void
tox4j_file_recv_control_cb (Tox_Friend_Number friend_number, Tox_File_Number file_number, Tox_File_Control control, Events *events)
{
  auto msg = events->add_events()->mutable_file_recv_control ();
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
tox4j_file_chunk_request_cb (Tox_Friend_Number friend_number, Tox_File_Number file_number, uint64_t position, size_t length, Events *events)
{
  auto msg = events->add_events()->mutable_file_chunk_request ();
  msg->set_friend_number (friend_number);
  msg->set_file_number (file_number);
  msg->set_position (position);
  msg->set_length (length);
}

static void
tox4j_file_recv_cb (Tox_Friend_Number friend_number, Tox_File_Number file_number, uint32_t kind, uint64_t file_size, uint8_t const *filename, size_t filename_length, Events *events)
{
  auto msg = events->add_events()->mutable_file_recv ();
  msg->set_friend_number (friend_number);
  msg->set_file_number (file_number);
  msg->set_kind (kind);
  msg->set_file_size (file_size);
  msg->set_filename (filename, filename_length);
}

static void
tox4j_file_recv_chunk_cb (Tox_Friend_Number friend_number, Tox_File_Number file_number, uint64_t position, uint8_t const *data, size_t length, Events *events)
{
  auto msg = events->add_events()->mutable_file_recv_chunk ();
  msg->set_friend_number (friend_number);
  msg->set_file_number (file_number);
  msg->set_position (position);
  msg->set_data (data, length);
}

static void
tox4j_friend_lossy_packet_cb (Tox_Friend_Number friend_number, uint8_t const *data, size_t length, Events *events)
{
  auto msg = events->add_events()->mutable_friend_lossy_packet ();
  msg->set_friend_number (friend_number);
  msg->set_data (data, length);
}

static void
tox4j_friend_lossless_packet_cb (Tox_Friend_Number friend_number, uint8_t const *data, size_t length, Events *events)
{
  auto msg = events->add_events()->mutable_friend_lossless_packet ();
  msg->set_friend_number (friend_number);
  msg->set_data (data, length);
}

static void
tox4j_conference_invite_cb (Tox_Friend_Number friend_number, Tox_Conference_Type type, uint8_t const *cookie, size_t length, Events *events)
{
  auto msg = events->add_events()->mutable_conference_invite ();
  msg->set_friend_number (friend_number);

  using proto::ConferenceType;
  switch (type)
    {
    case TOX_CONFERENCE_TYPE_TEXT:
      msg->set_type (ConferenceType::TEXT);
      break;
    case TOX_CONFERENCE_TYPE_AV:
      msg->set_type (ConferenceType::AV);
      break;
    }

  msg->set_cookie (cookie, length);
}

static void
tox4j_conference_connected_cb (Tox_Conference_Number conference_number, Events *events)
{
  auto msg = events->add_events()->mutable_conference_connected();
  msg->set_conference_number (conference_number);
}

static void
tox4j_conference_message_cb (Tox_Conference_Number conference_number, Tox_Conference_Peer_Number peer_number, Tox_Message_Type type, uint8_t const *message, size_t length, Events *events)
{
  auto msg = events->add_events()->mutable_conference_message();
  msg->set_conference_number (conference_number);
  msg->set_peer_number (peer_number);
  set_message_type (msg, type);
  msg->set_message (message, length);
}

static void
tox4j_conference_title_cb (Tox_Conference_Number conference_number, Tox_Conference_Peer_Number peer_number, uint8_t const *title, size_t length, Events *events)
{
  auto msg = events->add_events()->mutable_conference_title();
  msg->set_conference_number (conference_number);
  msg->set_peer_number (peer_number);
  msg->set_title (title, length);
}

static void
tox4j_conference_peer_name_cb (Tox_Conference_Number conference_number, Tox_Conference_Peer_Number peer_number, uint8_t const *name, size_t length, Events *events)
{
  auto msg = events->add_events()->mutable_conference_peer_name();
  msg->set_conference_number (conference_number);
  msg->set_peer_number (peer_number);
  msg->set_name (name, length);
}

static void
tox4j_conference_peer_list_changed_cb (Tox_Conference_Number conference_number, Events *events)
{
  auto msg = events->add_events()->mutable_conference_peer_list_changed();
  msg->set_conference_number (conference_number);
}

static void
tox4j_group_peer_name_cb (Tox_Group_Number group_number, Tox_Group_Peer_Number peer_id, uint8_t const *name, size_t name_length, Events *events)
{
  auto msg = events->add_events()->mutable_group_peer_name();
  msg->set_group_number (group_number);
  msg->set_peer_id (peer_id);
  msg->set_name (name, name_length);
}

static void
tox4j_group_peer_status_cb (Tox_Group_Number group_number, Tox_Group_Peer_Number peer_id, Tox_User_Status status, Events *events)
{
  auto msg = events->add_events()->mutable_group_peer_status();
  msg->set_group_number (group_number);
  msg->set_peer_id (peer_id);
  set_user_status(msg, status);
}

static void
tox4j_group_topic_cb (Tox_Group_Number group_number, Tox_Group_Peer_Number peer_id, uint8_t const *topic, size_t topic_length, Events *events)
{
  auto msg = events->add_events()->mutable_group_topic();
  msg->set_group_number (group_number);
  msg->set_peer_id (peer_id);
  msg->set_topic (topic, topic_length);
}

static void
tox4j_group_privacy_state_cb (Tox_Group_Number group_number, Tox_Group_Privacy_State privacy_state, Events *events)
{
  auto msg = events->add_events()->mutable_group_privacy_state();
  msg->set_group_number (group_number);

  using proto::GroupPrivacyState;
  switch (privacy_state)
    {
    case TOX_GROUP_PRIVACY_STATE_PUBLIC:
      msg->set_privacy_state (GroupPrivacyState::PUBLIC);
      break;
    case TOX_GROUP_PRIVACY_STATE_PRIVATE:
      msg->set_privacy_state (GroupPrivacyState::PRIVATE);
      break;
    }
}

static void
tox4j_group_voice_state_cb (uint32_t group_number, Tox_Group_Voice_State voice_state, Events *events)
{
  auto msg = events->add_events()->mutable_group_voice_state();
  msg->set_group_number (group_number);

  using proto::GroupVoiceState;
  switch (voice_state)
    {
    case TOX_GROUP_VOICE_STATE_ALL:
      msg->set_voice_state (GroupVoiceState::ALL);
      break;
    case TOX_GROUP_VOICE_STATE_MODERATOR:
      msg->set_voice_state (GroupVoiceState::MODERATOR);
      break;
    case TOX_GROUP_VOICE_STATE_FOUNDER:
      msg->set_voice_state (GroupVoiceState::FOUNDER);
      break;
    }
}

static void
tox4j_group_topic_lock_cb (uint32_t group_number, Tox_Group_Topic_Lock topic_lock, Events *events)
{
  auto msg = events->add_events()->mutable_group_topic_lock();
  msg->set_group_number (group_number);

  using proto::GroupTopicLock;
  switch (topic_lock)
    {
    case TOX_GROUP_TOPIC_LOCK_ENABLED:
      msg->set_topic_lock (GroupTopicLock::ENABLED);
      break;
    case TOX_GROUP_TOPIC_LOCK_DISABLED:
      msg->set_topic_lock (GroupTopicLock::DISABLED);
      break;
    }
}

static void
tox4j_group_peer_limit_cb (uint32_t group_number, uint32_t peer_limit, Events *events)
{
  auto msg = events->add_events()->mutable_group_peer_limit();
  msg->set_group_number (group_number);
  msg->set_peer_limit (peer_limit);
}

static void
tox4j_group_password_cb (uint32_t group_number, uint8_t const *password, size_t password_length, Events *events)
{
  auto msg = events->add_events()->mutable_group_password();
  msg->set_group_number (group_number);
  msg->set_password (password, password_length);
}

static void
tox4j_group_message_cb (Tox_Group_Number group_number, Tox_Group_Peer_Number peer_id, Tox_Message_Type message_type, uint8_t const *message, size_t message_length, Tox_Group_Message_Id message_id, Events *events)
{
  auto msg = events->add_events()->mutable_group_message();
  msg->set_group_number (group_number);
  msg->set_peer_id (peer_id);
  set_message_type (msg, message_type);
  msg->set_message (message, message_length);
  msg->set_message_id (message_id);
}

static void
tox4j_group_private_message_cb (Tox_Group_Number group_number, Tox_Group_Peer_Number peer_id, Tox_Message_Type message_type, uint8_t const *message, size_t message_length, Tox_Group_Message_Id message_id, Events *events)
{
  auto msg = events->add_events()->mutable_group_private_message();
  msg->set_group_number (group_number);
  msg->set_peer_id (peer_id);
  set_message_type (msg, message_type);
  msg->set_message (message, message_length);
  msg->set_message_id (message_id);
}

static void
tox4j_group_custom_packet_cb (Tox_Group_Number group_number, Tox_Group_Peer_Number peer_id, uint8_t const *data, size_t data_length, Events *events)
{
  auto msg = events->add_events()->mutable_group_custom_packet();
  msg->set_group_number (group_number);
  msg->set_peer_id (peer_id);
  msg->set_data (data, data_length);
}

static void
tox4j_group_custom_private_packet_cb (Tox_Group_Number group_number, Tox_Group_Peer_Number peer_id, uint8_t const *data, size_t data_length, Events *events)
{
  auto msg = events->add_events()->mutable_group_custom_private_packet();
  msg->set_group_number (group_number);
  msg->set_peer_id (peer_id);
  msg->set_data (data, data_length);
}

static void
tox4j_group_invite_cb (Tox_Friend_Number friend_number, uint8_t const *invite_data, size_t invite_data_length, uint8_t const *group_name, size_t group_name_length, Events *events)
{
  auto msg = events->add_events()->mutable_group_invite();
  msg->set_friend_number (friend_number);
  msg->set_invite_data (invite_data, invite_data_length);
  msg->set_group_name (group_name, group_name_length);
}

static void
tox4j_group_peer_join_cb (Tox_Group_Number group_number, Tox_Group_Peer_Number peer_id, Events *events)
{
  auto msg = events->add_events()->mutable_group_peer_join();
  msg->set_group_number (group_number);
  msg->set_peer_id (peer_id);
}

static void
tox4j_group_peer_exit_cb (Tox_Group_Number group_number, Tox_Group_Peer_Number peer_id, Tox_Group_Exit_Type exit_type, uint8_t const *name, size_t name_length, uint8_t const *part_message, size_t part_message_length, Events *events)
{
  auto msg = events->add_events()->mutable_group_peer_exit();
  msg->set_group_number (group_number);
  msg->set_peer_id (peer_id);

  using proto::GroupExitType;
  switch (exit_type)
    {
    case TOX_GROUP_EXIT_TYPE_QUIT:
      msg->set_exit_type (GroupExitType::QUIT);
      break;
    case TOX_GROUP_EXIT_TYPE_TIMEOUT:
      msg->set_exit_type (GroupExitType::TIMEOUT);
      break;
    case TOX_GROUP_EXIT_TYPE_DISCONNECTED:
      msg->set_exit_type (GroupExitType::DISCONNECTED);
      break;
    case TOX_GROUP_EXIT_TYPE_SELF_DISCONNECTED:
      msg->set_exit_type (GroupExitType::SELF_DISCONNECTED);
      break;
    case TOX_GROUP_EXIT_TYPE_KICK:
      msg->set_exit_type (GroupExitType::KICK);
      break;
    case TOX_GROUP_EXIT_TYPE_SYNC_ERROR:
      msg->set_exit_type (GroupExitType::SYNC_ERROR);
      break;
    }

  msg->set_name (name, name_length);
  msg->set_part_message (part_message, part_message_length);
}

static void
tox4j_group_self_join_cb (Tox_Group_Number group_number, Events *events)
{
  auto msg = events->add_events()->mutable_group_self_join();
  msg->set_group_number (group_number);
}

static void
tox4j_group_join_fail_cb (Tox_Group_Number group_number, Tox_Group_Join_Fail fail_type, Events *events)
{
  auto msg = events->add_events()->mutable_group_join_fail();
  msg->set_group_number (group_number);

  using proto::GroupJoinFail;
  switch (fail_type)
    {
    case TOX_GROUP_JOIN_FAIL_PEER_LIMIT:
      msg->set_fail_type (GroupJoinFail::PEER_LIMIT);
      break;
    case TOX_GROUP_JOIN_FAIL_INVALID_PASSWORD:
      msg->set_fail_type (GroupJoinFail::INVALID_PASSWORD);
      break;
    case TOX_GROUP_JOIN_FAIL_UNKNOWN:
      msg->set_fail_type (GroupJoinFail::UNKNOWN);
      break;
    }
}

static void
tox4j_group_moderation_cb (Tox_Group_Number group_number, Tox_Group_Peer_Number source_peer_id, Tox_Group_Peer_Number target_peer_id, Tox_Group_Mod_Event mod_type, Events *events)
{
  auto msg = events->add_events()->mutable_group_moderation();
  msg->set_group_number (group_number);
  msg->set_source_peer_id (source_peer_id);
  msg->set_target_peer_id (target_peer_id);

  using proto::GroupModEvent;
  switch (mod_type)
    {
    case TOX_GROUP_MOD_EVENT_KICK:
      msg->set_mod_type (GroupModEvent::KICK);
      break;
    case TOX_GROUP_MOD_EVENT_OBSERVER:
      msg->set_mod_type (GroupModEvent::OBSERVER);
      break;
    case TOX_GROUP_MOD_EVENT_USER:
      msg->set_mod_type (GroupModEvent::USER);
      break;
    case TOX_GROUP_MOD_EVENT_MODERATOR:
      msg->set_mod_type (GroupModEvent::MODERATOR);
      break;
    }
}


static auto
tox_options_new_unique ()
{
  struct Tox_Options_Deleter
  {
    [[maybe_unused]]
    void operator () (Tox_Options *options)
    {
      tox_options_free (options);
    }
  };

  return std::unique_ptr<Tox_Options, Tox_Options_Deleter> (tox_options_new (nullptr));
}


static tox::core_ptr
tox_new_unique (Tox_Options const *options, Tox_Err_New *error)
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
  auto opts = tox_options_new_unique ();
  if (!opts)
    {
      throw_tox_exception<Tox> (env, TOX_ERR_NEW_MALLOC);
      return 0;
    }

  tox_options_set_ipv6_enabled (opts.get (), ipv6Enabled);
  tox_options_set_udp_enabled (opts.get (), udpEnabled);
  tox_options_set_local_discovery_enabled (opts.get (), localDiscoveryEnabled);

  tox_options_set_proxy_type (opts.get (), Enum::valueOf<Tox_Proxy_Type> (env, proxyType));
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
  if (tox_options_get_proxy_type (opts.get ()) != TOX_PROXY_TYPE_NONE) {
    assert_valid_uint16 (proxyPort);
  }
  assert_valid_uint16 (startPort);
  assert_valid_uint16 (endPort);
  assert_valid_uint16 (tcpPort);

  auto save_data = fromJavaArray (env, saveData);
  tox_options_set_savedata_type (opts.get (), Enum::valueOf<Tox_Savedata_Type> (env, saveDataType));
  tox_options_set_savedata (opts.get (), save_data.data (), save_data.size ());

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
