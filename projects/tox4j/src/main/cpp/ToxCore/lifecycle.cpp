#include "ToxCore.h"

#ifdef TOX_VERSION_MAJOR

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


static void
tox_finalize ()
{
  assert (!"This function is only here for register_funcs and should never be called.");
}


REGISTER_FUNCS (
#define JAVA_METHOD_REF(x)
#define CXX_FUNCTION_REF(func)  REGISTER_FUNC (func),
#include "generated/natives.h"
#undef CXX_FUNCTION_REF
#undef JAVA_METHOD_REF

#define CALLBACK(NAME)          REGISTER_FUNC (tox4j_##NAME##_cb),
#include "tox/generated/core.h"
#undef CALLBACK

  REGISTER_FUNC (tox_new_unique)
);


/*
 * Class:     im_tox_tox4j_impl_ToxCoreJni
 * Method:    toxNew
 * Signature: (ZZILjava/lang/String;IIII)I
 */
TOX_METHOD (jint, New,
  jboolean ipv6Enabled, jboolean udpEnabled,
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

  opts->ipv6_enabled = ipv6Enabled;
  opts->udp_enabled = udpEnabled;

  opts->proxy_type = Enum::valueOf<TOX_PROXY_TYPE> (env, proxyType);
  UTFChars proxy_host (env, proxyHost);
  opts->proxy_host = proxy_host.data ();
  opts->proxy_port = proxyPort;

  opts->start_port = startPort;
  opts->end_port = endPort;
  opts->tcp_port = tcpPort;

  auto assert_valid_uint16 = [env](int port) {
    tox4j_assert (port >= 0);
    tox4j_assert (port <= 65535);
  };
  if (opts->proxy_type != TOX_PROXY_TYPE_NONE)
    assert_valid_uint16 (proxyPort);
  assert_valid_uint16 (startPort);
  assert_valid_uint16 (endPort);
  assert_valid_uint16 (tcpPort);

  auto save_data = fromJavaArray (env, saveData);
  opts->savedata_type = Enum::valueOf<TOX_SAVEDATA_TYPE> (env, saveDataType);
  opts->savedata_data   = save_data.data ();
  opts->savedata_length = save_data.size ();

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
      tox_get_savedata>::make,
    env
  );
}


