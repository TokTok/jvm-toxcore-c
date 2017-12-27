#pragma GCC diagnostic ignored "-Wunused-parameter"

#include "mock_jni.h"
#include <gtest/gtest.h>


jint
GetVersion (JNIEnv *env)
{
  ADD_FAILURE () << "GetVersion";
  return 0;
}

jclass
DefineClass (JNIEnv *env, const char *name, jobject loader, const jbyte *buf, jsize len)
{
  ADD_FAILURE () << "DefineClass";
  return 0;
}

jmethodID
FromReflectedMethod (JNIEnv *env, jobject method)
{
  ADD_FAILURE () << "FromReflectedMethod";
  return 0;
}

jfieldID
FromReflectedField (JNIEnv *env, jobject field)
{
  ADD_FAILURE () << "FromReflectedField";
  return 0;
}

jobject
ToReflectedMethod (JNIEnv *env, jclass cls, jmethodID methodID, jboolean isStatic)
{
  ADD_FAILURE () << "ToReflectedMethod";
  return 0;
}

jclass
GetSuperclass (JNIEnv *env, jclass sub)
{
  ADD_FAILURE () << "GetSuperclass";
  return 0;
}

jboolean
IsAssignableFrom (JNIEnv *env, jclass sub, jclass sup)
{
  ADD_FAILURE () << "IsAssignableFrom";
  return 0;
}

jobject
ToReflectedField (JNIEnv *env, jclass cls, jfieldID fieldID, jboolean isStatic)
{
  ADD_FAILURE () << "ToReflectedField";
  return 0;
}

jint
Throw (JNIEnv *env, jthrowable obj)
{
  ADD_FAILURE () << "Throw";
  return 0;
}

jthrowable
ExceptionOccurred (JNIEnv *env)
{
  ADD_FAILURE () << "ExceptionOccurred";
  return 0;
}

void
ExceptionDescribe (JNIEnv *env)
{
  ADD_FAILURE () << "ExceptionDescribe";
}

void
ExceptionClear (JNIEnv *env)
{
  ADD_FAILURE () << "ExceptionClear";
}

void
FatalError (JNIEnv *env, const char *msg)
{
  ADD_FAILURE () << "FatalError";
}

jint
PushLocalFrame (JNIEnv *env, jint capacity)
{
  ADD_FAILURE () << "PushLocalFrame";
  return 0;
}

jobject
PopLocalFrame (JNIEnv *env, jobject result)
{
  ADD_FAILURE () << "PopLocalFrame";
  return 0;
}

jobject
NewGlobalRef (JNIEnv *env, jobject lobj)
{
  ADD_FAILURE () << "NewGlobalRef";
  return 0;
}

void
DeleteGlobalRef (JNIEnv *env, jobject gref)
{
  ADD_FAILURE () << "DeleteGlobalRef";
}

void
DeleteLocalRef (JNIEnv *env, jobject obj)
{
  ADD_FAILURE () << "DeleteLocalRef";
}

jboolean
IsSameObject (JNIEnv *env, jobject obj1, jobject obj2)
{
  ADD_FAILURE () << "IsSameObject";
  return 0;
}

jobject
NewLocalRef (JNIEnv *env, jobject ref)
{
  ADD_FAILURE () << "NewLocalRef";
  return 0;
}

jint
EnsureLocalCapacity (JNIEnv *env, jint capacity)
{
  ADD_FAILURE () << "EnsureLocalCapacity";
  return 0;
}

jobject
AllocObject (JNIEnv *env, jclass clazz)
{
  ADD_FAILURE () << "AllocObject";
  return 0;
}

jobject
NewObject (JNIEnv *env, jclass clazz, jmethodID methodID, ...)
{
  ADD_FAILURE () << "NewObject";
  return 0;
}

jobject
NewObjectV (JNIEnv *env, jclass clazz, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "NewObjectV";
  return 0;
}

jobject
NewObjectA (JNIEnv *env, jclass clazz, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "NewObjectA";
  return 0;
}

jclass
GetObjectClass (JNIEnv *env, jobject obj)
{
  ADD_FAILURE () << "GetObjectClass";
  return 0;
}

jboolean
IsInstanceOf (JNIEnv *env, jobject obj, jclass clazz)
{
  ADD_FAILURE () << "IsInstanceOf";
  return 0;
}

jmethodID
GetMethodID (JNIEnv *env, jclass clazz, const char *name, const char *sig)
{
  ADD_FAILURE () << "GetMethodID";
  return 0;
}

jobject
CallObjectMethod (JNIEnv *env, jobject obj, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallObjectMethod";
  return 0;
}

jobject
CallObjectMethodV (JNIEnv *env, jobject obj, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallObjectMethodV";
  return 0;
}

jobject
CallObjectMethodA (JNIEnv *env, jobject obj, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallObjectMethodA";
  return 0;
}

jboolean
CallBooleanMethod (JNIEnv *env, jobject obj, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallBooleanMethod";
  return 0;
}

jboolean
CallBooleanMethodV (JNIEnv *env, jobject obj, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallBooleanMethodV";
  return 0;
}

jboolean
CallBooleanMethodA (JNIEnv *env, jobject obj, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallBooleanMethodA";
  return 0;
}

jbyte
CallByteMethod (JNIEnv *env, jobject obj, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallByteMethod";
  return 0;
}

