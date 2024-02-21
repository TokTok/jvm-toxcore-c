#ifndef DEBUG_LOG_H
#define DEBUG_LOG_H

#include "cpp14compat.h"

#include "util/jni/ArrayFromJava.h"
#include "util/pp_attributes.h"
#include "util/pp_cat.h"
#include "util/unused.h"
#include "util/wrap_void.h"

#include <chrono>
#include <memory>
#include <mutex>


/****************************************************************************
 *
 * register_funcs: Function registry (map of address to name) for logging.
 *
 ****************************************************************************/


/**
 * Look up the name of a function.
 */
std::string get_func_name (uintptr_t func);

template<typename R, typename ...Args>
std::string
get_func_name (R func (Args...))
{
  return get_func_name (reinterpret_cast<uintptr_t> (func));
}


/**
 * Add a single address/name pair to the function map.
 */
bool register_func (uintptr_t func, std::string const &name);


/**
 * Recursion end.
 */
static inline bool
register_funcs ()
{
  return true;
}

/**
 * Register all address/name pairs in the function map.
 */
template<typename Func, typename Name, typename ...Funcs>
bool
register_funcs (Func func, Name const &name, Funcs const &...funcs)
{
  register_func (func, name);
  return register_funcs (funcs...);
}


/**
 * Syntax helper macros for registering a number of function addresses with
 * their names. Intended to be called like:
 *
 * REGISTER_FUNCS (
 *   REGISTER_FUNC (std::printf),
 *   REGISTER_FUNC (std::puts),
 *   REGISTER_FUNC (std::fopen)
 * );
 *
 * This will register three of the <cstdio> functions so that they can be
 * looked up by print_func.
 */
#define REGISTER_FUNCS static PP_UNUSED bool const PP_CAT (register_funcs_, __LINE__) = register_funcs
#define REGISTER_FUNC(func) reinterpret_cast<uintptr_t> (func), #func


/**
 * Helper class to create a log entry in jni_log and write the function call
 * arguments and result to the new entry.
 */
struct LogEntry
{
  /**
   * Constructor without instance number. Pass the function as first argument
   * and all the arguments after it. These arguments do not need to be the
   * actual arguments passed to the function. For example, you may choose to
   * omit uninteresting arguments for the log.
   */
  template<typename Func, typename ...Args>
  explicit LogEntry (Func func, Args const&...args)
  {
    static_assert (
      std::is_function<std::remove_pointer_t<Func>>::value,
      "The first argument to LogEntry must be a function or instance number."
    );
  }


  /**
   * Call a function with some arguments and write the result with start time
   * and execution duration to the log entry. If the entry is null (this
   * happens when the log is full), the call is not timed, so print_result
   * has only the overhead of a comparison to 0 and a branch.
   */
  template<typename FuncT, typename ...Args>
  auto
  print_result (FuncT func, Args &&...args)
  {
    return wrap_void (func, std::forward<Args> (args)...);
  }
};

struct InstanceLogEntry
  : LogEntry
{
  /**
   * The same as above but with an instance number.
   */
  template<typename Func, typename ...Args>
  InstanceLogEntry (int instanceNumber, Func func, Args const &...args)
    : LogEntry (func, args...)
  {
  }
};

#endif
