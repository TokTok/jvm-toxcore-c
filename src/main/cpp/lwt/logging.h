#pragma once

#include <array>
#include <vector>

#ifdef HAVE_GLOG
#  include <glog/logging.h>
#else
#  include <ostream>

struct null_ostream
  : std::ostream
{
};

#define LOG(LEVEL) (null_ostream ())
#define LOG_ASSERT(cond) assert (cond)
#endif

namespace tox
{
  void output_hex (std::ostream &os, uint8_t const *data, size_t length);


  template<std::size_t N>
  std::ostream &
  operator << (std::ostream &os, std::array<uint8_t, N> const &array)
  {
    output_hex (os, array.data (), array.size ());
    return os;
  }


  struct formatter
  {
    formatter &operator = (formatter const &fmt) = delete;

    formatter (formatter &&fmt)
      : text_ (std::move (fmt.text_))
    { }

    formatter (std::vector<char> &&text)
      : text_ (text)
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
}