jbyte
CallByteMethodV (JNIEnv *env, jobject obj, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallByteMethodV";
  return 0;
}

jbyte
CallByteMethodA (JNIEnv *env, jobject obj, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallByteMethodA";
  return 0;
}

jchar
CallCharMethod (JNIEnv *env, jobject obj, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallCharMethod";
  return 0;
}

jchar
CallCharMethodV (JNIEnv *env, jobject obj, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallCharMethodV";
  return 0;
}

jchar
CallCharMethodA (JNIEnv *env, jobject obj, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallCharMethodA";
  return 0;
}

jshort
CallShortMethod (JNIEnv *env, jobject obj, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallShortMethod";
  return 0;
}

jshort
CallShortMethodV (JNIEnv *env, jobject obj, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallShortMethodV";
  return 0;
}

jshort
CallShortMethodA (JNIEnv *env, jobject obj, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallShortMethodA";
  return 0;
}

jint
CallIntMethod (JNIEnv *env, jobject obj, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallIntMethod";
  return 0;
}

jint
CallIntMethodV (JNIEnv *env, jobject obj, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallIntMethodV";
  return 0;
}

jint
CallIntMethodA (JNIEnv *env, jobject obj, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallIntMethodA";
  return 0;
}

jlong
CallLongMethod (JNIEnv *env, jobject obj, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallLongMethod";
  return 0;
}

jlong
CallLongMethodV (JNIEnv *env, jobject obj, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallLongMethodV";
  return 0;
}

jlong
CallLongMethodA (JNIEnv *env, jobject obj, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallLongMethodA";
  return 0;
}

jfloat
CallFloatMethod (JNIEnv *env, jobject obj, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallFloatMethod";
  return 0;
}

jfloat
CallFloatMethodV (JNIEnv *env, jobject obj, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallFloatMethodV";
  return 0;
}

jfloat
CallFloatMethodA (JNIEnv *env, jobject obj, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallFloatMethodA";
  return 0;
}

jdouble
CallDoubleMethod (JNIEnv *env, jobject obj, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallDoubleMethod";
  return 0;
}

jdouble
CallDoubleMethodV (JNIEnv *env, jobject obj, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallDoubleMethodV";
  return 0;
}

jdouble
CallDoubleMethodA (JNIEnv *env, jobject obj, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallDoubleMethodA";
  return 0;
}

void
CallVoidMethod (JNIEnv *env, jobject obj, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallVoidMethod";
}

void
CallVoidMethodV (JNIEnv *env, jobject obj, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallVoidMethodV";
}

void
CallVoidMethodA (JNIEnv *env, jobject obj, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallVoidMethodA";
}

jobject
CallNonvirtualObjectMethod (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallNonvirtualObjectMethod";
  return 0;
}

jobject
CallNonvirtualObjectMethodV (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallNonvirtualObjectMethodV";
  return 0;
}

jobject
CallNonvirtualObjectMethodA (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallNonvirtualObjectMethodA";
  return 0;
}

jboolean
CallNonvirtualBooleanMethod (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallNonvirtualBooleanMethod";
  return 0;
}

jboolean
CallNonvirtualBooleanMethodV (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallNonvirtualBooleanMethodV";
  return 0;
}

jboolean
CallNonvirtualBooleanMethodA (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallNonvirtualBooleanMethodA";
  return 0;
}

jbyte
CallNonvirtualByteMethod (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallNonvirtualByteMethod";
  return 0;
}

jbyte
CallNonvirtualByteMethodV (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallNonvirtualByteMethodV";
  return 0;
}

jbyte
CallNonvirtualByteMethodA (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallNonvirtualByteMethodA";
  return 0;
}

jchar
CallNonvirtualCharMethod (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallNonvirtualCharMethod";
  return 0;
}

jchar
CallNonvirtualCharMethodV (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallNonvirtualCharMethodV";
  return 0;
}

jchar
CallNonvirtualCharMethodA (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallNonvirtualCharMethodA";
  return 0;
}

jshort
CallNonvirtualShortMethod (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallNonvirtualShortMethod";
  return 0;
}

jshort
CallNonvirtualShortMethodV (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallNonvirtualShortMethodV";
  return 0;
}

jshort
CallNonvirtualShortMethodA (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallNonvirtualShortMethodA";
  return 0;
}

jint
CallNonvirtualIntMethod (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallNonvirtualIntMethod";
  return 0;
}

jint
CallNonvirtualIntMethodV (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallNonvirtualIntMethodV";
  return 0;
}

jint
CallNonvirtualIntMethodA (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallNonvirtualIntMethodA";
  return 0;
}

jlong
CallNonvirtualLongMethod (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallNonvirtualLongMethod";
  return 0;
}

jlong
CallNonvirtualLongMethodV (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallNonvirtualLongMethodV";
  return 0;
}

jlong
CallNonvirtualLongMethodA (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallNonvirtualLongMethodA";
  return 0;
}

jfloat
CallNonvirtualFloatMethod (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallNonvirtualFloatMethod";
  return 0;
}

jfloat
CallNonvirtualFloatMethodV (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallNonvirtualFloatMethodV";
  return 0;
}

jfloat
CallNonvirtualFloatMethodA (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallNonvirtualFloatMethodA";
  return 0;
}

jdouble
CallNonvirtualDoubleMethod (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallNonvirtualDoubleMethod";
  return 0;
}

