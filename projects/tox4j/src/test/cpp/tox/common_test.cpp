#include "tox/core.h"

#include "util/logging.h"
#include <gtest/gtest.h>


TEST (ToxCore, Dummy) {
  LOG (INFO) << "Dummy test";
  EXPECT_TRUE (true);
}