/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeSelfConnectionStatus
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeSelfConnectionStatus
  (JNIEnv *env, jclass, jint instanceNumber, jint connection_status)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        assert (tox != nullptr);
        tox4j_self_connection_status_cb (Enum::valueOf<TOX_CONNECTION> (env, connection_status), &events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeFileRecvControl
 * Signature: (IIII)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeFileRecvControl
  (JNIEnv *env, jclass, jint instanceNumber, jint friend_number, jint file_number, jint control)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        assert (tox != nullptr);
        tox4j_file_recv_control_cb (friend_number, file_number, Enum::valueOf<TOX_FILE_CONTROL> (env, control), &events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeFileRecv
 * Signature: (IIIIJ[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeFileRecv
  (JNIEnv *env, jclass, jint instanceNumber, jint friend_number, jint file_number, jint kind, jlong file_size, jbyteArray filename)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        assert (tox != nullptr);
        auto filenameArray = fromJavaArray (env, filename);
        tox4j_file_recv_cb (friend_number, file_number, kind, file_size, filenameArray.data (), filenameArray.size (), &events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeFileRecvChunk
 * Signature: (IIIJ[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeFileRecvChunk
  (JNIEnv *env, jclass, jint instanceNumber, jint friend_number, jint file_number, jlong position, jbyteArray data)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        assert (tox != nullptr);
        auto dataArray = fromJavaArray (env, data);
        tox4j_file_recv_chunk_cb (friend_number, file_number, position, dataArray.data (), dataArray.size (), &events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeFileChunkRequest
 * Signature: (IIIJI)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeFileChunkRequest
  (JNIEnv *env, jclass, jint instanceNumber, jint friend_number, jint file_number, jlong position, jint length)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        assert (tox != nullptr);
        tox4j_file_chunk_request_cb (friend_number, file_number, position, length, &events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeFriendConnectionStatus
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeFriendConnectionStatus
  (JNIEnv *env, jclass, jint instanceNumber, jint friend_number, jint connection_status)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        assert (tox != nullptr);
        tox4j_friend_connection_status_cb (friend_number, Enum::valueOf<TOX_CONNECTION> (env, connection_status), &events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeFriendLosslessPacket
 * Signature: (II[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeFriendLosslessPacket
  (JNIEnv *env, jclass, jint instanceNumber, jint friend_number, jbyteArray data)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        assert (tox != nullptr);
        auto dataArray = fromJavaArray (env, data);
        tox4j_friend_lossless_packet_cb (friend_number, dataArray.data (), dataArray.size (), &events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeFriendLossyPacket
 * Signature: (II[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeFriendLossyPacket
  (JNIEnv *env, jclass, jint instanceNumber, jint friend_number, jbyteArray data)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        assert (tox != nullptr);
        auto dataArray = fromJavaArray (env, data);
        tox4j_friend_lossy_packet_cb (friend_number, dataArray.data (), dataArray.size (), &events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeFriendMessage
 * Signature: (IIII[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeFriendMessage
  (JNIEnv *env, jclass, jint instanceNumber, jint friend_number, jint type, jint time_delta, jbyteArray message)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        assert (tox != nullptr);
        auto messageArray = fromJavaArray (env, message);
        tox4j_friend_message_cb (friend_number, Enum::valueOf<TOX_MESSAGE_TYPE> (env, type), /*time_delta, */ messageArray.data (), messageArray.size (), &events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeFriendName
 * Signature: (II[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeFriendName
  (JNIEnv *env, jclass, jint instanceNumber, jint friend_number, jbyteArray name)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        assert (tox != nullptr);
        auto nameArray = fromJavaArray (env, name);
        tox4j_friend_name_cb (friend_number, nameArray.data (), nameArray.size (), &events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeFriendRequest
 * Signature: (I[BI[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeFriendRequest
  (JNIEnv *env, jclass, jint instanceNumber, jbyteArray public_key, jint time_delta, jbyteArray message)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        assert (tox != nullptr);
        auto public_keyArray = fromJavaArray (env, public_key);
        auto messageArray = fromJavaArray (env, message);
        tox4j_friend_request_cb (public_keyArray.data (), /*time_delta, */ messageArray.data (), messageArray.size (), &events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeFriendStatus
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeFriendStatus
  (JNIEnv *env, jclass, jint instanceNumber, jint friend_number, jint status)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        assert (tox != nullptr);
        tox4j_friend_status_cb (friend_number, Enum::valueOf<TOX_USER_STATUS> (env, status), &events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeFriendStatusMessage
 * Signature: (II[B)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeFriendStatusMessage
  (JNIEnv *env, jclass, jint instanceNumber, jint friend_number, jbyteArray message)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        assert (tox != nullptr);
        auto messageArray = fromJavaArray (env, message);
        tox4j_friend_status_message_cb (friend_number, messageArray.data (), messageArray.size (), &events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeFriendTyping
 * Signature: (IIZ)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeFriendTyping
  (JNIEnv *env, jclass, jint instanceNumber, jint friend_number, jboolean is_typing)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        assert (tox != nullptr);
        tox4j_friend_typing_cb (friend_number, is_typing, &events);
      }
  );
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    invokeFriendReadReceipt
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_invokeFriendReadReceipt
  (JNIEnv *env, jclass, jint instanceNumber, jint friend_number, jint message_id)
{
  return instances.with_instance (env, instanceNumber,
    [=] (Tox *tox, Events &events)
      {
        assert (tox != nullptr);
        tox4j_friend_read_receipt_cb (friend_number, message_id, &events);
      }
  );
}

#endif
