#include "util/exceptions.h"

#include <cstdlib>
#include <sstream>


PP_NORETURN void
tox4j_fatal_error (JNIEnv *env, char const *message)
{
  env->FatalError (message);
  std::abort ();
}


static std::string
fullMessage (jint instance_number, char const *message)
{
  std::ostringstream result;

  result << message << ", instance_number = " << instance_number;
  return result.str ();
}


static void
throw_exception (JNIEnv *env, jint instance_number, char const *class_name, char const *message)
{
  env->ThrowNew (env->FindClass (class_name), fullMessage (instance_number, message).c_str ());
}

void
throw_tox_killed_exception (JNIEnv *env, jint instance_number, char const *message)
{
  throw_exception (env, instance_number, "im/tox/tox4j/exceptions/ToxKilledException", message);
}

void
throw_illegal_state_exception (JNIEnv *env, jint instance_number, char const *message)
{
  throw_exception (env, instance_number, "java/lang/IllegalStateException", message);
}

void
throw_illegal_state_exception (JNIEnv *env, jint instance_number, std::string const &message)
{
  throw_exception (env, instance_number, "java/lang/IllegalStateException", message.c_str ());
}


void
throw_tox_exception (JNIEnv *env, char const *module, char const *prefix, char const *method, char const *code)
{
  std::string className = "im/tox/tox4j/";
  className += module;
  className += "/exceptions/Tox";
  className += prefix;
  className += method;
  className += "Exception";

  jclass exceptionClass = env->FindClass (className.c_str ());
  if (!exceptionClass)
    return;

  std::string enumName = className + "$Code";
  jclass enumClass = env->FindClass (enumName.c_str ());
  if (!enumClass)
    return;

  std::string valueOfSig = "(Ljava/lang/String;)L" + enumName + ";";
  jmethodID valueOf = env->GetStaticMethodID (enumClass, "valueOf", valueOfSig.c_str ());
  if (!valueOf)
    return;

  std::string constructorSig = "(L" + enumName + ";)V";
  jmethodID constructor = env->GetMethodID (exceptionClass, "<init>", constructorSig.c_str ());
  if (!constructor)
    return;

  jobject enumCode = env->CallStaticObjectMethod (enumClass, valueOf, env->NewStringUTF (code));
  if (env->ExceptionCheck ())
    return;
  tox4j_assert (enumCode);

  jobject exception = env->NewObject (exceptionClass, constructor, enumCode);
  if (env->ExceptionCheck ())
    return;
  tox4j_assert (exception);

  env->Throw ((jthrowable)exception);
}
