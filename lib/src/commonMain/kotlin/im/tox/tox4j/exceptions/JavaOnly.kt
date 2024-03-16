package im.tox.tox4j.exceptions

/**
 * Annotation to mark error codes in Java exception enums as Java-only, so they are not emitted as
 * part of the error code conversion fragments in C++ (see {@link
 * im.tox.tox4j.impl.jni.codegen.JniErrorCodes}).
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class JavaOnly
