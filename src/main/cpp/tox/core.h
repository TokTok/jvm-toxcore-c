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

#define COMPAT_CB(CB) \
  inline void tox_callback_##CB(Tox *tox, tox_##CB##_cb *callback, void *) \
  { \
    ::tox_callback_##CB(tox, callback); \
  }

  COMPAT_CB (self_connection_status)
  COMPAT_CB (friend_status_message)
  COMPAT_CB (friend_name)
  COMPAT_CB (friend_request)
  COMPAT_CB (friend_typing)
  COMPAT_CB (file_chunk_request)
  COMPAT_CB (file_recv)
  COMPAT_CB (file_recv_chunk)
  COMPAT_CB (file_recv_control)
  COMPAT_CB (friend_connection_status)
  COMPAT_CB (friend_lossless_packet)
  COMPAT_CB (friend_lossy_packet)
  COMPAT_CB (friend_message)
  COMPAT_CB (friend_read_receipt)
  COMPAT_CB (friend_status)

#undef COMPAT_CB

#define CALLBACK(NAME)  using callback_##NAME = detail::cb<Tox, tox_##NAME##_cb, tox_callback_##NAME>;
#include "generated/core.h"
#undef CALLBACK
}
