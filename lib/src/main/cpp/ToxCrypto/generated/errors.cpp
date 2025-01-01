#include "../ToxCrypto.h"

HANDLE ("Decryption", Decryption)
{
  switch (error)
    {
    success_case (DECRYPTION);
    failure_case (DECRYPTION, BAD_FORMAT);
    failure_case (DECRYPTION, FAILED);
    failure_case (DECRYPTION, INVALID_LENGTH);
    failure_case (DECRYPTION, KEY_DERIVATION_FAILED);
    failure_case (DECRYPTION, NULL);
    }
  return unhandled ();
}

HANDLE ("Encryption", Encryption)
{
  switch (error)
    {
    success_case (ENCRYPTION);
    failure_case (ENCRYPTION, FAILED);
    failure_case (ENCRYPTION, KEY_DERIVATION_FAILED);
    failure_case (ENCRYPTION, NULL);
    }
  return unhandled ();
}

HANDLE ("GetSalt", Get_Salt)
{
  switch (error)
    {
    success_case (GET_SALT);
    failure_case (GET_SALT, BAD_FORMAT);
    failure_case (GET_SALT, NULL);
    }
  return unhandled ();
}

HANDLE ("KeyDerivation", Key_Derivation)
{
  switch (error)
    {
    success_case (KEY_DERIVATION);
    failure_case (KEY_DERIVATION, FAILED);
    failure_case (KEY_DERIVATION, NULL);
    }
  return unhandled ();
}
