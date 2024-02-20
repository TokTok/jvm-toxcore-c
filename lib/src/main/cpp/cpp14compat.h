#pragma once

#if !defined(HAVE_GETS)
extern "C" char *gets (char *);
#endif

#include <memory>

#if defined(HAVE_MAKE_UNIQUE)
using ::std::make_unique;
#else

template<typename T, typename ...Args>
std::unique_ptr<T>
make_unique (Args &&...args)
{
  return std::unique_ptr<T> (new T (std::forward<Args> (args)...));
}
#endif

#if !defined(HAVE_TO_STRING)
#include <sstream>

namespace std {
  template<typename T>
  string
  to_string (T const &v)
  {
    ostringstream out;
    out << v;
    return out.str ();
  }
}
#endif
