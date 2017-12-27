#include "util/instance_manager.h"

#include "util/logging.h"
#include <gtest/gtest.h>

#include "../mock_jni.h"


static std::unique_ptr<int>
make_int (int i)
{
  return std::unique_ptr<int> (new int (i));
}


typedef instance_manager<std::unique_ptr<int>, std::unique_ptr<int>> int_manager;


TEST (InstanceManager, Add) {
  mock_jni *env = mock_jnienv ();

  int_manager mgr;
  jint id = mgr.add (env, make_int (1), make_int (2));
  ASSERT_GT (id, 0);
}


TEST (InstanceManager, Kill) {
  mock_jni *env = mock_jnienv ();

  int_manager mgr;
  jint id = mgr.add (env, make_int (1), make_int (2));
  mgr.kill (env, id);
}


TEST (InstanceManager, FinalizeWithoutKill) {
  mock_jni *env = mock_jnienv ();

  int_manager mgr;
  jint id = mgr.add (env, make_int (1), make_int (2));
  mgr.finalize (env, id);
  ASSERT_TRUE (env->exn != nullptr);
}


TEST (InstanceManager, Finalize) {
  mock_jni *env = mock_jnienv ();

  int_manager mgr;
  jint id = mgr.add (env, make_int (1), make_int (2));
  mgr.kill (env, id);
  mgr.finalize (env, id);
}


TEST (InstanceManager, WithInstance) {
  mock_jni *env = mock_jnienv ();

  int_manager mgr;
  jint id = mgr.add (env, make_int (1), make_int (2));
  mgr.with_instance (env, id,
    [] (int *a, int &b)
      {
        ASSERT_EQ (*a, 1);
        ASSERT_EQ (b, 2);
      }
  );
}
