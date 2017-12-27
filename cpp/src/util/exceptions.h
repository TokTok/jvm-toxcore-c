#pragma once

#include <jni.h>

#include <string>

#include "util/pp_attributes.h"


void throw_tox_killed_exception (JNIEnv *env, jint instance_number, char const *message);
void throw_illegal_state_exception (JNIEnv *env, jint instance_number, char const *message);
void throw_illegal_state_exception (JNIEnv *env, jint instance_number, std::string const &message);
void throw_tox_exception (JNIEnv *env, char const *module, char const *prefix, char const *method, char const *code);


PP_NORETURN void tox4j_fatal_error (JNIEnv *env, char const *message);

#define tox4j_fatal(message) tox4j_fatal_error (env, message)


#define STR(token) STR_(token)
#define STR_(token) #token
#define tox4j_assert(condition) do {                                            \
  if (!(condition))                                                             \
    tox4j_fatal (__FILE__ ":" STR (__LINE__) ": Assertion failed: " #condition);\
} while (0)
