#include "ToxCore.h"

#include <algorithm>
#include <vector>

template<>
void
print_arg<Tox *> (protolog::Value &value, Tox *const &tox)
{
  static std::vector<Tox *> ids;
  auto found = std::find (ids.begin (), ids.end (), tox);
  if (found == ids.end ())
    {
      ids.push_back (tox);
      found = ids.end () - 1;
    }
  value.set_v_string ("@" + std::to_string (found - ids.begin () + 1));
}

template<>
void
print_arg<Tox_Options *> (protolog::Value &value, Tox_Options *const &options)
{
  if (options == nullptr)
    value.set_v_string ("<null>");
  else
    {
      protolog::Struct *object = value.mutable_v_object ();
      print_member (*object, "ipv6_enabled", tox_options_get_ipv6_enabled(options));
      print_member (*object, "udp_enabled", tox_options_get_udp_enabled(options));
      print_member (*object, "proxy_type", tox_options_get_proxy_type(options));
      print_member (*object, "proxy_host", tox_options_get_proxy_host(options));
      print_member (*object, "start_port", tox_options_get_start_port(options));
      print_member (*object, "end_port", tox_options_get_end_port(options));
      print_member (*object, "tcp_port", tox_options_get_tcp_port(options));
      print_member (*object, "savedata_type", tox_options_get_savedata_type(options));
      print_member (*object, "savedata",
        tox_options_get_savedata_data(options),
        tox_options_get_savedata_length(options));
    }
}

template<>
void
print_arg<core::Events *> (protolog::Value &value, core::Events *const &events)
{
  if (events == nullptr)
    value.set_v_string ("<null>");
  else
    value.set_v_string ("<core::Events[" + std::to_string (events->ByteSizeLong ()) + "]>");
}

#define enum_case(ENUM)                               \
    case TOX_##ENUM: value.set_v_string ("TOX_" #ENUM); break

template<>
void
print_arg<TOX_FILE_KIND> (protolog::Value &value, TOX_FILE_KIND const &kind)
{
  switch (kind)
    {
    enum_case (FILE_KIND_DATA);
    enum_case (FILE_KIND_AVATAR);
    default:
      value.set_v_string ("(TOX_FILE_KIND)" + std::to_string (kind));
      break;
    }
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    tox4jLastLog
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_tox4jLastLog
  (JNIEnv *env, jclass)
{
  if (jni_log.empty ())
    return nullptr;

  return toJavaArray (env, jni_log.clear ());
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    tox4jSetMaxLogSize
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_tox4jSetMaxLogSize
  (JNIEnv *, jclass, jint maxSize)
{
  jni_log.max_size (maxSize);
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    tox4jGetMaxLogSize
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_tox4jGetMaxLogSize
  (JNIEnv *, jclass)
{
  return jni_log.max_size ();
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    tox4jGetCurrentLogSize
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_tox4jGetCurrentLogSize
  (JNIEnv *, jclass)
{
  return jni_log.size ();
}

/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    tox4jSetLogFilter
 * Signature: ([Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_tox4jSetLogFilter
  (JNIEnv *env, jclass, jobjectArray filters)
{
  int stringCount = env->GetArrayLength (filters);

  std::vector<std::string> filter_strings;
  for (int i = 0; i < stringCount; i++)
    {
      UTFChars filter (env, static_cast<jstring> (env->GetObjectArrayElement(filters, i)));
      filter_strings.push_back (filter.to_string ());
    }

  jni_log.filter (std::move (filter_strings));
}
