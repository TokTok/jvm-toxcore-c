#pragma once

#include "util/debug_log.h"

#include <cassert>
#include <memory>

namespace tox
{

  namespace detail
  {

    template<typename Subsystem, typename ...Args>
    struct tox_cb
    {
      template<void Set (Subsystem *, void (*)(Subsystem *, Args..., void *), void *)>
      struct func
      {
        template<typename UserData>
        using type = void (*) (Args..., UserData *);

        template<typename UserData, type<UserData> Callback>
        static void
        invoke (Subsystem *tox, Args ...args, void *user_data)
        {
          assert (tox != nullptr);
          LogEntry log_entry (Callback, args...);
          return log_entry.print_result (
            Callback, args..., static_cast<UserData *> (user_data)
          ).unwrap ();
        }

        template<typename UserData, type<UserData> Callback>
        static std::unique_ptr<UserData>
        set (Subsystem *tox, std::unique_ptr<UserData> user_data)
        {
          assert (user_data.get () != nullptr);
          Set (tox, invoke<UserData, Callback>, user_data.get ());
          return user_data;
        }
      };
    };


    template<typename Subsystem, typename Sig>
    struct mk_tox_cb;

    // XXX: why don't variadic templates work here?
    template<typename Subsystem, typename ...Args>
    struct mk_tox_cb<Subsystem, void (Subsystem *, Args..., void *)>
      : tox_cb<Subsystem, Args...>
    { };

    template<typename Subsystem, typename Arg1>
    struct mk_tox_cb<Subsystem, void (Subsystem *, Arg1, void *)>
      : tox_cb<Subsystem, Arg1>
    { };

    template<typename Subsystem, typename Arg1, typename Arg2>
    struct mk_tox_cb<Subsystem, void (Subsystem *, Arg1, Arg2, void *)>
      : tox_cb<Subsystem, Arg1, Arg2>
    { };

    template<typename Subsystem, typename Arg1, typename Arg2, typename Arg3>
    struct mk_tox_cb<Subsystem, void (Subsystem *, Arg1, Arg2, Arg3, void *)>
      : tox_cb<Subsystem, Arg1, Arg2, Arg3>
    { };

    template<typename Subsystem, typename Arg1, typename Arg2, typename Arg3, typename Arg4>
    struct mk_tox_cb<Subsystem, void (Subsystem *, Arg1, Arg2, Arg3, Arg4, void *)>
      : tox_cb<Subsystem, Arg1, Arg2, Arg3, Arg4>
    { };

    template<typename Subsystem, typename Arg1, typename Arg2, typename Arg3, typename Arg4, typename Arg5>
    struct mk_tox_cb<Subsystem, void (Subsystem *, Arg1, Arg2, Arg3, Arg4, Arg5, void *)>
      : tox_cb<Subsystem, Arg1, Arg2, Arg3, Arg4, Arg5>
    { };

    template<typename Subsystem, typename Arg1, typename Arg2, typename Arg3, typename Arg4, typename Arg5, typename Arg6>
    struct mk_tox_cb<Subsystem, void (Subsystem *, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, void *)>
      : tox_cb<Subsystem, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6>
    { };


    template<typename Subsystem, typename Arg1, typename Arg2, typename Arg3, typename Arg4, typename Arg5, typename Arg6, typename Arg7>
    struct mk_tox_cb<Subsystem, void (Subsystem *, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, void *)>
      : tox_cb<Subsystem, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7>
    { };


    template<typename Subsystem, typename Arg1, typename Arg2, typename Arg3, typename Arg4, typename Arg5, typename Arg6, typename Arg7, typename Arg8>
    struct mk_tox_cb<Subsystem, void (Subsystem *, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, void *)>
      : tox_cb<Subsystem, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8>
    { };


    template<typename Subsystem, typename Arg1, typename Arg2, typename Arg3, typename Arg4, typename Arg5, typename Arg6, typename Arg7, typename Arg8, typename Arg9>
    struct mk_tox_cb<Subsystem, void (Subsystem *, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, void *)>
      : tox_cb<Subsystem, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9>
    { };


    template<typename Subsystem, typename Arg1, typename Arg2, typename Arg3, typename Arg4, typename Arg5, typename Arg6, typename Arg7, typename Arg8, typename Arg9, typename Arg10>
    struct mk_tox_cb<Subsystem, void (Subsystem *, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10, void *)>
      : tox_cb<Subsystem, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10>
    { };


    template<typename Subsystem, typename Arg1, typename Arg2, typename Arg3, typename Arg4, typename Arg5, typename Arg6, typename Arg7, typename Arg8, typename Arg9, typename Arg10, typename Arg11>
    struct mk_tox_cb<Subsystem, void (Subsystem *, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10, Arg11, void *)>
      : tox_cb<Subsystem, Arg1, Arg2, Arg3, Arg4, Arg5, Arg6, Arg7, Arg8, Arg9, Arg10, Arg11>
    { };


    template<typename Subsystem, typename Sig, void Set (Subsystem *, Sig, void *)>
    struct cb
      : mk_tox_cb<Subsystem, Sig>::template func<Set>
    { };

  }

  namespace detail
  {

    template<
      typename Subsystem,
      typename UserData,
      typename Cb,
      typename Cb::template type<UserData> Sig
    >
    struct setter
    {
      static std::unique_ptr<UserData>
      set (Subsystem *tox, std::unique_ptr<UserData> user_data)
      {
        return Cb::template set<UserData, Sig> (tox, std::move (user_data));
      }
    };


    template<typename Subsystem, typename UserData, typename ...Callbacks>
    struct set_callbacks;

    template<typename Subsystem, typename UserData>
    struct set_callbacks<Subsystem, UserData>
    {
      static std::unique_ptr<UserData>
      set (Subsystem *, std::unique_ptr<UserData> user_data)
      {
        return user_data;
      }
    };

    template<typename Subsystem, typename UserData, typename Head, typename ...Tail>
    struct set_callbacks<Subsystem, UserData, Head, Tail...>
    {
      static std::unique_ptr<UserData>
      set (Subsystem *tox, std::unique_ptr<UserData> user_data)
      {
        return set_callbacks<Subsystem, UserData, Tail...>::set (tox, Head::set (tox, std::move (user_data)));
      }
    };


    template<typename Subsystem, typename UserData, typename ...Callbacks>
    struct callback_setter
    {
      template<
        typename Cb,
        typename Cb::template type<UserData> Sig
      >
      callback_setter<Subsystem, UserData, Callbacks..., setter<Subsystem, UserData, Cb, Sig>>
      set () &&
      { return { std::move (user_data) }; }

      std::unique_ptr<UserData>
      set (Subsystem *tox) &&
      {
        return set_callbacks<Subsystem, UserData, Callbacks...>::set (tox, std::move (user_data));
      }

      std::unique_ptr<UserData> user_data;
    };

  }


  template<typename Subsystem, typename UserData>
  detail::callback_setter<Subsystem, UserData>
  callbacks (std::unique_ptr<UserData> user_data)
  {
    return { std::move (user_data) };
  }

}
