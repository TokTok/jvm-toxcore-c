// Instance manager, JNI utilities.
#include "tox4j/Tox4j.h"

// Protobuf classes.
#include "Core.pb.h"

// JNI declarations from javah.
#include "generated/im_tox_tox4j_impl_jni_ToxCoreJni.h"

// Header from toxcore.
#include <tox/core.h>

#include <tuple>

#ifndef SUBSYSTEM
#define SUBSYSTEM TOX
#define STRUCT    Tox
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


// get_size can give any integer type: the group list's gives uint32_t.
template<
  typename T,
  auto get_size,
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

template<
  typename ConvertT,
  typename GetSize,
  typename GetData
>
class get_vector_err;

template<
  typename ConvertT, typename ...GetSizeArgs, typename ...GetDataArgs
>
class get_vector_err<ConvertT, size_t(GetSizeArgs...), bool(GetDataArgs...)>
{
  using ValueT = std::remove_pointer_t<std::tuple_element_t<sizeof...(GetDataArgs)-2, std::tuple<GetDataArgs...>>>;

  template<typename F, size_t... Is>
  static auto indices_impl(F f, std::index_sequence<Is...>) {
    return f(std::integral_constant<size_t, Is>()...);
  }

  template<size_t N, typename F>
  static auto indices(F f) {
    return indices_impl(f, std::make_index_sequence<N>());
  }

  template<typename F, typename... Ts>
  static auto drop_last(F f, Ts... ts) {
    return indices<sizeof...(Ts)-1>([&](auto... Is){
      auto tuple = std::make_tuple(ts...);
      return f(std::get<Is>(tuple)...);
    });
  }

public:
  template<size_t get_size(GetSizeArgs...), bool get_data(GetDataArgs...)>
  static std::vector<ConvertT>
  make (GetSizeArgs ...args)
  {
    // The error pointer is the last argument, and every *_OK is 0.
    auto error_ptr = std::get<sizeof...(GetSizeArgs) - 1>(std::forward_as_tuple(args...));

    size_t const sz = get_size(args...);
    // On error `sz` is garbage, and with_error_handling throws.
    if (*error_ptr != 0) {
      return {};
    }

    std::vector<ValueT> vec(sz);
    if (!drop_last([&](auto ...argsWithoutErr) {
      // Ignore error here. If get_size failed, then get_data will fail in the same way.
      return get_data(argsWithoutErr..., vec.data(), nullptr);
    }, args...)) {
      // It failed, don't try the conversion.
      return {};
    }

    return convert_vector<ConvertT, uint8_t>::value(std::move(vec));
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
