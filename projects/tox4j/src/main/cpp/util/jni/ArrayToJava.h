#pragma once

#include <type_traits>
#include <vector>

#include <jni.h>


/*****************************************************************************
 *
 * C++ arrays as Java arrays.
 *
 *****************************************************************************/


namespace detail
{


template<
  typename JType,
  typename JavaArray,
  JavaArray (JNIEnv::*New) (jsize size),
  void (JNIEnv::*Set) (JavaArray, jsize, jsize, JType const *)
>
struct MakeArrayToJava
{
  typedef JType java_type;
  typedef JavaArray array_type;

  static JavaArray make (JNIEnv *env, jsize size, JType const *data)
  {
    JavaArray array = (env->*New) (size);

    (env->*Set) (array, 0, size, data);
    return array;
  }
};


template<typename JType>
struct ArrayToJava;

template<> struct ArrayToJava<jboolean> { typedef MakeArrayToJava<jboolean, jbooleanArray, &JNIEnv::NewBooleanArray, &JNIEnv::SetBooleanArrayRegion> type; };
template<> struct ArrayToJava<jbyte   > { typedef MakeArrayToJava<jbyte   , jbyteArray   , &JNIEnv::NewByteArray   , &JNIEnv::SetByteArrayRegion   > type; };
template<> struct ArrayToJava<jchar   > { typedef MakeArrayToJava<jchar   , jcharArray   , &JNIEnv::NewCharArray   , &JNIEnv::SetCharArrayRegion   > type; };
template<> struct ArrayToJava<jshort  > { typedef MakeArrayToJava<jshort  , jshortArray  , &JNIEnv::NewShortArray  , &JNIEnv::SetShortArrayRegion  > type; };
template<> struct ArrayToJava<jint    > { typedef MakeArrayToJava<jint    , jintArray    , &JNIEnv::NewIntArray    , &JNIEnv::SetIntArrayRegion    > type; };
template<> struct ArrayToJava<jlong   > { typedef MakeArrayToJava<jlong   , jlongArray   , &JNIEnv::NewLongArray   , &JNIEnv::SetLongArrayRegion   > type; };
template<> struct ArrayToJava<jfloat  > { typedef MakeArrayToJava<jfloat  , jfloatArray  , &JNIEnv::NewFloatArray  , &JNIEnv::SetFloatArrayRegion  > type; };
template<> struct ArrayToJava<jdouble > { typedef MakeArrayToJava<jdouble , jdoubleArray , &JNIEnv::NewDoubleArray , &JNIEnv::SetDoubleArrayRegion > type; };


}


template<typename CType>
using ArrayToJava = typename detail::ArrayToJava<typename std::make_signed<CType>::type>::type;


template<typename T>
auto
toJavaArray (JNIEnv *env, std::vector<T> const &data)
{
  typedef typename ArrayToJava<T>::java_type java_type;
  static_assert (sizeof (T) == sizeof (java_type),
    "Size requirements for Java array not met");
  return ArrayToJava<T>::make (env, data.size (),
    reinterpret_cast<java_type const *> (data.data ()));
}


template<typename T, std::size_t N>
auto
toJavaArray (JNIEnv *env, T const (&data)[N])
{
  typedef typename ArrayToJava<T>::java_type java_type;
  static_assert (sizeof (T) == sizeof (java_type),
    "Size requirements for Java array not met");
  return ArrayToJava<T>::make (env, N,
    reinterpret_cast<java_type const *> (data));
}
