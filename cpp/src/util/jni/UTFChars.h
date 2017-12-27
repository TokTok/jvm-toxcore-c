#pragma once

#include <jni.h>
#include <string>


/*****************************************************************************
 *
 * UTF-8 encoded Java string as C++ char array.
 *
 *****************************************************************************/


struct UTFChars
{
  UTFChars (JNIEnv *env, jstring string)
    : env (env)
    , string (string)
    , chars (string ? env->GetStringUTFChars (string, 0) : nullptr)
  { }

  UTFChars (UTFChars const &) = delete;
  ~UTFChars () { if (string) env->ReleaseStringUTFChars (string, chars); }

  char const *data () const { return chars; }

  size_t size () const { return (size_t)string ? env->GetStringUTFLength (string) : 0; }

  std::string to_string () const { return std::string (data (), size ()); }

private:
  JNIEnv *env;
  jstring string;
  char const *chars;
};
