package fraktal.io.android.demo.chat.domain

import com.fraktalio.fmodel.domain.Decider
import com.fraktalio.fmodel.domain.decider
import fraktal.io.android.demo.shared.repository.MessagesRepository
import fraktal.io.android.demo.shared.service.ChatUpdateService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

typealias ChatPageDecider = Decider<ChatPageCSE.Command, ChatPageCSE.State, ChatPageCSE.Event>

fun chatPageDecider(
    chatUpdateService: ChatUpdateService,
    messagesRepository: MessagesRepository
): ChatPageDecider = decider {
    initialState { ChatPageCSE.State(true, 0, 50, 0) }
    decide { command, state ->
        when (command) {
            is ChatPageCSE.Command.Load -> {
                flow {
                    // just without paging
                    emit(ChatPageCSE.Event.Loading(command.pageIndex))
                    try {
                        val all = messagesRepository.getAll()
                        emit(ChatPageCSE.Event.OnPageLoaded(command.pageIndex, all))
                    } catch (e: Throwable) {
                        emit(ChatPageCSE.Event.OnLoadError(e))
                    }
                }
            }

            is ChatPageCSE.Command.Send -> {
                flow {
                    try {
                        chatUpdateService.sendFromMe(command.content)
                    } catch (e: Throwable) {
                        emit(ChatPageCSE.Event.OnSendError(e))
                    }
                }
            }
        }
    }
    evolve { state, event ->
        when (event) {
            is ChatPageCSE.Event.Loading -> {
                state.copy(
                    isLoading = false
                )
            }

            is ChatPageCSE.Event.OnLoadError -> {
                state.copy(
                    throwable = event.throwable
                )
            }

            is ChatPageCSE.Event.OnPageLoaded -> {
                state.copy(
                    pageIndex = event.pageIndex,
                )
            }

            is ChatPageCSE.Event.OnSendError -> {
                state.copy(
                    throwable = event.throwable
                )
            }
        }
    }
}
