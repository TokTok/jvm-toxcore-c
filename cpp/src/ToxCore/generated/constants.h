// im.tox.tox4j.core.ToxCoreConstants$
void
checkToxCoreConstants ()
{
  static_assert (TOX_ADDRESS_SIZE == 38, "Java constant out of sync with C");
  static_assert (TOX_DEFAULT_END_PORT == 33545, "Java constant out of sync with C");
  static_assert (TOX_DEFAULT_PROXY_PORT == 8080, "Java constant out of sync with C");
  static_assert (TOX_DEFAULT_START_PORT == 33445, "Java constant out of sync with C");
  static_assert (TOX_DEFAULT_TCP_PORT == 0, "Java constant out of sync with C");
  static_assert (TOX_FILE_ID_LENGTH == 32, "Java constant out of sync with C");
  static_assert (TOX_MAX_CUSTOM_PACKET_SIZE == 1373, "Java constant out of sync with C");
  static_assert (TOX_MAX_FILENAME_LENGTH == 255, "Java constant out of sync with C");
  static_assert (TOX_MAX_FRIEND_REQUEST_LENGTH == 1016, "Java constant out of sync with C");
  static_assert (TOX_MAX_HOSTNAME_LENGTH == 255, "Java constant out of sync with C");
  static_assert (TOX_MAX_MESSAGE_LENGTH == 1372, "Java constant out of sync with C");
  static_assert (TOX_MAX_NAME_LENGTH == 128, "Java constant out of sync with C");
  static_assert (TOX_MAX_STATUS_MESSAGE_LENGTH == 1007, "Java constant out of sync with C");
  static_assert (TOX_PUBLIC_KEY_SIZE == 32, "Java constant out of sync with C");
  static_assert (TOX_SECRET_KEY_SIZE == 32, "Java constant out of sync with C");
}
