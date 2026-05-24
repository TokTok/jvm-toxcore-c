// im.tox.tox4j.core.ToxCoreConstants$
static void
checkToxCoreConstants ()
{
  static_assert (TOX_ADDRESS_SIZE == 38, "Java constant out of sync with C");
  static_assert (TOX_CONFERENCE_ID_SIZE == 32, "Java constant out of sync with C");
  static_assert (TOX_DHT_ID_SIZE == 32, "Java constant out of sync with C");
  static_assert (TOX_FILE_ID_LENGTH == 32, "Java constant out of sync with C");
  static_assert (TOX_GROUP_CHAT_ID_SIZE == 32, "Java constant out of sync with C");
  static_assert (TOX_GROUP_MAX_CUSTOM_LOSSLESS_PACKET_LENGTH == 1373, "Java constant out of sync with C");
  static_assert (TOX_GROUP_MAX_CUSTOM_LOSSY_PACKET_LENGTH == 1373, "Java constant out of sync with C");
  static_assert (TOX_GROUP_MAX_GROUP_NAME_LENGTH == 48, "Java constant out of sync with C");
  static_assert (TOX_GROUP_MAX_MESSAGE_LENGTH == 1372, "Java constant out of sync with C");
  static_assert (TOX_GROUP_MAX_PART_LENGTH == 128, "Java constant out of sync with C");
  static_assert (TOX_GROUP_MAX_PASSWORD_SIZE == 32, "Java constant out of sync with C");
  static_assert (TOX_GROUP_MAX_TOPIC_LENGTH == 512, "Java constant out of sync with C");
  static_assert (TOX_GROUP_PEER_PUBLIC_KEY_SIZE == 32, "Java constant out of sync with C");
  static_assert (TOX_MAX_CUSTOM_PACKET_SIZE == 1373, "Java constant out of sync with C");
  static_assert (TOX_MAX_FILENAME_LENGTH == 255, "Java constant out of sync with C");
  static_assert (TOX_MAX_FRIEND_REQUEST_LENGTH == 921, "Java constant out of sync with C");
  static_assert (TOX_MAX_HOSTNAME_LENGTH == 255, "Java constant out of sync with C");
  static_assert (TOX_MAX_MESSAGE_LENGTH == 1372, "Java constant out of sync with C");
  static_assert (TOX_MAX_NAME_LENGTH == 128, "Java constant out of sync with C");
  static_assert (TOX_MAX_STATUS_MESSAGE_LENGTH == 1007, "Java constant out of sync with C");
  static_assert (TOX_NOSPAM_SIZE == 4, "Java constant out of sync with C");
  static_assert (TOX_PUBLIC_KEY_SIZE == 32, "Java constant out of sync with C");
  static_assert (TOX_SECRET_KEY_SIZE == 32, "Java constant out of sync with C");
  static_assert (TOX_VERSION_MAJOR == 0, "Java constant out of sync with C");
  static_assert (TOX_VERSION_MINOR == 2, "Java constant out of sync with C");
  static_assert (TOX_VERSION_PATCH == 25, "Java constant out of sync with C");
}
