// Instance manager, JNI utilities.
#include "tox4j/Tox4j.h"

// Protobuf classes.
#include "Core.pb.h"

// JNI declarations from javah.
#include "im_tox_tox4j_impl_jni_ToxCoreJni.h"

// Header from toxcore.
#include <tox/core.h>

#ifndef SUBSYSTEM
#define SUBSYSTEM TOX
#define CLASS     ToxCore
#define PREFIX    tox
#endif

#ifdef TOX_VERSION_MAJOR
namespace core
{
  namespace proto = im::tox::tox4j::core::proto;

  using Events = proto::CoreEvents;

  extern ToxInstances<tox::core_ptr, std::unique_ptr<Events>> instances;
}
#endif


template<typename T, size_t get_size (Tox const *), void get_data (Tox const *, T *)>
auto
get_vector (Tox const *tox, JNIEnv *env)
{
  std::vector<T> name (get_size (tox));
  get_data (tox, name.data ());

  return toJavaArray (env, name);
}


template<std::size_t N>
std::size_t
constant_size (Tox const *)
{
  return N;
}
