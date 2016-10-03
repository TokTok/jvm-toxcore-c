#include <jni.h>

#include <algorithm>
#include <string>
#include <vector>


struct mock_jclass
  : _jclass
{
  explicit mock_jclass (std::string name)
    : name (name)
  { }

  std::string name;
};


struct mock_jthrowable
  : _jthrowable
{
  mock_jthrowable (jclass clazz, std::string name)
    : clazz (static_cast<mock_jclass *> (clazz))
    , name (name)
  { }

  mock_jclass *clazz;
  std::string name;
};


struct mock_jni
  : JNIEnv
{
  mock_jni (JNINativeInterface_ const *functions)
    : JNIEnv { functions }
  { }


  jclass
  FindClass (const char *name)
  {
    return new mock_jclass (name);
  }

  jint
  ThrowNew (jclass clazz, const char *msg)
  {
    exn = new mock_jthrowable (clazz, msg);
    return 0;
  }


  mock_jthrowable *exn = nullptr;
};


mock_jni *mock_jnienv ();
