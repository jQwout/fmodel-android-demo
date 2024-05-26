package fraktal.io.ext

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Simple EventBus.
 * EventBus is not an EventStore. It does not store/save events to the storage/DB.
 *
 * https://elizarov.medium.com/shared-flows-broadcast-channels-899b675e805c
 */
class EventBus<E> {
    private val _events = MutableSharedFlow<E>()
    val events = _events.asSharedFlow() // read-only public view

    suspend fun postEvent(event: E) {
        _events.emit(event) // suspends until subscribers receive it
    }
}