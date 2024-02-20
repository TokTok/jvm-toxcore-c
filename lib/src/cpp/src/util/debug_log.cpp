#include "util/debug_log.h"

#include <tox/core.h>

#include <algorithm>
#include <cctype>
#include <cstdlib>
#include <iostream>
#include <sstream>
#include <map>

#include <jni.h>


/**
 * Maximum number of bytes logged in a "bytes" value.
 */
static std::size_t const MAX_DATA_LOG_LENGTH = 128;


/****************************************************************************
 *
 * :: Log and log entry.
 *
 ****************************************************************************/


// Global log.
JniLog jni_log;


struct JniLog::data
{
  int max_size = 100;
  std::recursive_mutex mutex;
  protolog::JniLog log;
  std::vector<std::string> filters;
};


JniLog::Entry::Entry (data *log, protolog::JniLogEntry *entry, std::unique_lock<std::recursive_mutex> lock)
  : log_ (log)
  , entry_ (entry)
  , lock_ (std::move (lock))
{
}


JniLog::Entry::Entry(Entry &&rhs)
  : log_ (rhs.log_)
  , entry_ (rhs.entry_)
  , lock_ (std::move (rhs.lock_))
{
  rhs.log_ = nullptr;
  rhs.entry_ = nullptr;
}


JniLog::Entry::~Entry ()
{
  // Check if this entry needs to be filtered out.
  if (entry_ && std::find (log_->filters.begin (), log_->filters.end (), entry_->name ()) != log_->filters.end ())
    {
      auto *entries = log_->log.mutable_entries ();

      // Search for the current entry in the recently added entries.
      auto found = std::find_if (entries->rbegin (), entries->rend (),
        [this](auto const &element)
        { return &element == entry_; }
      );

      // This would mean the log was cleared before the mutex was unlocked.
      assert (found != entries->rend ());

      // Remove the entry from the log, moving the ones after it to the front
      // by one place. These should not be many, usually just one. During a
      // single entry's lifetime, only event processing can happen recursively.
      int deleted_index = entries->rend () - found - 1;
      entries->DeleteSubrange (deleted_index, 1);
    }
}


JniLog::JniLog ()
  : self (make_unique<data> ())
{
}

JniLog::~JniLog ()
{
}


JniLog::Entry
JniLog::new_entry ()
{
  // No lock for max_size.
  if (self->max_size == 0)
    return { };

  // Acquire a lock and pass it to the Entry.
  std::unique_lock<std::recursive_mutex> lock (self->mutex);
  if (self->log.entries_size () >= self->max_size)
    // If the log is full, unlock right away and return null.
    return nullptr;
  return JniLog::Entry (self.get (), self->log.add_entries (), std::move (lock));
}

std::vector<char>
JniLog::clear ()
{
  std::lock_guard<std::recursive_mutex> lock (self->mutex);

  // Try to update log entries with numeric names.
  for (protolog::JniLogEntry &entry : *self->log.mutable_entries ())
    {
      assert (!entry.name ().empty ());
      if (std::isdigit (entry.name ()[0]))
        {
          std::istringstream in (entry.name ());
          std::uintptr_t pointer;
          in >> pointer;
          entry.set_name (get_func_name (pointer));
        }
    }

  std::vector<char> buffer (self->log.ByteSizeLong ());
  self->log.SerializeToArray (buffer.data (), buffer.size ());
  self->log.Clear ();

  return buffer;
}

bool
JniLog::empty () const
{
  return size () == 0;
}

void
JniLog::max_size (int max_size)
{
  self->max_size = max_size;
}

int
JniLog::max_size () const
{
  return self->max_size;
}

int
JniLog::size () const
{
  std::lock_guard<std::recursive_mutex> lock (self->mutex);
  return self->log.entries_size ();
}

void
JniLog::filter (std::vector<std::string> filters)
{
  std::lock_guard<std::recursive_mutex> lock (self->mutex);
  self->filters = std::move (filters);
}


/****************************************************************************
 *
 * :: Function name registry.
 *
 ****************************************************************************/


/**
 * The global singleton is in a function because otherwise static
 * initialisation order does not guarantee the map to be initialised when it
 * is accessed from another translation unit calling register_func.
 */
static std::map<std::uintptr_t, std::string const> &
func_names ()
{
  static std::map<std::uintptr_t, std::string const> func_names;
  return func_names;
}


bool
register_func (std::uintptr_t func, std::string const &name)
{
  auto &names = func_names ();
  assert (names.find (func) == names.end ());
  names.insert (std::make_pair (func, name));
  return true;
}


std::string
get_func_name (std::uintptr_t func)
{
  auto &names = func_names ();
  auto found = names.find (func);
  if (found != names.end ())
    return found->second;
  else
    return std::to_string (func);
}


void
print_func (protolog::JniLogEntry &log_entry, std::uintptr_t func)
{
  log_entry.set_name (get_func_name (func));
}


/****************************************************************************
 *
 * :: Common print_arg specialisations for C++ and JNI types.
 *
 ****************************************************************************/


template<typename Arg>
void
print_arg (protolog::Value &value, Arg const &arg)
{
  value.set_v_sint64 (arg);
}

template void print_arg<         bool     > (protolog::Value &,          bool      const &);
template void print_arg<  signed char     > (protolog::Value &,   signed char      const &);
template void print_arg<unsigned char     > (protolog::Value &, unsigned char      const &);
template void print_arg<  signed short    > (protolog::Value &,   signed short     const &);
template void print_arg<unsigned short    > (protolog::Value &, unsigned short     const &);
template void print_arg<  signed int      > (protolog::Value &,   signed int       const &);
template void print_arg<unsigned int      > (protolog::Value &, unsigned int       const &);
template void print_arg<  signed long     > (protolog::Value &,   signed long      const &);
template void print_arg<unsigned long     > (protolog::Value &, unsigned long      const &);
template void print_arg<  signed long long> (protolog::Value &,   signed long long const &);
template void print_arg<unsigned long long> (protolog::Value &, unsigned long long const &);

template<>
void
print_arg<char const *> (protolog::Value &value, char const *const &data)
{
  if (data == nullptr)
    value.set_v_string ("<null>");
  else
    value.set_v_string (data);
}

template<>
void
print_arg<uint8_t const *> (protolog::Value &value, uint8_t const *const &data)
{
  if (data == nullptr)
    value.set_v_string ("<null>");
  else
    value.set_v_string ("byte[]");
}

void
print_arg (protolog::Value &value, uint8_t const *data, std::size_t length)
{
  if (data == nullptr)
    value.set_v_string ("<null>");
  else
    {
      value.set_v_bytes (data, std::min (length, MAX_DATA_LOG_LENGTH));
      if (length > MAX_DATA_LOG_LENGTH)
        value.set_truncated (length);
    }
}

void
print_arg (protolog::Value &value, int16_t const *data, std::size_t length)
{
  if (data == nullptr)
    value.set_v_string ("<null>");
  else
    value.set_v_string ("short[" + std::to_string (length) + "]");
}

template<>
void
print_arg<std::vector<uint8_t>> (protolog::Value &value, std::vector<uint8_t> const &data)
{
  print_arg (value, data.data (), data.size ());
}

template<>
void
print_arg<std::vector<jint>> (protolog::Value &value, std::vector<jint> const &data)
{
  value.set_v_string ("int[" + std::to_string (data.size ()) + "]");
}
