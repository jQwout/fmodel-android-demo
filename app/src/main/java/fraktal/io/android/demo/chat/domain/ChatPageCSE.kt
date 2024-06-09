package fraktal.io.android.demo.chat.domain

import fraktal.io.android.demo.shared.models.chat.ChatMessage

object ChatPageCSE {

    sealed interface Command {

        class Load(
            val offset: Int,
            val count: Int,
            val pageIndex: Int
        ) : Command


        class Send(
            val content: String
        ) : Command
    }

    data class State(
        val isLoading: Boolean,
        val offset: Int,
        val count: Int,
        val pageIndex: Int,
        val throwable: Throwable? = null
    )

    sealed interface Event {

        class Loading(val pageIndex: Int) : Event

        class OnPageLoaded(
            val pageIndex: Int,
            val list: List<ChatMessage>,
        ) : Event

        class OnLoadError(val throwable: Throwable) : Event

        class OnSendError(val throwable: Throwable) : Event

    }

}