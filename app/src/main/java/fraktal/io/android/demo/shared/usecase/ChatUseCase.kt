package fraktal.io.android.demo.shared.usecase

import fraktal.io.android.demo.shared.models.chat.ChatMessage
import fraktal.io.android.demo.shared.models.worker.Worker
import fraktal.io.android.demo.shared.repository.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalDate as JavaLocalDate

class ChatUseCase(
    private val chatRepository: ChatRepository,
    private val coroutineScope: CoroutineScope
) {

    private val fakeChatGenerator: FakeChatGenerator = FakeChatGenerator()

    fun subscribe() {

    }

    private class FakeChatGenerator {

        fun generateRandomMessage(workers: List<Worker>): ChatMessage {
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

            // Random number of phrases in the message (from 1 to 3)
            val randomPhrase = phrases.random()
            val randomWorker = workers.random()

            return ChatMessage(
                senderId = randomWorker.id,
                senderName = randomWorker.firstName + " " + randomWorker.lastName,
                time = JavaLocalDate.now().toKotlinLocalDate(),
                content = randomPhrase
            )
        }
    }
}