jdouble
CallNonvirtualDoubleMethodV (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallNonvirtualDoubleMethodV";
  return 0;
}

jdouble
CallNonvirtualDoubleMethodA (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallNonvirtualDoubleMethodA";
  return 0;
}

void
CallNonvirtualVoidMethod (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallNonvirtualVoidMethod";
}

void
CallNonvirtualVoidMethodV (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallNonvirtualVoidMethodV";
}

void
CallNonvirtualVoidMethodA (JNIEnv *env, jobject obj, jclass clazz, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallNonvirtualVoidMethodA";
}

jfieldID
GetFieldID (JNIEnv *env, jclass clazz, const char *name, const char *sig)
{
  ADD_FAILURE () << "GetFieldID";
  return 0;
}

jobject
GetObjectField (JNIEnv *env, jobject obj, jfieldID fieldID)
{
  ADD_FAILURE () << "GetObjectField";
  return 0;
}

jboolean
GetBooleanField (JNIEnv *env, jobject obj, jfieldID fieldID)
{
  ADD_FAILURE () << "GetBooleanField";
  return 0;
}

jbyte
GetByteField (JNIEnv *env, jobject obj, jfieldID fieldID)
{
  ADD_FAILURE () << "GetByteField";
  return 0;
}

jchar
GetCharField (JNIEnv *env, jobject obj, jfieldID fieldID)
{
  ADD_FAILURE () << "GetCharField";
  return 0;
}

jshort
GetShortField (JNIEnv *env, jobject obj, jfieldID fieldID)
{
  ADD_FAILURE () << "GetShortField";
  return 0;
}

jint
GetIntField (JNIEnv *env, jobject obj, jfieldID fieldID)
{
  ADD_FAILURE () << "GetIntField";
  return 0;
}

jlong
GetLongField (JNIEnv *env, jobject obj, jfieldID fieldID)
{
  ADD_FAILURE () << "GetLongField";
  return 0;
}

jfloat
GetFloatField (JNIEnv *env, jobject obj, jfieldID fieldID)
{
  ADD_FAILURE () << "GetFloatField";
  return 0;
}

jdouble
GetDoubleField (JNIEnv *env, jobject obj, jfieldID fieldID)
{
  ADD_FAILURE () << "GetDoubleField";
  return 0;
}

void
SetObjectField (JNIEnv *env, jobject obj, jfieldID fieldID, jobject val)
{
  ADD_FAILURE () << "SetObjectField";
}

void
SetBooleanField (JNIEnv *env, jobject obj, jfieldID fieldID, jboolean val)
{
  ADD_FAILURE () << "SetBooleanField";
}

void
SetByteField (JNIEnv *env, jobject obj, jfieldID fieldID, jbyte val)
{
  ADD_FAILURE () << "SetByteField";
}

void
SetCharField (JNIEnv *env, jobject obj, jfieldID fieldID, jchar val)
{
  ADD_FAILURE () << "SetCharField";
}

void
SetShortField (JNIEnv *env, jobject obj, jfieldID fieldID, jshort val)
{
  ADD_FAILURE () << "SetShortField";
}

void
SetIntField (JNIEnv *env, jobject obj, jfieldID fieldID, jint val)
{
  ADD_FAILURE () << "SetIntField";
}

void
SetLongField (JNIEnv *env, jobject obj, jfieldID fieldID, jlong val)
{
  ADD_FAILURE () << "SetLongField";
}

void
SetFloatField (JNIEnv *env, jobject obj, jfieldID fieldID, jfloat val)
{
  ADD_FAILURE () << "SetFloatField";
}

void
SetDoubleField (JNIEnv *env, jobject obj, jfieldID fieldID, jdouble val)
{
  ADD_FAILURE () << "SetDoubleField";
}

jmethodID
GetStaticMethodID (JNIEnv *env, jclass clazz, const char *name, const char *sig)
{
  ADD_FAILURE () << "GetStaticMethodID";
  return 0;
}

jobject
CallStaticObjectMethod (JNIEnv *env, jclass clazz, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallStaticObjectMethod";
  return 0;
}

jobject
CallStaticObjectMethodV (JNIEnv *env, jclass clazz, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallStaticObjectMethodV";
  return 0;
}

jobject
CallStaticObjectMethodA (JNIEnv *env, jclass clazz, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallStaticObjectMethodA";
  return 0;
}

jboolean
CallStaticBooleanMethod (JNIEnv *env, jclass clazz, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallStaticBooleanMethod";
  return 0;
}

jboolean
CallStaticBooleanMethodV (JNIEnv *env, jclass clazz, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallStaticBooleanMethodV";
  return 0;
}

jboolean
CallStaticBooleanMethodA (JNIEnv *env, jclass clazz, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallStaticBooleanMethodA";
  return 0;
}

jbyte
CallStaticByteMethod (JNIEnv *env, jclass clazz, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallStaticByteMethod";
  return 0;
}

jbyte
CallStaticByteMethodV (JNIEnv *env, jclass clazz, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallStaticByteMethodV";
  return 0;
}

jbyte
CallStaticByteMethodA (JNIEnv *env, jclass clazz, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallStaticByteMethodA";
  return 0;
}

jchar
CallStaticCharMethod (JNIEnv *env, jclass clazz, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallStaticCharMethod";
  return 0;
}

