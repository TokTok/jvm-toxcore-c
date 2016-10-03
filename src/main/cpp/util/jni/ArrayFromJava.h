#pragma once

#include <jni.h>

#include <cstdint>
#include <memory>


/*****************************************************************************
 *
 * Java arrays as C++ arrays.
 *
 *****************************************************************************/


namespace detail
{


template<
  typename JType,
  typename JavaArray,
  void (JNIEnv::*ReleaseArrayElements) (JavaArray, JType *, jint)
>
struct JavaArrayDeleter
{
  JavaArrayDeleter (JNIEnv *env, JavaArray jArray)
    : env (env)
    , jArray (jArray)
  { }

  void operator () (JType *cArray) const
  {
    (env->*ReleaseArrayElements) (jArray, cArray, JNI_ABORT);
  }

  JNIEnv *const env;
  JavaArray const jArray;
};


template<
  typename JType,
  typename CType,
  typename JavaArray,
  JType *(JNIEnv::*GetArrayElements) (JavaArray, jboolean *),
  void (JNIEnv::*ReleaseArrayElements) (JavaArray, JType *, jint)
>
struct MakeArrayFromJava
{
  typedef std::unique_ptr<JType,
    JavaArrayDeleter<JType, JavaArray, ReleaseArrayElements>
  > array_pointer;

  MakeArrayFromJava (JNIEnv *env, JavaArray jArray)
    : cArray (
        jArray ? (env->*GetArrayElements) (jArray, nullptr) : nullptr,
        typename array_pointer::deleter_type (env, jArray)
      )
  {
  }

  CType const *
  data () const
  {
    static_assert (std::is_same<JType, typename std::make_signed<CType>::type>::value,
      "Java array element type should be the same as the C element type modulo signedness");
    static_assert (sizeof (JType) == sizeof (CType),
      "Size requirements for Java array not met");
    return reinterpret_cast<CType const *> (cArray.get ());
  }

  size_t size () const { return jArray () ? env ()->GetArrayLength (jArray ()) : 0; }
  bool empty () const { return size () == 0; }

private:
  JNIEnv *env      () const { return cArray.get_deleter ().env   ; }
  JavaArray jArray () const { return cArray.get_deleter ().jArray; }

  array_pointer cArray;
};


template<typename JavaArray>
struct ArrayFromJava;

template<> struct ArrayFromJava<jbooleanArray> { typedef MakeArrayFromJava<jboolean, bool    , jbooleanArray, &JNIEnv::GetBooleanArrayElements, &JNIEnv::ReleaseBooleanArrayElements> type; };
template<> struct ArrayFromJava<jbyteArray   > { typedef MakeArrayFromJava<jbyte   , uint8_t , jbyteArray   , &JNIEnv::GetByteArrayElements   , &JNIEnv::ReleaseByteArrayElements   > type; };
template<> struct ArrayFromJava<jcharArray   > { typedef MakeArrayFromJava<jchar   , uint16_t, jcharArray   , &JNIEnv::GetCharArrayElements   , &JNIEnv::ReleaseCharArrayElements   > type; };
template<> struct ArrayFromJava<jshortArray  > { typedef MakeArrayFromJava<jshort  , int16_t , jshortArray  , &JNIEnv::GetShortArrayElements  , &JNIEnv::ReleaseShortArrayElements  > type; };
template<> struct ArrayFromJava<jintArray    > { typedef MakeArrayFromJava<jint    , int32_t , jintArray    , &JNIEnv::GetIntArrayElements    , &JNIEnv::ReleaseIntArrayElements    > type; };
template<> struct ArrayFromJava<jlongArray   > { typedef MakeArrayFromJava<jlong   , int64_t , jlongArray   , &JNIEnv::GetLongArrayElements   , &JNIEnv::ReleaseLongArrayElements   > type; };
template<> struct ArrayFromJava<jfloatArray  > { typedef MakeArrayFromJava<jfloat  , float   , jfloatArray  , &JNIEnv::GetFloatArrayElements  , &JNIEnv::ReleaseFloatArrayElements  > type; };
template<> struct ArrayFromJava<jdoubleArray > { typedef MakeArrayFromJava<jdouble , double  , jdoubleArray , &JNIEnv::GetDoubleArrayElements , &JNIEnv::ReleaseDoubleArrayElements > type; };


}


template<typename JavaArray>
using ArrayFromJava = typename detail::ArrayFromJava<JavaArray>::type;


template<typename JavaArray>
auto
fromJavaArray (JNIEnv *env, JavaArray jArray)
{
  return ArrayFromJava<JavaArray> (env, jArray);
}
