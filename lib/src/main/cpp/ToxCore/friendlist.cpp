#include "ToxCore.h"

using namespace core;


/*
 * Class:     im_tox_tox4j_impl_jni_ToxCoreJni
 * Method:    toxConferenceGetId
 * Signature: (II)[B
 */
JNIEXPORT jbyteArray JNICALL Java_im_tox_tox4j_impl_jni_ToxCoreJni_toxConferenceGetId
  (JNIEnv *env, jclass, jint instanceNumber, jint conferenceNumber)
{
  return instances.with_instance (env, instanceNumber,
    [env, conferenceNumber] (Tox *tox, core::Events &) -> jbyteArray
      {
        uint8_t id[TOX_CONFERENCE_ID_SIZE];
        if (!tox_conference_get_id (tox, conferenceNumber, id))
          {
            // tox_conference_get_id has no error enum.
            throw_tox_exception (env, "core", "", "ConferenceGetId",
              "CONFERENCE_NOT_FOUND");
            return nullptr;
          }
        return toJavaArray (env, id);
      }
  );
}
