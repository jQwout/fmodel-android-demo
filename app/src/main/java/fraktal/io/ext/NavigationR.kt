package fraktal.io.ext

import com.fraktalio.fmodel.domain.Saga
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

interface NavigationAR
interface NavigationResult


class NavManager(
    private val saga: Saga<NavigationAR, NavigationResult>,
    private val eventBus: MutableSharedFlow<NavigationResult> = MutableSharedFlow(1),
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    val navResult = eventBus.asSharedFlow()

    fun post(actionResult: NavigationAR) {
        coroutineScope.launch {
            saga.react(actionResult).collect {
                eventBus.emit(it)
            }
        }
    }
}