jchar
CallStaticCharMethodV (JNIEnv *env, jclass clazz, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallStaticCharMethodV";
  return 0;
}

jchar
CallStaticCharMethodA (JNIEnv *env, jclass clazz, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallStaticCharMethodA";
  return 0;
}

jshort
CallStaticShortMethod (JNIEnv *env, jclass clazz, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallStaticShortMethod";
  return 0;
}

jshort
CallStaticShortMethodV (JNIEnv *env, jclass clazz, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallStaticShortMethodV";
  return 0;
}

jshort
CallStaticShortMethodA (JNIEnv *env, jclass clazz, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallStaticShortMethodA";
  return 0;
}

jint
CallStaticIntMethod (JNIEnv *env, jclass clazz, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallStaticIntMethod";
  return 0;
}

jint
CallStaticIntMethodV (JNIEnv *env, jclass clazz, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallStaticIntMethodV";
  return 0;
}

jint
CallStaticIntMethodA (JNIEnv *env, jclass clazz, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallStaticIntMethodA";
  return 0;
}

jlong
CallStaticLongMethod (JNIEnv *env, jclass clazz, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallStaticLongMethod";
  return 0;
}

jlong
CallStaticLongMethodV (JNIEnv *env, jclass clazz, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallStaticLongMethodV";
  return 0;
}

jlong
CallStaticLongMethodA (JNIEnv *env, jclass clazz, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallStaticLongMethodA";
  return 0;
}

jfloat
CallStaticFloatMethod (JNIEnv *env, jclass clazz, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallStaticFloatMethod";
  return 0;
}

jfloat
CallStaticFloatMethodV (JNIEnv *env, jclass clazz, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallStaticFloatMethodV";
  return 0;
}

jfloat
CallStaticFloatMethodA (JNIEnv *env, jclass clazz, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallStaticFloatMethodA";
  return 0;
}

jdouble
CallStaticDoubleMethod (JNIEnv *env, jclass clazz, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallStaticDoubleMethod";
  return 0;
}

jdouble
CallStaticDoubleMethodV (JNIEnv *env, jclass clazz, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallStaticDoubleMethodV";
  return 0;
}

jdouble
CallStaticDoubleMethodA (JNIEnv *env, jclass clazz, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallStaticDoubleMethodA";
  return 0;
}

void
CallStaticVoidMethod (JNIEnv *env, jclass cls, jmethodID methodID, ...)
{
  ADD_FAILURE () << "CallStaticVoidMethod";
}

void
CallStaticVoidMethodV (JNIEnv *env, jclass cls, jmethodID methodID, va_list args)
{
  ADD_FAILURE () << "CallStaticVoidMethodV";
}

void
CallStaticVoidMethodA (JNIEnv *env, jclass cls, jmethodID methodID, const jvalue *args)
{
  ADD_FAILURE () << "CallStaticVoidMethodA";
}

jfieldID
GetStaticFieldID (JNIEnv *env, jclass clazz, const char *name, const char *sig)
{
  ADD_FAILURE () << "GetStaticFieldID";
  return 0;
}

jobject
GetStaticObjectField (JNIEnv *env, jclass clazz, jfieldID fieldID)
{
  ADD_FAILURE () << "GetStaticObjectField";
  return 0;
}

jboolean
GetStaticBooleanField (JNIEnv *env, jclass clazz, jfieldID fieldID)
{
  ADD_FAILURE () << "GetStaticBooleanField";
  return 0;
}

jbyte
GetStaticByteField (JNIEnv *env, jclass clazz, jfieldID fieldID)
{
  ADD_FAILURE () << "GetStaticByteField";
  return 0;
}

jchar
GetStaticCharField (JNIEnv *env, jclass clazz, jfieldID fieldID)
{
  ADD_FAILURE () << "GetStaticCharField";
  return 0;
}

jshort
GetStaticShortField (JNIEnv *env, jclass clazz, jfieldID fieldID)
{
  ADD_FAILURE () << "GetStaticShortField";
  return 0;
}

jint
GetStaticIntField (JNIEnv *env, jclass clazz, jfieldID fieldID)
{
  ADD_FAILURE () << "GetStaticIntField";
  return 0;
}

jlong
GetStaticLongField (JNIEnv *env, jclass clazz, jfieldID fieldID)
{
  ADD_FAILURE () << "GetStaticLongField";
  return 0;
}

jfloat
GetStaticFloatField (JNIEnv *env, jclass clazz, jfieldID fieldID)
{
  ADD_FAILURE () << "GetStaticFloatField";
  return 0;
}

jdouble
GetStaticDoubleField (JNIEnv *env, jclass clazz, jfieldID fieldID)
{
  ADD_FAILURE () << "GetStaticDoubleField";
  return 0;
}

void
SetStaticObjectField (JNIEnv *env, jclass clazz, jfieldID fieldID, jobject value)
{
  ADD_FAILURE () << "SetStaticObjectField";
}

void
SetStaticBooleanField (JNIEnv *env, jclass clazz, jfieldID fieldID, jboolean value)
{
  ADD_FAILURE () << "SetStaticBooleanField";
}

void
SetStaticByteField (JNIEnv *env, jclass clazz, jfieldID fieldID, jbyte value)
{
  ADD_FAILURE () << "SetStaticByteField";
}

