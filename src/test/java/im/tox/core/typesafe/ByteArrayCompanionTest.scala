package im.tox.core.typesafe

abstract class ByteArrayCompanionTest[T <: AnyVal, S <: Security](module: ByteArrayCompanion[T, S])
  extends WrappedValueCompanionTest[Array[Byte], T, S](module)
