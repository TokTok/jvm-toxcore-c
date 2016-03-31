#include "util/logging.h"
#include <gtest/gtest.h>

// #include <google/protobuf/stubs/common.h>

int
main (int argc, char **argv)
{
  google::InitGoogleLogging (argv[0]);
  testing::InitGoogleTest (&argc, argv);

  google::LogToStderr ();

  int result = RUN_ALL_TESTS ();

  google::ShutdownGoogleLogging ();
  // google::protobuf::ShutdownProtobufLibrary ();

  return result;
}
