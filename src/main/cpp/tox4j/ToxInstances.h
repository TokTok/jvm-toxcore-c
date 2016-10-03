#pragma once

#include "util/instance_manager.h"
#include "util/pp_cat.h"
#include "util/debug_log.h"

#include <iostream>



/*****************************************************************************
 *
 * Conversion of C++ values to Java values.
 *
 *****************************************************************************/


template<typename ToxFunc, typename ...Args>
struct conversions
{
  template<typename T>
  static T
  from_java (T value)
  {
    return value;
  }

  template<
    typename JType,
    typename CType,
    typename JavaArray,
    JType *(JNIEnv::*GetArrayElements) (JavaArray, jboolean *),
    void (JNIEnv::*ReleaseArrayElements) (JavaArray, JType *, jint)
  >
  static auto
  from_java (detail::MakeArrayFromJava<JType, CType, JavaArray, GetArrayElements, ReleaseArrayElements> const &array)
  {
    return array.data ();
  }


  template<typename T>
  static T
  to_java (JNIEnv *env, wrapped_value<T> &&wrapped)
  {
    unused (env);
    return wrapped.unwrap ();
  }

  template<typename T>
  static auto
  to_java (JNIEnv *env, wrapped_value<std::vector<T>> &&wrapped)
  {
    return toJavaArray (env, wrapped.unwrap ());
  }

  static auto
  to_java (JNIEnv *env, LogEntry &log_entry, ToxFunc func, Args ...args)
  {
    return to_java (env, log_entry.print_result (func, from_java (args)...));
  }
};





/*****************************************************************************
 *
 * Error handling code.
 *
 *****************************************************************************/


/**
 * A class containing the meaning of an error code enum value.
 *
 * OK is turned into SUCCESS, everything else that is known is turned into
 * FAILURE together with an error enum name, and unknown values are translated
 * to UNHANDLED.
 */
struct ErrorHandling
{
  enum Result
  {
    UNHANDLED,
    SUCCESS,
    FAILURE
  };

  Result const result;

  /**
   * Null if the result is SUCCESS or UNHANDLED.
   */
  char const *const error;
};

inline ErrorHandling success   (                 ) { return { ErrorHandling::SUCCESS,   nullptr }; }
inline ErrorHandling failure   (char const *error) { return { ErrorHandling::FAILURE,   error   }; }
inline ErrorHandling unhandled (                 ) { return { ErrorHandling::UNHANDLED, nullptr }; }


/**
 * Turn an error code into SUCCESS, FAILURE, or UNHANDLED values. These are
 * defined using HANDLE in generated/errors.cpp for each subsystem.
 */
template<typename ErrorT>
ErrorHandling
handle_error_enum (ErrorT error);


/*****************************************************************************
 *
 * Find Tox error code (last parameter).
 *
 *****************************************************************************/

template<typename FuncT>
struct error_type_of;

template<typename Result, typename Head, typename... Tail>
struct error_type_of<Result (*) (Head, Tail...)>
{
  typedef typename error_type_of<Result (*) (Tail...)>::type type;
};

template<typename Result, typename ErrorCode>
struct error_type_of<Result (*) (ErrorCode *)>
{
  typedef ErrorCode type;
};



