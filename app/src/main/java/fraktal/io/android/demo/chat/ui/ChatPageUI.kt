package fraktal.io.android.demo.chat.ui

import fraktal.io.android.demo.chat.domain.ChatPageCSE
import fraktal.io.android.demo.chat.domain.ChatQSE
import fraktal.io.android.demo.shared.models.chat.ChatMessage
import fraktal.io.ext.FViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class ChatPageUI(
    val messages: ImmutableList<ChatMessage>,
    val pages: Int,
    val initLoading: Boolean,
    val messagesErrorStr: String?
) {

    fun asChatQSEState() = ChatQSE.State(
        messages,
        pages,
        initLoading,
        false, // TODO
        messagesErrorStr
    )
}

fun ChatQSE.State.asChatPageUI() = ChatPageUI(
    messages = list.toPersistentList(),
    pages = pages,
    initLoading = initLoading,
    messagesErrorStr = messagesErrorStr,
)

typealias ChatPagesViewModels = FViewModel<ChatPageCSE.Command, *, ChatPageUI, ChatPageCSE.Event>