#include "util/wrap_void.h"

#include "util/logging.h"
#include <gtest/gtest.h>

#include "../mock_jni.h"


static std::unique_ptr<int>
make_int (int i)
{
  return std::make_unique<int> (i);
}


TEST (WrapVoid, MovableType1) {
  auto wrapped = wrap_void (make_int, 4);
  ASSERT_EQ (4, *wrapped.value);
  ASSERT_EQ (4, *wrapped.value);
}

TEST (WrapVoid, MovableType2) {
  auto wrapped = wrap_void (make_int, 4);
  ASSERT_EQ (4, *wrapped.unwrap ());
  ASSERT_EQ (nullptr, wrapped.unwrap ());
}
