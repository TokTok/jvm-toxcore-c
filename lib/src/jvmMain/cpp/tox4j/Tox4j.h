#pragma once

#include "ToxInstances.h"
#include "util/jni/ArrayFromJava.h"
#include "util/jni/ArrayToJava.h"
#include "util/jni/Enum.h"
#include "util/jni/UTFChars.h"
#include "util/pp_cat.h"
#include "util/to_bytes.h"


#define JAVA_METHOD_NAME(NAME) \
  PP_CAT(Java_im_tox_tox4j_impl_jni_, PP_CAT(CLASS, PP_CAT(Jni_, NAME)))

#define TOX_METHOD_NAME(NAME) \
  JAVA_METHOD_NAME(PP_CAT(PREFIX, NAME))


#define JNI_METHOD(TYPE, NAME, ...) \
extern "C" JNIEXPORT TYPE JNICALL NAME \
  (JNIEnv *env, jclass, __VA_ARGS__)

#define JAVA_METHOD(TYPE, NAME, ...) \
  JNI_METHOD(TYPE, JAVA_METHOD_NAME(NAME), __VA_ARGS__)

#define TOX_METHOD(TYPE, NAME, ...) \
  JNI_METHOD(TYPE, TOX_METHOD_NAME(NAME), __VA_ARGS__)
