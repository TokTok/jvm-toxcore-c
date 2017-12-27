// im.tox.tox4j.crypto.ToxCryptoConstants$
void
checkToxCryptoConstants ()
{
  static_assert (TOX_PASS_BOX_ZERO_BYTES == 16, "Java constant out of sync with C");
  static_assert (TOX_PASS_ENCRYPTION_EXTRA_LENGTH == 80, "Java constant out of sync with C");
  static_assert (TOX_PASS_HASH_LENGTH == 32, "Java constant out of sync with C");
  static_assert (TOX_PASS_KEY_LENGTH == 32, "Java constant out of sync with C");
  static_assert (TOX_PASS_NONCE_LENGTH == 24, "Java constant out of sync with C");
  static_assert (TOX_PASS_PUBLIC_KEY_LENGTH == 32, "Java constant out of sync with C");
  static_assert (TOX_PASS_SALT_LENGTH == 32, "Java constant out of sync with C");
  static_assert (TOX_PASS_SECRET_KEY_LENGTH == 32, "Java constant out of sync with C");
  static_assert (TOX_PASS_SHARED_KEY_LENGTH == 32, "Java constant out of sync with C");
  static_assert (TOX_PASS_ZERO_BYTES == 32, "Java constant out of sync with C");
}
