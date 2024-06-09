package fraktal.io.android.demo.shared.models.chat

import kotlinx.datetime.LocalDateTime

class ChatMessage(
    val senderId: Long,
    val senderName: String,
    val messageId: Long,
    val time: LocalDateTime,
    val content: String,
    val isMe: Boolean
)

typealias ChatMessagesPart = List<ChatMessage>

fun ChatMessage.toChatMessagesPart(): ChatMessagesPart {
    return listOf(this)
}
