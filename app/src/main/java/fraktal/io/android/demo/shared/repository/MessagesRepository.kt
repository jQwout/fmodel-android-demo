package fraktal.io.android.demo.shared.repository

import fraktal.io.android.demo.Messages
import fraktal.io.android.demo.MessagesQueries
import fraktal.io.android.demo.shared.models.chat.ChatMessage
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface MessagesRepository {

    suspend fun save(content: String, timeMs: Long, isMe: Boolean, senderId: Long, senderName: String): ChatMessage

    // suspend fun get(startOffset: Int, endOffset: Int): List<ChatMessage> firstly, let make it without paging

    suspend fun getAll(): List<ChatMessage>


    class Db(
        private val messagesQueries: MessagesQueries,
    ) : MessagesRepository {

        override suspend fun save(content: String, timeMs: Long, isMe: Boolean, senderId: Long, senderName: String): ChatMessage {
            return messagesQueries.transactionWithResult {
                messagesQueries.insert(senderId, senderName, timeMs, content, isMe)
                val lastId = messagesQueries.selectLast().executeAsOne()
                val raw = messagesQueries.selectById(lastId).executeAsOne()
                raw.asChatMessage()
            }
        }

        override suspend fun getAll(): List<ChatMessage> {
            val raw = messagesQueries.select().executeAsList()

            return raw.map {
                it.asChatMessage()
            }
        }

        private fun Messages.asChatMessage() = ChatMessage(
            this.sender_id,
            this.sender_name,
            this.id,
            Instant.fromEpochMilliseconds(this.time)
                .toLocalDateTime(TimeZone.currentSystemDefault()), // create special mapper for this
            this.content,
            this.isMe ?: false
        )
    }
}