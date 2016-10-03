#ifndef DEBUG_LOG_H
#define DEBUG_LOG_H

#include "util/jni/ArrayFromJava.h"
#include "util/pp_attributes.h"
#include "util/pp_cat.h"
#include "util/unused.h"
#include "util/wrap_void.h"

#include "ProtoLog.pb.h"

#include <chrono>
#include <memory>
#include <mutex>

namespace protolog = im::tox::tox4j::impl::jni::proto;


/****************************************************************************
 *
 * print_arg: Writes a single argument or return value to a Value.
 *
 ****************************************************************************/


/**
 * General print_arg function for converting any C value to a protolog::Value.
 *
 * This function is specialised for every possible type in C++ function calls.
 * For user-defined types, the print_arg function must be specialised as well.
 */
template<typename Arg>
void print_arg (protolog::Value &value, Arg const &arg);

/**
 * Overload for byte and short arrays of known length. In this case, the values
 * are put in a "bytes" member of the protobuf.
 */
void print_arg (protolog::Value &value, uint8_t const *data, std::size_t length);
void print_arg (protolog::Value &value, int16_t const *data, std::size_t length);

/**
 * Overload for arrays with static bounds.
 */
template<typename T, std::size_t N>
void
print_arg (protolog::Value &value, T const (&array)[N])
{
  print_arg (value, array, N);
}

/**
 * The overload for unique_ptr just calls print_arg on the raw pointer.
 */
template<typename Arg, typename ArgDeleter>
void
print_arg (protolog::Value &value, std::unique_ptr<Arg, ArgDeleter> const &arg)
{
  print_arg (value, arg.get ());
}

/**
 * An overload for Java arrays passed to C functions without their size.
 */
template<
  typename JType,
  typename CType,
  typename JavaArray,
  JType *(JNIEnv::*GetArrayElements) (JavaArray, jboolean *),
  void (JNIEnv::*ReleaseArrayElements) (JavaArray, JType *, jint)
>
void
print_arg (protolog::Value &value, detail::MakeArrayFromJava<JType, CType, JavaArray, GetArrayElements, ReleaseArrayElements> const &array)
{
  print_arg (value, array.data (), array.size ());
}

/**
 * For wrapped_value, we need two overloads, one for the general case and one
 * for void.
 */
template<typename Arg>
void
print_arg (protolog::Value &value, wrapped_value<Arg> const &arg)
{
  // Don't use arg.unwrap() here, because it moves the value out.
  print_arg (value, arg.value);
}

/**
 * The overload for wrapped_value<void> leaves the Value empty.
 */
static inline void
print_arg (protolog::Value &value, wrapped_value<void> const &arg)
{
  unused (value, arg);
}


/****************************************************************************
 *
 * print_member: Adds a name/value pair to an object type Value.
 *
 ****************************************************************************/


template<typename ...Args>
void
print_member (protolog::Struct &object, char const *name, Args ...member_args)
{
  print_arg ((*object.mutable_members ())[name], member_args...);
}


/****************************************************************************
 *
 * print_args: Call print_arg on all arguments and adds them to the log entry.
 *
 ****************************************************************************/


/**
 * Recursion end.
 */
static inline void
print_args (protolog::JniLogEntry &log_entry)
{
  unused (log_entry);
}

/**
 * Recursive function to print_arg every argument.
 *
 * This forward declaration exists so that the data+size overload can call it.
 */
template<typename ...Args>
void print_args (protolog::JniLogEntry &log_entry, Args const &...args);

/**
 * Overload for byte arrays of known size. This is declared before the
 * recursive function below so that it will be in the overload set within
 * the default function.
 */
template<typename ...Args>
void
print_args (protolog::JniLogEntry &log_entry,
            uint8_t const *data, std::size_t size, Args const &...args)
{
  protolog::Value *value = log_entry.add_arguments ();
  print_arg (*value, data, size);
  print_args (log_entry, args...);
}

template<typename Arg0, typename ...Args>
void
print_args (protolog::JniLogEntry &log_entry,
            Arg0 const &arg0, Args const &...args)
{
  protolog::Value *value = log_entry.add_arguments ();
  print_arg (*value, arg0);
  print_args (log_entry, args...);
}


