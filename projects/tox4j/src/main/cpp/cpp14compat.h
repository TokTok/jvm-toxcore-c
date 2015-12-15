#if !defined(HAVE_GETS)
extern "C" char *gets (char *);
#endif

#if !defined(HAVE_MAKE_UNIQUE)
#include <memory>

namespace std {
  template<typename T, typename ...Args>
  unique_ptr<T>
  make_unique (Args &&...args)
  {
    return std::unique_ptr<T> (new T (std::forward<Args> (args)...));
  }
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
