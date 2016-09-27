#pragma once

#include "tox/common.h"
#include <tox/toxav.h>

namespace tox
{
  struct av_deleter
  {
    void operator () (ToxAV *toxav)
    {
      toxav_kill (toxav);
    }
  };

  typedef std::unique_ptr<ToxAV, av_deleter> av_ptr;

#define COMPAT_CB(CB) \
  inline void toxav_callback_##CB(ToxAV *av, toxav_##CB##_cb *callback, void *) \
  { \
    ::toxav_callback_##CB(av, callback); \
  }

#if 0
  COMPAT_CB (audio_receive_frame)
  COMPAT_CB (video_receive_frame)
  COMPAT_CB (call)
  COMPAT_CB (call_state)
  COMPAT_CB (bit_rate_status)
#endif

#undef COMPAT_CB

#define CALLBACK(NAME)  using callback_##NAME = detail::cb<ToxAV, toxav_##NAME##_cb, toxav_callback_##NAME>;
#include "generated/av.h"
#undef CALLBACK
}