void
SetStaticCharField (JNIEnv *env, jclass clazz, jfieldID fieldID, jchar value)
{
  ADD_FAILURE () << "SetStaticCharField";
}

void
SetStaticShortField (JNIEnv *env, jclass clazz, jfieldID fieldID, jshort value)
{
  ADD_FAILURE () << "SetStaticShortField";
}

void
SetStaticIntField (JNIEnv *env, jclass clazz, jfieldID fieldID, jint value)
{
  ADD_FAILURE () << "SetStaticIntField";
}

void
SetStaticLongField (JNIEnv *env, jclass clazz, jfieldID fieldID, jlong value)
{
  ADD_FAILURE () << "SetStaticLongField";
}

void
SetStaticFloatField (JNIEnv *env, jclass clazz, jfieldID fieldID, jfloat value)
{
  ADD_FAILURE () << "SetStaticFloatField";
}

void
SetStaticDoubleField (JNIEnv *env, jclass clazz, jfieldID fieldID, jdouble value)
{
  ADD_FAILURE () << "SetStaticDoubleField";
}

jstring
NewString (JNIEnv *env, const jchar *unicode, jsize len)
{
  ADD_FAILURE () << "NewString";
  return 0;
}

jsize
GetStringLength (JNIEnv *env, jstring str)
{
  ADD_FAILURE () << "GetStringLength";
  return 0;
}

const jchar *
GetStringChars (JNIEnv *env, jstring str, jboolean *isCopy)
{
  ADD_FAILURE () << "GetStringChars";
  return 0;
}

void
ReleaseStringChars (JNIEnv *env, jstring str, const jchar *chars)
{
  ADD_FAILURE () << "ReleaseStringChars";
}

jstring
NewStringUTF (JNIEnv *env, const char *utf)
{
  ADD_FAILURE () << "NewStringUTF";
  return 0;
}

jsize
GetStringUTFLength (JNIEnv *env, jstring str)
{
  ADD_FAILURE () << "GetStringUTFLength";
  return 0;
}

const char *
GetStringUTFChars (JNIEnv *env, jstring str, jboolean *isCopy)
{
  ADD_FAILURE () << "GetStringUTFChars";
  return 0;
}

void
ReleaseStringUTFChars (JNIEnv *env, jstring str, const char *chars)
{
  ADD_FAILURE () << "ReleaseStringUTFChars";
}

jsize
GetArrayLength (JNIEnv *env, jarray array)
{
  ADD_FAILURE () << "GetArrayLength";
  return 0;
}

jobjectArray
NewObjectArray (JNIEnv *env, jsize len, jclass clazz, jobject init)
{
  ADD_FAILURE () << "NewObjectArray";
  return 0;
}

jobject
GetObjectArrayElement (JNIEnv *env, jobjectArray array, jsize index)
{
  ADD_FAILURE () << "GetObjectArrayElement";
  return 0;
}

void
SetObjectArrayElement (JNIEnv *env, jobjectArray array, jsize index, jobject val)
{
  ADD_FAILURE () << "SetObjectArrayElement";
}

jbooleanArray
NewBooleanArray (JNIEnv *env, jsize len)
{
  ADD_FAILURE () << "NewBooleanArray";
  return 0;
}

jbyteArray
NewByteArray (JNIEnv *env, jsize len)
{
  ADD_FAILURE () << "NewByteArray";
  return 0;
}

jcharArray
NewCharArray (JNIEnv *env, jsize len)
{
  ADD_FAILURE () << "NewCharArray";
  return 0;
}

jshortArray
NewShortArray (JNIEnv *env, jsize len)
{
  ADD_FAILURE () << "NewShortArray";
  return 0;
}

jintArray
NewIntArray (JNIEnv *env, jsize len)
{
  ADD_FAILURE () << "NewIntArray";
  return 0;
}

jlongArray
NewLongArray (JNIEnv *env, jsize len)
{
  ADD_FAILURE () << "NewLongArray";
  return 0;
}

jfloatArray
NewFloatArray (JNIEnv *env, jsize len)
{
  ADD_FAILURE () << "NewFloatArray";
  return 0;
}

jdoubleArray
NewDoubleArray (JNIEnv *env, jsize len)
{
  ADD_FAILURE () << "NewDoubleArray";
  return 0;
}

jboolean *
GetBooleanArrayElements (JNIEnv *env, jbooleanArray array, jboolean *isCopy)
{
  ADD_FAILURE () << "GetBooleanArrayElements";
  return 0;
}

jbyte *
GetByteArrayElements (JNIEnv *env, jbyteArray array, jboolean *isCopy)
{
  ADD_FAILURE () << "GetByteArrayElements";
  return 0;
}

jchar *
GetCharArrayElements (JNIEnv *env, jcharArray array, jboolean *isCopy)
{
  ADD_FAILURE () << "GetCharArrayElements";
  return 0;
}

jshort *
GetShortArrayElements (JNIEnv *env, jshortArray array, jboolean *isCopy)
{
  ADD_FAILURE () << "GetShortArrayElements";
  return 0;
}

jint *
GetIntArrayElements (JNIEnv *env, jintArray array, jboolean *isCopy)
{
  ADD_FAILURE () << "GetIntArrayElements";
  return 0;
}

jlong *
GetLongArrayElements (JNIEnv *env, jlongArray array, jboolean *isCopy)
{
  ADD_FAILURE () << "GetLongArrayElements";
  return 0;
}

