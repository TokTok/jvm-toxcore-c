package im.tox.core.typesafe

abstract class FixedSizeByteArrayCompanionTest[T <: AnyVal, S <: Security](module: FixedSizeByteArrayCompanion[T, S])
  extends ByteArrayCompanionTest(module)
