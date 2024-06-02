package fraktal.io.android.demo.shared.repository

import fraktal.io.android.demo.shared.models.chat.ChatMessage
import fraktal.io.android.demo.shared.models.worker.Worker
import fraktal.io.android.demo.workers.profile.domain.workerQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

interface ChatRepository {

    suspend fun send(chatMessage: ChatMessage)

    fun subscribeOf(workers: List<Worker>): Flow<List<ChatMessage>>
}


class FakeChatRepository(
    private val coroutineScope: CoroutineScope
) : ChatRepository {

    private val messages: MutableSharedFlow<List<ChatMessage>> = MutableSharedFlow(1, extraBufferCapacity = Int.MAX_VALUE)

    init {
        coroutineScope.launch {
            while (this.isActive) {

            }
        }
    }

    override suspend fun send(chatMessage: ChatMessage) {
        messages.emit(listOf(chatMessage))
    }

    override fun subscribeOf(workers: List<Worker>): Flow<List<ChatMessage>> {
        if (workers.isEmpty()) return emptyFlow()
        return messages
    }

    private suspend fun waitRandomTime() {
        val randomDelay = Random.nextLong(3000, 9001) // Random delay between 3000ms and 9000ms (inclusive)
        delay(randomDelay)
    }

    fun generateRandomMessage(workers: List<Worker>): ChatMessage {
        val words = listOf(
            "Hi, folks",
            "Iam going to the warehouse",
            "I'm going to smoke.",
            "Lets go to the lunch"
        )

        val randomWord = words.random()
        val randomAuthor = workers.random()
    }
}