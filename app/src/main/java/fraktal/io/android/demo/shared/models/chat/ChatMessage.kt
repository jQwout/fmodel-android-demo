package fraktal.io.android.demo.shared.models.chat

import kotlinx.datetime.LocalDate

class ChatMessage(
    val senderId: Long,
    val senderName: String,
    val time: LocalDate,
    val content: String
)