jfloat *
GetFloatArrayElements (JNIEnv *env, jfloatArray array, jboolean *isCopy)
{
  ADD_FAILURE () << "GetFloatArrayElements";
  return 0;
}

jdouble *
GetDoubleArrayElements (JNIEnv *env, jdoubleArray array, jboolean *isCopy)
{
  ADD_FAILURE () << "GetDoubleArrayElements";
  return 0;
}

void
ReleaseBooleanArrayElements (JNIEnv *env, jbooleanArray array, jboolean *elems, jint mode)
{
  ADD_FAILURE () << "ReleaseBooleanArrayElements";
}

void
ReleaseByteArrayElements (JNIEnv *env, jbyteArray array, jbyte *elems, jint mode)
{
  ADD_FAILURE () << "ReleaseByteArrayElements";
}

void
ReleaseCharArrayElements (JNIEnv *env, jcharArray array, jchar *elems, jint mode)
{
  ADD_FAILURE () << "ReleaseCharArrayElements";
}

void
ReleaseShortArrayElements (JNIEnv *env, jshortArray array, jshort *elems, jint mode)
{
  ADD_FAILURE () << "ReleaseShortArrayElements";
}

void
ReleaseIntArrayElements (JNIEnv *env, jintArray array, jint *elems, jint mode)
{
  ADD_FAILURE () << "ReleaseIntArrayElements";
}

void
ReleaseLongArrayElements (JNIEnv *env, jlongArray array, jlong *elems, jint mode)
{
  ADD_FAILURE () << "ReleaseLongArrayElements";
}

void
ReleaseFloatArrayElements (JNIEnv *env, jfloatArray array, jfloat *elems, jint mode)
{
  ADD_FAILURE () << "ReleaseFloatArrayElements";
}

void
ReleaseDoubleArrayElements (JNIEnv *env, jdoubleArray array, jdouble *elems, jint mode)
{
  ADD_FAILURE () << "ReleaseDoubleArrayElements";
}

void
GetBooleanArrayRegion (JNIEnv *env, jbooleanArray array, jsize start, jsize l, jboolean *buf)
{
  ADD_FAILURE () << "GetBooleanArrayRegion";
}

void
GetByteArrayRegion (JNIEnv *env, jbyteArray array, jsize start, jsize len, jbyte *buf)
{
  ADD_FAILURE () << "GetByteArrayRegion";
}

void
GetCharArrayRegion (JNIEnv *env, jcharArray array, jsize start, jsize len, jchar *buf)
{
  ADD_FAILURE () << "GetCharArrayRegion";
}

void
GetShortArrayRegion (JNIEnv *env, jshortArray array, jsize start, jsize len, jshort *buf)
{
  ADD_FAILURE () << "GetShortArrayRegion";
}

void
GetIntArrayRegion (JNIEnv *env, jintArray array, jsize start, jsize len, jint *buf)
{
  ADD_FAILURE () << "GetIntArrayRegion";
}

void
GetLongArrayRegion (JNIEnv *env, jlongArray array, jsize start, jsize len, jlong *buf)
{
  ADD_FAILURE () << "GetLongArrayRegion";
}

void
GetFloatArrayRegion (JNIEnv *env, jfloatArray array, jsize start, jsize len, jfloat *buf)
{
  ADD_FAILURE () << "GetFloatArrayRegion";
}

void
GetDoubleArrayRegion (JNIEnv *env, jdoubleArray array, jsize start, jsize len, jdouble *buf)
{
  ADD_FAILURE () << "GetDoubleArrayRegion";
}

void
SetBooleanArrayRegion (JNIEnv *env, jbooleanArray array, jsize start, jsize l, const jboolean *buf)
{
  ADD_FAILURE () << "SetBooleanArrayRegion";
}

void
SetByteArrayRegion (JNIEnv *env, jbyteArray array, jsize start, jsize len, const jbyte *buf)
{
  ADD_FAILURE () << "SetByteArrayRegion";
}

void
SetCharArrayRegion (JNIEnv *env, jcharArray array, jsize start, jsize len, const jchar *buf)
{
  ADD_FAILURE () << "SetCharArrayRegion";
}

void
SetShortArrayRegion (JNIEnv *env, jshortArray array, jsize start, jsize len, const jshort *buf)
{
  ADD_FAILURE () << "SetShortArrayRegion";
}

void
SetIntArrayRegion (JNIEnv *env, jintArray array, jsize start, jsize len, const jint *buf)
{
  ADD_FAILURE () << "SetIntArrayRegion";
}

void
SetLongArrayRegion (JNIEnv *env, jlongArray array, jsize start, jsize len, const jlong *buf)
{
  ADD_FAILURE () << "SetLongArrayRegion";
}

void
SetFloatArrayRegion (JNIEnv *env, jfloatArray array, jsize start, jsize len, const jfloat *buf)
{
  ADD_FAILURE () << "SetFloatArrayRegion";
}

void
SetDoubleArrayRegion (JNIEnv *env, jdoubleArray array, jsize start, jsize len, const jdouble *buf)
{
  ADD_FAILURE () << "SetDoubleArrayRegion";
}

jint
RegisterNatives (JNIEnv *env, jclass clazz, const JNINativeMethod *methods, jint nMethods)
{
  ADD_FAILURE () << "RegisterNatives";
  return 0;
}

