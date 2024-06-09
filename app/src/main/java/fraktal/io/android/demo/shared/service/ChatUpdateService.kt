package fraktal.io.android.demo.shared.service

import fraktal.io.android.demo.shared.models.chat.ChatMessage
import fraktal.io.android.demo.shared.models.chat.ChatMessagesPart
import fraktal.io.android.demo.shared.models.chat.toChatMessagesPart
import fraktal.io.android.demo.shared.models.worker.Worker
import fraktal.io.android.demo.shared.repository.AdminRepository
import fraktal.io.android.demo.shared.repository.MessagesRepository
import fraktal.io.android.demo.shared.repository.WorkerRepository
import fraktal.io.android.demo.shared.utils.visibleName
import fraktal.io.ext.EventBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalDate
import kotlin.random.Random

interface ChatUpdateService {

    suspend fun sendFromMe(text: String)

    suspend fun subscribe(): Flow<List<ChatMessage>>
}


class FakeChatService(
    private val workerRepository: WorkerRepository,
    private val messagesRepository: MessagesRepository,
    private val adminRepository: AdminRepository,
    private val coroutineScope: CoroutineScope,
) : ChatUpdateService {

    private val messagesUpdates = EventBus<ChatMessagesPart>()

    override suspend fun sendFromMe(text: String) {
        waitRandomTime()
        messagesRepository.save(
            text,
            Clock.System.now().toEpochMilliseconds(),
            true,
            adminRepository.adminId,
            adminRepository.adminName
        )
        responseOnMessage()
    }

    override suspend fun subscribe(): Flow<ChatMessagesPart> {
        return messagesUpdates.events
    }

    private fun responseOnMessage() {
        coroutineScope.launch {
            waitRandomTime(2)
            messagesUpdates.postEvent(
                generateRandomMessage(workerRepository.getAll()).toChatMessagesPart()
            )
        }
    }

    private suspend fun waitRandomTime(k: Int = 1) {
        val randomDelay = Random.nextLong(3000, 5000) // Random delay between 3000ms and 9000ms (inclusive)
        delay(randomDelay * k)
    }

    private suspend fun generateRandomMessage(workers: List<Worker>): ChatMessage {
        val phrases = listOf(
            "Hello everyone!",
            "Has anyone seen the boss?",
            "Let's take a break.",
            "I'm going to lunch.",
            "Does anyone have tape?",
            "Don't forget about the 3 PM meeting.",
            "Who wants coffee?",
            "I'll send the report later.",
            "Is it time to leave already?",
            "I need help with the project.",
            "Where can I find a pen?",
            "I'll get back to you later.",
            "Please check your email.",
            "See you at the meeting.",
            "Don't forget to update the task status."
        )

        // Random number of phrases in the message
        val randomPhrase = phrases.random()
        val randomWorker = workers.random()

        return messagesRepository.save(
            content = randomPhrase,
            timeMs = Clock.System.now().toEpochMilliseconds(),
            isMe = false,
            senderId = randomWorker.id,
            senderName = randomWorker.visibleName()
        )
    }
}