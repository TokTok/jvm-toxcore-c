package im.tox

import im.tox.documentation._

package object tox4j extends Documented {
  doc"""
  |Low level Tox network interfaces.
  |
  |The packages are organised by subsystem:
  |- A/V in ${av.name}
  |- Regular text messaging and group chats in ${core.name}
  |- Cryptographic primitives in ${crypto.name}
  |
  |The subsystem packages define data structures and interfaces for all low level Tox library operations. Various
  |implementations of these interfaces is contained within subpackages of the ${impl.name} package. The native
  |interface to the C library libtoxcore is located in ${impl.jni.name}.
  |
  |Each subsystem package contains one or more of the following subpackages:
  |- callbacks: Interfaces for the callbacks (event listeners). This package provides a combined Listener interface
  |    (e.g. ${ref[core.callbacks.ToxCoreEventListener[_]]} which inherits from all the single callback interfaces
  |    in the package. An Adapter class (e.g. ${ref[core.callbacks.ToxCoreEventAdapter[_]]}) provides empty default
  |    implementations for all callbacks. Run ${ref(impl.jni.codegen.JniCallbacks)} to update the JNI bridge.
  |- enums: Java enums mirroring the C versions exactly. The enums' ordinal does not necessarily correspond to the C
  |    enum value, but the JNI bridge needs to know the ordinals, so if you reorder these, then you must update the
  |    bridge, by running ${ref(impl.jni.codegen.JniEnums)}.
  |- exceptions: Exception classes corresponding to the C API's error codes. One exception class for each error enum in
  |    the C API. Each exception class contains a Code enum (e.g. ${ref[core.exceptions.ToxBootstrapException.Code]})
  |    with all the errors that can occur in Java. If you change these, you need to update the JNI bridge by running
  |    ${ref(impl.jni.codegen.JniErrorCodes)}.
  """
}
