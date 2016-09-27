// Instance manager, JNI utilities.
#include "tox4j/Tox4j.h"

// Protobuf classes.
#include "Av.pb.h"

// JNI declarations from javah.
#include "im_tox_tox4j_impl_jni_ToxAvJni.h"

// Header from toxcore.
#include <tox/av.h>

#ifndef SUBSYSTEM
#define SUBSYSTEM TOXAV
#define CLASS     ToxAv
#define PREFIX    toxav
#endif

namespace av
{
  namespace proto = im::tox::tox4j::av::proto;

  using Events = proto::AvEvents;

  extern ToxInstances<tox::av_ptr, std::unique_ptr<Events>> instances;
}
