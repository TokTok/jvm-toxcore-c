#include "util/to_bytes.h"

#include "util/logging.h"
#include <gtest/gtest.h>

#include "../mock_jni.h"


template<std::size_t N>
std::string
str (char const (&bytes)[N])
{
  return std::string (bytes, N - 1);
}


TEST (ToBytes, NegativeValues) {
  std::cout << "\033[1;31mbold red text\033[0m\n";
  int16_t values[] = { -1, 0, 1, -32768, -32767, 32767 };
  std::string out;
  to_bytes (values, values + sizeof values / sizeof *values, out);
  ASSERT_EQ (
    str(
      "\xFF\xFF" // -1
      "\x00\x00" // 0
      "\x00\x01" // 1
      "\x80\x00" // -32768
      "\x80\x01" // -32767
      "\x7F\xFF" // 32767
    ),
    out
  );
}
