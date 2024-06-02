package fraktal.io.android.demo.chat.domain

import fraktal.io.android.demo.shared.models.chat.ChatMessage


sealed interface ChatCommand {

    data object Load: ChatCommand

    data class Send(val chatMessage: ChatMessage): ChatCommand

}

