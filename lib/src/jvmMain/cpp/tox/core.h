#pragma once

#include "tox/common.h"
#include <tox/tox.h>


namespace tox
{
  struct core_deleter
  {
    void operator () (Tox *tox)
    {
      tox_kill (tox);
    }
  };

  typedef std::unique_ptr<Tox, core_deleter> core_ptr;

#define CALLBACK(CB) \
  inline void tox_callback_##CB(Tox *tox, tox_##CB##_cb *callback, void *) \
  { \
    ::tox_callback_##CB(tox, callback); \
  } \
  using callback_##CB = detail::cb<Tox, tox_##CB##_cb, tox_callback_##CB>;
#include "generated/core.h"
#undef CALLBACK
}
