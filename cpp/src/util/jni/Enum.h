#pragma once

#include <jni.h>


struct Enum
{
  template<typename T>
  static jint ordinal (JNIEnv *env, T value);

  template<typename T>
  static T valueOf (JNIEnv *env, jint ordinal);
};