/****************************************************************************
 *
 * register_funcs: Function registry (map of address to name) for logging.
 *
 ****************************************************************************/


/**
 * Look up the name of a function.
 */
std::string get_func_name (uintptr_t func);

/**
 * Look up the name of a function and set it in the log entry.
 */
void print_func (protolog::JniLogEntry &log_entry, uintptr_t func);


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


/****************************************************************************
 *
 * JniLog and LogEntry
 *
 ****************************************************************************/


/**
 * The main public interface for creating log entries, serialising the log,
 * and configuring the logging behaviour.
 *
 * Every member function locks the JniLog instance mutex.
 */
struct JniLog
{
  // Private data is hidden in the implementation file.
  struct data;

  /**
   * The log entry wraps a protolog::JniLogEntry pointer and a mutex lock guard,
   * which is created when a new entry is created, and held until the Entry goes
   * out of scope. This ensures that any writes to the log entry are done before
   * any other operations (in particular, clear()) occur.
   *
   * It also contains some pointer operators ->, *, and bool conversion so it
   * behaves roughly like a JniLogEntry pointer.
   */
  struct Entry
  {
    Entry (Entry &&) = default;

    Entry (data *log = nullptr, protolog::JniLogEntry *entry = nullptr, std::unique_lock<std::recursive_mutex> lock = { });
    ~Entry ();

    protolog::JniLogEntry *operator -> () const { return  entry; }
    protolog::JniLogEntry &operator *  () const { return *entry; }
    explicit operator bool () const { return entry; }

  private:
    data *log;
    protolog::JniLogEntry *entry;
    std::unique_lock<std::recursive_mutex> lock;
  };

  JniLog ();
  ~JniLog ();

  /**
   * Create a new log entry. Only one thread can operate on the log at the
   * same time. This function does not acquire a lock when max_size is set
   * to 0.
   */
  Entry new_entry ();

  /**
   * Serialise the log to bytes and then delete all log entries. After this,
   * empty() will be true.
   */
  std::vector<char> clear ();

  /**
   * Return whether the size of the log is 0.
   */
  bool empty () const;

  /**
   * Set/get the maximum size of the log.
   *
   * These functions do not acquire the lock.
   */
  void max_size (int max_size);
  int max_size () const;

  /**
   * Get the number of entries in the log.
   */
  int size () const;

  /**
   * Set filters to avoid logging certain calls.
   */
  void filter (std::vector<std::string> filters);

private:
  std::unique_ptr<data> self;
};


/**
 * We keep one global log for the process, so we don't need to store it
 * anywhere, and so it can be accessed from the LogEntry helper class.
 */
extern JniLog jni_log;


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
  LogEntry (Func func, Args const &...args)
  {
    static_assert (
      std::is_function<typename std::remove_pointer<Func>::type>::value,
      "The first argument to LogEntry must be a function or instance number."
    );

    if (entry)
      {
        print_func (*entry, reinterpret_cast<uintptr_t> (func));
        print_args (*entry, args...);
      }
  }

  /**
   * The same as above but with an instance number.
   */
  template<typename Func, typename ...Args>
  LogEntry (int instanceNumber, Func func, Args const &...args)
    : LogEntry (func, args...)
  {
    if (entry)
      entry->set_instance_number (instanceNumber);
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
    if (entry)
      {
        auto start = std::chrono::system_clock::now();
        auto result = wrap_void (func, std::forward<Args> (args)...);
        auto end = std::chrono::system_clock::now();

        using std::chrono::duration_cast;
        using std::chrono::seconds;
        using std::chrono::nanoseconds;

        auto start_time = start.time_since_epoch ();

        protolog::Timestamp *timestamp = entry->mutable_timestamp ();
        timestamp->set_seconds (duration_cast<seconds> (start_time).count ());
        timestamp->set_nanos (duration_cast<nanoseconds> (start_time).count () % 1000000000);

        entry->set_elapsed_nanos (duration_cast<nanoseconds> (end - start).count ());

        print_arg (*entry->mutable_result (), result);

        return result;
      }

    return wrap_void (func, std::forward<Args> (args)...);
  }

private:
  JniLog::Entry const entry = jni_log.new_entry ();
};

#endif