jint
UnregisterNatives (JNIEnv *env, jclass clazz)
{
  ADD_FAILURE () << "UnregisterNatives";
  return 0;
}

jint
MonitorEnter (JNIEnv *env, jobject obj)
{
  ADD_FAILURE () << "MonitorEnter";
  return 0;
}

jint
MonitorExit (JNIEnv *env, jobject obj)
{
  ADD_FAILURE () << "MonitorExit";
  return 0;
}

jint
GetJavaVM (JNIEnv *env, JavaVM **vm)
{
  ADD_FAILURE () << "GetJavaVM";
  return 0;
}

void
GetStringRegion (JNIEnv *env, jstring str, jsize start, jsize len, jchar *buf)
{
  ADD_FAILURE () << "GetStringRegion";
}

void
GetStringUTFRegion (JNIEnv *env, jstring str, jsize start, jsize len, char *buf)
{
  ADD_FAILURE () << "GetStringUTFRegion";
}

void *
GetPrimitiveArrayCritical (JNIEnv *env, jarray array, jboolean *isCopy)
{
  ADD_FAILURE () << "GetPrimitiveArrayCritical";
  return 0;
}

void
ReleasePrimitiveArrayCritical (JNIEnv *env, jarray array, void *carray, jint mode)
{
  ADD_FAILURE () << "ReleasePrimitiveArrayCritical";
}

const jchar *
GetStringCritical (JNIEnv *env, jstring string, jboolean *isCopy)
{
  ADD_FAILURE () << "GetStringCritical";
  return 0;
}

void
ReleaseStringCritical (JNIEnv *env, jstring string, const jchar *cstring)
{
  ADD_FAILURE () << "ReleaseStringCritical";
}

jweak
NewWeakGlobalRef (JNIEnv *env, jobject obj)
{
  ADD_FAILURE () << "NewWeakGlobalRef";
  return 0;
}

void
DeleteWeakGlobalRef (JNIEnv *env, jweak ref)
{
  ADD_FAILURE () << "DeleteWeakGlobalRef";
}

jboolean
ExceptionCheck (JNIEnv *env)
{
  ADD_FAILURE () << "ExceptionCheck";
  return 0;
}

jobject
NewDirectByteBuffer (JNIEnv *env, void *address, jlong capacity)
{
  ADD_FAILURE () << "NewDirectByteBuffer";
  return 0;
}

void *
GetDirectBufferAddress (JNIEnv *env, jobject buf)
{
  ADD_FAILURE () << "GetDirectBufferAddress";
  return 0;
}

jlong
GetDirectBufferCapacity (JNIEnv *env, jobject buf)
{
  ADD_FAILURE () << "GetDirectBufferCapacity";
  return 0;
}

/* New JNI 1.6 Features */

jobjectRefType
GetObjectRefType (JNIEnv *env, jobject obj)
{
  ADD_FAILURE () << "GetObjectRefType";
  return { };
}


static mock_jni &
self (JNIEnv *env)
{
  return *static_cast<mock_jni *> (env);
}


