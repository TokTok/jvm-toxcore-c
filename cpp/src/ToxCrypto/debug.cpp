#include "ToxCrypto.h"

#include <algorithm>
#include <vector>

template<>
void
print_arg<Tox_Pass_Key *> (protolog::Value &value, Tox_Pass_Key *const &key)
{
  value.set_v_string ("pass_key");
}
