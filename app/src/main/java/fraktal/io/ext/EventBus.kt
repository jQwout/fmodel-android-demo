package fraktal.io.ext

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Simple EventBus.
 * EventBus is not an EventStore. It does not store/save events to the storage/DB.
 *
 * https://elizarov.medium.com/shared-flows-broadcast-channels-899b675e805c
 */
class EventBus<E>(capacity: Int = Channel.BUFFERED) {
    private val _events = Channel<E>(capacity = capacity)
    val events = _events.consumeAsFlow() // expose as flow

    suspend fun postEvent(event: E) {
        _events.send(event) // suspends on buffer overflow
    }
}