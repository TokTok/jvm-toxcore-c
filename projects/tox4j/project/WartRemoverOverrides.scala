import sbt.Keys._
import sbt._
import sbt.tox4j.OptionalPlugin
import wartremover.Wart
import wartremover.WartRemover.autoImport._

object WartRemoverOverrides extends OptionalPlugin {

  object Keys

  private def custom(checker: String): Wart = {
    Wart.custom(s"im.tox.tox4j.lint.$checker")
  }

  override val moduleSettings = Seq(
    wartremoverExcluded := {
      val proto = (sourceManaged in Compile).value / "compiled_protobuf" / "im" / "tox" / "tox4j"

      // TODO(iphydf): infer these
      val avProtos = Seq(
        "AudioReceiveFrame",
        "AvEvents",
        "BitRateStatus",
        "Call",
        "CallControl",
        "CallState",
        "InternalFields_avProto",
        "VideoReceiveFrame"
      ).map(_ + ".scala").map(proto / "av" / "proto" / _)

      val coreProtos = Seq(
        "Connection",
        "CoreEvents",
        "FileChunkRequest",
        "FileControl",
        "FileRecv",
        "FileRecvChunk",
        "FileRecvControl",
        "FriendConnectionStatus",
        "FriendLosslessPacket",
        "FriendLossyPacket",
        "FriendMessage",
        "FriendName",
        "FriendReadReceipt",
        "FriendRequest",
        "FriendStatus",
        "FriendStatusMessage",
        "FriendTyping",
        "InternalFields_coreProto",
        "MessageType",
        "SelfConnectionStatus",
        "UserStatus"
      ).map(_ + ".scala").map(proto / "core" / "proto" / _)

      val protoLogProtos = Seq(
        "JniLogEntry",
        "JniLog",
        "Member",
        "TimeVal",
        "Value",
        "InternalFields_ProtoLogProto"
      ).map(_ + ".scala").map(proto / "impl" / "jni" / "proto" / _)

      avProtos ++ coreProtos ++ protoLogProtos
    }
  )

}
