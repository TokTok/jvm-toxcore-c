package im.tox.tox4j.av.callbacks

import im.tox.tox4j.av.enums.ToxavFriendCallState
import im.tox.tox4j.core.data.ToxFriendNumber
import java.util.EnumSet

/** Called when the call state changes. */
interface CallStateCallback<ToxCoreState> {
  /**
   * @param friendNumber The friend number this call state change is for.
   * @param callState A set of ToxCallState values comprising the new call state. Although this is a
   *   Collection (therefore might actually be a List), this is effectively a Set. Any
   *   [[ToxavFriendCallState]] value is contained exactly 0 or 1 times.
   */
  fun callState(
      friendNumber: ToxFriendNumber,
      callState: EnumSet<ToxavFriendCallState>,
      state: ToxCoreState
  ): ToxCoreState = state
}
