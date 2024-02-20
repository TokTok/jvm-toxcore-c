#pragma once

#include <utility>

#include "cpp14compat.h"

/**
 * Helper template to capture the return value of a function call so that
 * functions returning void can be treated the same way as non-void ones.
 *
 * Both contain an unwrap member function with the return type of the called
 * function. This can be used in other function templates returning some T
 * that can be void.
 *
 * T must be movable or copyable for this to work. Also, all the arguments
 * must be copyable.
 */
template <typename T>
struct wrapped_value {
  T value;
  T unwrap() { return std::move(value); }

  template <typename FuncT, typename... Args>
  static wrapped_value wrap(FuncT func, Args &&...args) {
    return {func(std::forward<Args>(args)...)};
  }
};

template <>
struct wrapped_value<void> {
  void unwrap() const {}

  template <typename FuncT, typename... Args>
  static wrapped_value wrap(FuncT func, Args &&...args) {
    func(std::forward<Args>(args)...);
    return {};
  }
};

/**
 * Call this with the function and its arguments to wrap the return value in
 * a wrapped_value class.
 */
template <typename FuncT, typename... Args>
wrapped_value<std::invoke_result_t<FuncT, Args...>> wrap_void(FuncT func, Args &&...args) {
  return wrapped_value<std::invoke_result_t<FuncT, Args...>>::wrap(func,
                                                                   std::forward<Args>(args)...);
}
