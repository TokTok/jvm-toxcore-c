// Instance manager, JNI utilities.
#include "tox4j/Tox4j.h"

// Protobuf classes.
#include "Core.pb.h"

// JNI declarations from javah.
#include "generated/im_tox_tox4j_impl_jni_ToxCoreJni.h"

// Header from toxcore.
#include <tox/core.h>

#ifndef SUBSYSTEM
#define SUBSYSTEM TOX
#define CLASS     ToxCore
#define PREFIX    tox
#endif

void reference_symbols_core ();

namespace core
{
  namespace proto = im::tox::tox4j::core::proto;

  using Events = proto::CoreEvents;

  extern ToxInstances<tox::core_ptr, std::unique_ptr<Events>> instances;
}


template<typename ConvertT, typename T>
struct convert_vector
{
  static std::vector<ConvertT>
  value (std::vector<T> vec)
  {
    return std::vector<ConvertT> (vec.begin (), vec.end ());
  }
};

template<typename ConvertT>
struct convert_vector<ConvertT, ConvertT>
{
  static std::vector<ConvertT>
  value (std::vector<ConvertT> vec)
  {
    return vec;
  }
};


template<
  typename T,
  size_t get_size (Tox const *),
  void get_data (Tox const *, T *),
  typename ConvertT = T
>
struct get_vector
{
  static std::vector<ConvertT>
  make (Tox const *tox)
  {
    std::vector<T> vec (get_size (tox));
    get_data (tox, vec.data ());

    return convert_vector<ConvertT, T>::value (std::move (vec));
  }
};


template<std::size_t N>
struct constant_size
{
  static std::size_t
  make (Tox const *)
  {
    return N;
  }
};
