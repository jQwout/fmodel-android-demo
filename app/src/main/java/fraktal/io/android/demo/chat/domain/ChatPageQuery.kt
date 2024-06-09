package fraktal.io.android.demo.chat.domain

import fraktal.io.ext.Query

typealias ChatQuery = Query<ChatQSE.State, ChatPageCSE.Event>

fun chatQuery() = ChatQuery(
    initialState = ChatQSE.State(emptyList(), 0, false, false, null),
    evolve = { state, event ->
        when (event) {
            is ChatPageCSE.Event.Loading -> state.copy(
                initLoading = state.list.isEmpty(),
                nextPageLoading = state.list.isNotEmpty(),
                messagesErrorStr = null
            )

            is ChatPageCSE.Event.OnLoadError -> state.copy(messagesErrorStr = "Something went wrong")
            is ChatPageCSE.Event.OnPageLoaded -> {
                state.copy(
                    pages = event.pageIndex,
                    list = (state.list + event.list).sortedBy { it.time },
                    messagesErrorStr = null
                )
            }

            is ChatPageCSE.Event.OnSendError -> state
        }
    }
)