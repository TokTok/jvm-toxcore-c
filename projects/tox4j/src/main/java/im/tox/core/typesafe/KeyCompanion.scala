package im.tox.core.typesafe

abstract class KeyCompanion[T <: AnyVal, S <: Security](
  Size: Int,
  toValue: T => Array[Byte]
) extends FixedSizeByteArrayCompanion[T, S](Size, toValue)
