import sbt.Keys._
import sbt._
import sbt.tox4j.OptionalPlugin
import wartremover.WartRemover.autoImport._

object WartRemoverOverrides extends OptionalPlugin {

  object Keys

  private def custom(checker: String): wartremover.Wart = {
    Wart.custom(s"im.tox.tox4j.lint.$checker")
  }

  override val moduleSettings = Seq(
    wartremoverExcluded := {
      val basePath = (sourceManaged in Compile).value / "compiled_protobuf" / "im" / "tox"

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
      ).map(_ + ".scala").map(basePath / "tox4j" / "av" / "proto" / _)

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
      ).map(_ + ".scala").map(basePath / "tox4j" / "core" / "proto" / _)

      val protoLogProtos = Seq(
        "JniLogEntry",
        "JniLog",
        "Member",
        "Struct",
        "Timestamp",
        "Value",
        "InternalFields_ProtoLogProto"
      ).map(_ + ".scala").map(basePath / "tox4j" / "impl" / "jni" / "proto" / _)

      val clientProtos = Seq(
        "Profile",
        "Profiles",
        "InternalFields_ClientProfileProto"
      ).map(_ + ".scala").map(basePath / "client" / "proto" / _)

      avProtos ++ coreProtos ++ protoLogProtos ++ clientProtos
    }
  )

}