#define ERROR_CODE(METHOD)                          \
  PP_CAT (SUBSYSTEM, _ERR_##METHOD)

#define success_case(METHOD)                        \
  case PP_CAT (ERROR_CODE (METHOD), _OK):           \
    return success()

#define failure_case(METHOD, ERROR)                 \
  case PP_CAT (ERROR_CODE (METHOD), _##ERROR):      \
    return failure (#ERROR)


#define HANDLE(NAME, METHOD)                                      \
template<>                                                        \
extern char const *const method_name<ERROR_CODE (METHOD)> = NAME; \
                                                                  \
template<>                                                        \
ErrorHandling                                                     \
handle_error_enum<ERROR_CODE (METHOD)> (ERROR_CODE (METHOD) error)


/**
 * Package name inside im.tox.tox4j (av, core, crypto) in which the subsystem
 * classes live.
 */
template<typename Subsystem>
extern char const *const module_name;

/**
 * Exception name prefix (Tox, Toxav).
 */
template<typename Subsystem>
extern char const *const exn_prefix;

/**
 * Exception name (after prefix) corresponding to the Tox error code enum.
 *
 * These are defined in generated/errors.cpp for each subsystem using the
 * HANDLE macro.
 */
template<typename ErrorCode>
extern char const *const method_name;


/**
 * Throw a specific Tox exception. The Object parameter is Tox or ToxAV, the
 * ErrorType is the error enum type. This overload takes the enum member name
 * directly instead of the error code.
 */
template<typename Object, typename ErrorType>
void
throw_tox_exception (JNIEnv *env, char const *error)
{
  return throw_tox_exception (env,
    module_name<Object>,
    exn_prefix<Object>,
    method_name<ErrorType>,
    error
  );
}


/**
 * Throw a specific Tox exception by error code. It looks up the enum name
 * and creates the appropriate exception class.
 */
template<typename Object, typename ErrorType>
void
throw_tox_exception (JNIEnv *env, ErrorType error)
{
  ErrorHandling result = handle_error_enum<ErrorType> (error);
  switch (result.result)
    {
    case ErrorHandling::FAILURE:
      return throw_tox_exception<Object, ErrorType> (env, result.error);
    case ErrorHandling::SUCCESS:
      return throw_illegal_state_exception (env, error, "Throwing OK code");
    case ErrorHandling::UNHANDLED:
      return throw_illegal_state_exception (env, error, "Unknown error code");
    }

  // If this happens, there is a programming error or memory corruption.
  return throw_illegal_state_exception (env, error, "Memory corruption or cosmic rays");
}


/**
 * Calls a tox function that has an error code as last parameter. It captures
 * the error and throws an appropriate Java exception for that error code if
 * it is anything but "OK". If the function returns with an OK error code,
 * success_func will be called with the return value.
 *
 * The return type of with_error_handling is the result type of calling
 * success_func with the return value of tox_func called with args.
 *
 * It stores the result of the function call in the passed LogEntry.
 * 
 * @param Object Tox or ToxAV, the type of the first parameter to the tox
 *               function without pointer.
 *
 * @param log_entry A LogEntry to store the function result in.
 * @param env The current JNIEnv.
 * @param success_func Function to be called when the error code is OK.
 * @param tox_func The native function to call. Must have an error code as
 *                 last parameter.
 * @param args All the arguments to the tox_func except the error code. The
 *             error code pointer is provided by with_error_handling.
 */
template<typename Object, typename SuccessFunc, typename ToxFunc, typename ...Args>
auto
with_error_handling (LogEntry &log_entry,
                     JNIEnv *env,
                     SuccessFunc success_func,
                     ToxFunc tox_func,
                     Args &&...args)
{
  using error_type = typename error_type_of<ToxFunc>::type;

  // Create an error code value and pass a pointer to the tox function.
  error_type error;
  auto value = conversions<ToxFunc, Args..., error_type *>::to_java (
    env, log_entry, tox_func, std::forward<Args> (args)..., &error
  );
  // Handle it, producing either a SUCCESS or a FAILURE with the error code.
  ErrorHandling result = handle_error_enum<error_type> (error);
  switch (result.result)
    {
    case ErrorHandling::SUCCESS:
      // Call the success function to produce a Java value.
      return success_func (std::move (value));
    case ErrorHandling::FAILURE:
      // Throw an exception in case of error.
      throw_tox_exception<Object, error_type> (env, result.error);
      break;
    case ErrorHandling::UNHANDLED:
      // This only happens if the tox API changed.
      throw_illegal_state_exception (env, error, "Unknown error code");
      break;
    }

  // Return a default value in case of error. This won't be used, since the
  // error will be translated to an exception.
  return decltype (success_func (std::move (value))) ();
}


/**
 * A Tox instance manager. In addition to the facilities provided by
 * instance_manager, this provides with_error_handling member functions for
 * tox error code to exception translation.
 *
 * All function calls are logged to a LogEntry.
 */
template<typename ObjectP, typename EventsP>
struct ToxInstances
  : instance_manager<ObjectP, EventsP>
{
  typedef instance_manager<ObjectP, EventsP> super;

  using typename super::Object;
  using typename super::Events;


  /**
   * Call a tox API function and translate the error code to an exception, or
   * a successful result using success_func.
   */
  template<typename SuccessFunc, typename ToxFunc, typename ...Args>
  auto
  with_error_handling (JNIEnv *env,
                       SuccessFunc success_func,
                       ToxFunc tox_func,
                       Args &&...args)
  {
    LogEntry log_entry (tox_func, args...);
    return ::with_error_handling<Object> (
      log_entry, env, success_func,
      tox_func, std::forward<Args> (args)...
    );
  }


  /**
   * A composition of with_instance and with_error_handling.
   */
  template<typename SuccessFunc, typename ToxFunc, typename ...Args>
  auto
  with_instance_err (JNIEnv *env,
                     jint instanceNumber,
                     SuccessFunc success_func,
                     ToxFunc tox_func,
                     Args &&...args)
  {
    return this->with_instance (env, instanceNumber,
      [&] (Object *tox, Events &events)
        {
          unused (events);
          LogEntry log_entry (instanceNumber, tox_func, tox, args...);
          return ::with_error_handling<Object> (
            log_entry, env, success_func,
            tox_func, tox, std::forward<Args> (args)...
          );
        }
    );
  }


  /**
   * The same as with_instance_err, but translating boolean return values to
   * void. Useful for functions that redundantly return their success as bool,
   * but we already capture success or failure in the error code.
   */
  template<typename ToxFunc, typename ...Args>
  auto
  with_instance_ign (JNIEnv *env,
                     jint instanceNumber,
                     ToxFunc tox_func,
                     Args &&...args)
  {
    struct ignore
    {
      void operator () (bool) { }
    };
    return with_instance_err (env, instanceNumber, ignore (),
      tox_func, std::forward<Args> (args)...
    );
  }


  /**
   * Call a tox function that can not fail.
   */
  template<typename ToxFunc, typename ...Args>
  auto
  with_instance_noerr (JNIEnv *env,
                       jint instanceNumber,
                       ToxFunc tox_func,
                       Args &&...args)
  {
    return this->with_instance (env, instanceNumber,
      [&] (Object *tox, Events &events)
        {
          unused (events);
          LogEntry log_entry (instanceNumber, tox_func, tox, args...);
          return conversions<ToxFunc, Object *, Args...>::to_java (
            env, log_entry, tox_func, tox, std::forward<Args> (args)...
          );
        }
    );
  }
};