static JNINativeInterface_ const functions = {
  nullptr,
  nullptr,
  nullptr,
  nullptr,

  GetVersion,

  DefineClass,
  [] (JNIEnv *env, const char *name) { return self (env).FindClass (name); },

  FromReflectedMethod,
  FromReflectedField,

  ToReflectedMethod,

  GetSuperclass,
  IsAssignableFrom,

  ToReflectedField,

  Throw,
  [] (JNIEnv *env, jclass clazz, const char *msg) { return self (env).ThrowNew (clazz, msg); },
  ExceptionOccurred,
  ExceptionDescribe,
  ExceptionClear,
  FatalError,

  PushLocalFrame,
  PopLocalFrame,

  NewGlobalRef,
  DeleteGlobalRef,
  DeleteLocalRef,
  IsSameObject,
  NewLocalRef,
  EnsureLocalCapacity,

  AllocObject,
  NewObject,
  NewObjectV,
  NewObjectA,

  GetObjectClass,
  IsInstanceOf,

  GetMethodID,

  CallObjectMethod,
  CallObjectMethodV,
  CallObjectMethodA,

  CallBooleanMethod,
  CallBooleanMethodV,
  CallBooleanMethodA,

  CallByteMethod,
  CallByteMethodV,
  CallByteMethodA,

  CallCharMethod,
  CallCharMethodV,
  CallCharMethodA,

  CallShortMethod,
  CallShortMethodV,
  CallShortMethodA,

  CallIntMethod,
  CallIntMethodV,
  CallIntMethodA,

  CallLongMethod,
  CallLongMethodV,
  CallLongMethodA,

  CallFloatMethod,
  CallFloatMethodV,
  CallFloatMethodA,

  CallDoubleMethod,
  CallDoubleMethodV,
  CallDoubleMethodA,

  CallVoidMethod,
  CallVoidMethodV,
  CallVoidMethodA,

  CallNonvirtualObjectMethod,
  CallNonvirtualObjectMethodV,
  CallNonvirtualObjectMethodA,

  CallNonvirtualBooleanMethod,
  CallNonvirtualBooleanMethodV,
  CallNonvirtualBooleanMethodA,

  CallNonvirtualByteMethod,
  CallNonvirtualByteMethodV,
  CallNonvirtualByteMethodA,

  CallNonvirtualCharMethod,
  CallNonvirtualCharMethodV,
  CallNonvirtualCharMethodA,

  CallNonvirtualShortMethod,
  CallNonvirtualShortMethodV,
  CallNonvirtualShortMethodA,

  CallNonvirtualIntMethod,
  CallNonvirtualIntMethodV,
  CallNonvirtualIntMethodA,

  CallNonvirtualLongMethod,
  CallNonvirtualLongMethodV,
  CallNonvirtualLongMethodA,

  CallNonvirtualFloatMethod,
  CallNonvirtualFloatMethodV,
  CallNonvirtualFloatMethodA,

  CallNonvirtualDoubleMethod,
  CallNonvirtualDoubleMethodV,
  CallNonvirtualDoubleMethodA,

  CallNonvirtualVoidMethod,
  CallNonvirtualVoidMethodV,
  CallNonvirtualVoidMethodA,

  GetFieldID,

  GetObjectField,
  GetBooleanField,
  GetByteField,
  GetCharField,
  GetShortField,
  GetIntField,
  GetLongField,
  GetFloatField,
  GetDoubleField,

  SetObjectField,
  SetBooleanField,
  SetByteField,
  SetCharField,
  SetShortField,
  SetIntField,
  SetLongField,
  SetFloatField,
  SetDoubleField,

  GetStaticMethodID,

  CallStaticObjectMethod,
  CallStaticObjectMethodV,
  CallStaticObjectMethodA,

  CallStaticBooleanMethod,
  CallStaticBooleanMethodV,
  CallStaticBooleanMethodA,

  CallStaticByteMethod,
  CallStaticByteMethodV,
  CallStaticByteMethodA,

  CallStaticCharMethod,
  CallStaticCharMethodV,
  CallStaticCharMethodA,

  CallStaticShortMethod,
  CallStaticShortMethodV,
  CallStaticShortMethodA,

  CallStaticIntMethod,
  CallStaticIntMethodV,
  CallStaticIntMethodA,

  CallStaticLongMethod,
  CallStaticLongMethodV,
  CallStaticLongMethodA,

  CallStaticFloatMethod,
  CallStaticFloatMethodV,
  CallStaticFloatMethodA,

  CallStaticDoubleMethod,
  CallStaticDoubleMethodV,
  CallStaticDoubleMethodA,

  CallStaticVoidMethod,
  CallStaticVoidMethodV,
  CallStaticVoidMethodA,

  GetStaticFieldID,
  GetStaticObjectField,
  GetStaticBooleanField,
  GetStaticByteField,
  GetStaticCharField,
  GetStaticShortField,
  GetStaticIntField,
  GetStaticLongField,
  GetStaticFloatField,
  GetStaticDoubleField,

  SetStaticObjectField,
  SetStaticBooleanField,
  SetStaticByteField,
  SetStaticCharField,
  SetStaticShortField,
  SetStaticIntField,
  SetStaticLongField,
  SetStaticFloatField,
  SetStaticDoubleField,

  NewString,
  GetStringLength,
  GetStringChars,
  ReleaseStringChars,

  NewStringUTF,
  GetStringUTFLength,
  GetStringUTFChars,
  ReleaseStringUTFChars,


  GetArrayLength,

  NewObjectArray,
  GetObjectArrayElement,
  SetObjectArrayElement,

  NewBooleanArray,
  NewByteArray,
  NewCharArray,
  NewShortArray,
  NewIntArray,
  NewLongArray,
  NewFloatArray,
  NewDoubleArray,

  GetBooleanArrayElements,
  GetByteArrayElements,
  GetCharArrayElements,
  GetShortArrayElements,
  GetIntArrayElements,
  GetLongArrayElements,
  GetFloatArrayElements,
  GetDoubleArrayElements,

  ReleaseBooleanArrayElements,
  ReleaseByteArrayElements,
  ReleaseCharArrayElements,
  ReleaseShortArrayElements,
  ReleaseIntArrayElements,
  ReleaseLongArrayElements,
  ReleaseFloatArrayElements,
  ReleaseDoubleArrayElements,

  GetBooleanArrayRegion,
  GetByteArrayRegion,
  GetCharArrayRegion,
  GetShortArrayRegion,
  GetIntArrayRegion,
  GetLongArrayRegion,
  GetFloatArrayRegion,
  GetDoubleArrayRegion,

  SetBooleanArrayRegion,
  SetByteArrayRegion,
  SetCharArrayRegion,
  SetShortArrayRegion,
  SetIntArrayRegion,
  SetLongArrayRegion,
  SetFloatArrayRegion,
  SetDoubleArrayRegion,

  RegisterNatives,
  UnregisterNatives,

  MonitorEnter,
  MonitorExit,

  GetJavaVM,

  GetStringRegion,
  GetStringUTFRegion,

  GetPrimitiveArrayCritical,
  ReleasePrimitiveArrayCritical,

  GetStringCritical,
  ReleaseStringCritical,

  NewWeakGlobalRef,
  DeleteWeakGlobalRef,

  ExceptionCheck,

  NewDirectByteBuffer,
  GetDirectBufferAddress,
  GetDirectBufferCapacity,

  /* New JNI 1.6 Features */

  GetObjectRefType,
};


mock_jni *
mock_jnienv ()
{
  return new mock_jni { &functions };
}
