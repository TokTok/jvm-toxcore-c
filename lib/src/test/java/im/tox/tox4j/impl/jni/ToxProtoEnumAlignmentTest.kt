package im.tox.tox4j.impl.jni

import com.google.protobuf.ProtocolMessageEnum
import im.tox.tox4j.core.enums.ToxConferenceType
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.core.enums.ToxFileControl
import im.tox.tox4j.core.enums.ToxGroupExitType
import im.tox.tox4j.core.enums.ToxGroupJoinFail
import im.tox.tox4j.core.enums.ToxGroupModEvent
import im.tox.tox4j.core.enums.ToxGroupPrivacyState
import im.tox.tox4j.core.enums.ToxGroupTopicLock
import im.tox.tox4j.core.enums.ToxGroupVoiceState
import im.tox.tox4j.core.enums.ToxLogLevel
import im.tox.tox4j.core.enums.ToxMessageType
import im.tox.tox4j.core.enums.ToxUserStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import im.tox.tox4j.core.proto.ConferenceType as ProtoConferenceType
import im.tox.tox4j.core.proto.Connection as ProtoConnection
import im.tox.tox4j.core.proto.FileControl as ProtoFileControl
import im.tox.tox4j.core.proto.GroupExitType as ProtoGroupExitType
import im.tox.tox4j.core.proto.GroupJoinFailKind as ProtoGroupJoinFailKind
import im.tox.tox4j.core.proto.GroupModEvent as ProtoGroupModEvent
import im.tox.tox4j.core.proto.GroupPrivacyStateKind as ProtoGroupPrivacyStateKind
import im.tox.tox4j.core.proto.GroupTopicLockKind as ProtoGroupTopicLockKind
import im.tox.tox4j.core.proto.GroupVoiceStateKind as ProtoGroupVoiceStateKind
import im.tox.tox4j.core.proto.LogLevel as ProtoLogLevel
import im.tox.tox4j.core.proto.MessageType as ProtoMessageType
import im.tox.tox4j.core.proto.UserStatus as ProtoUserStatus

/** The event dispatchers map a proto enum's `number` to the Kotlin `ordinal`. */
class ToxProtoEnumAlignmentTest {
    private fun <K : Enum<K>, P : ProtocolMessageEnum> assertAligned(
        kotlinValues: Array<K>,
        protoLookup: (String) -> P,
    ) {
        for (k in kotlinValues) {
            val p = protoLookup(k.name)
            assertEquals(
                k.ordinal,
                p.number,
                "Kotlin ${k::class.simpleName}.${k.name} has ordinal ${k.ordinal} " +
                    "but proto counterpart has number ${p.number}; a `.proto` reorder " +
                    "would silently mis-map dispatcher events.",
            )
        }
    }

    @Test
    fun userStatus_alignsProtoAndKotlin() = assertAligned(ToxUserStatus.values()) { ProtoUserStatus.Type.valueOf(it) }

    @Test
    fun connection_alignsProtoAndKotlin() = assertAligned(ToxConnection.values()) { ProtoConnection.Type.valueOf(it) }

    @Test
    fun fileControl_alignsProtoAndKotlin() = assertAligned(ToxFileControl.values()) { ProtoFileControl.Type.valueOf(it) }

    @Test
    fun conferenceType_alignsProtoAndKotlin() = assertAligned(ToxConferenceType.values()) { ProtoConferenceType.Type.valueOf(it) }

    @Test
    fun messageType_alignsProtoAndKotlin() = assertAligned(ToxMessageType.values()) { ProtoMessageType.Type.valueOf(it) }

    @Test
    fun groupExitType_alignsProtoAndKotlin() = assertAligned(ToxGroupExitType.values()) { ProtoGroupExitType.Type.valueOf(it) }

    @Test
    fun groupJoinFail_alignsProtoAndKotlin() = assertAligned(ToxGroupJoinFail.values()) { ProtoGroupJoinFailKind.Type.valueOf(it) }

    @Test
    fun groupModEvent_alignsProtoAndKotlin() = assertAligned(ToxGroupModEvent.values()) { ProtoGroupModEvent.Type.valueOf(it) }

    @Test
    fun groupPrivacyState_alignsProtoAndKotlin() =
        assertAligned(ToxGroupPrivacyState.values()) { ProtoGroupPrivacyStateKind.Type.valueOf(it) }

    @Test
    fun groupTopicLock_alignsProtoAndKotlin() = assertAligned(ToxGroupTopicLock.values()) { ProtoGroupTopicLockKind.Type.valueOf(it) }

    @Test
    fun groupVoiceState_alignsProtoAndKotlin() = assertAligned(ToxGroupVoiceState.values()) { ProtoGroupVoiceStateKind.Type.valueOf(it) }

    @Test
    fun logLevel_alignsProtoAndKotlin() = assertAligned(ToxLogLevel.values()) { ProtoLogLevel.Type.valueOf(it) }
}
