#pragma once

#include <array>
#include <ostream>
#include <vector>


struct scope_counter
{
  static std::size_t scope;

  scope_counter ()
  { scope++; }

  ~scope_counter ()
  { scope--; }

  scope_counter (scope_counter const &rhs) = delete;
};

std::ostream &scope_indent (std::ostream &os, int line);

#define SCOPE scope_counter const _scope


#ifdef HAVE_GLOG
#  include <glog/logging.h>
#  undef LOG
#  define LOG(KIND) scope_indent (COMPACT_GOOGLE_LOG_##KIND.stream(), __LINE__)
#else
namespace google
{
  inline void InitGoogleLogging (char const *) { }
  inline void ShutdownGoogleLogging () { }
  inline void LogToStderr () { }
}

struct null_ostream
  : std::ostream
{
};

#  define LOG(LEVEL) (null_ostream ())
#  define LOG_ASSERT(cond) assert (cond)
#endif

void output_hex (std::ostream &os, uint8_t const *data, size_t length);


struct formatter
{
  formatter (formatter &&fmt)
    : text_ (std::move (fmt.text_))
  { }

  explicit formatter (std::vector<char> text)
    : text_ (std::move (text))
  { }

  friend std::ostream &operator << (std::ostream &os, formatter const &fmt);

private:
  std::vector<char> const text_;
};


template<size_t N, typename ...Args>
formatter
format (char const (&fmt)[N], Args const &...args)
{
  std::vector<char> text (snprintf (nullptr, 0, fmt, args...) + 1);
  snprintf (text.data (), text.size (), fmt, args...);

  return formatter (std::move (text));
}

template<std::size_t N>
std::ostream &
operator << (std::ostream &os, std::array<uint8_t, N> const &array)
{
  output_hex (os, array.data (), array.size ());
  return os;
}
