package fraktal.io.android.demo.chat.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import fraktal.io.android.demo.chat.domain.ChatPageCSE
import fraktal.io.android.demo.chat.domain.ChatQSE
import fraktal.io.android.demo.chat.domain.chatPageDecider
import fraktal.io.android.demo.chat.domain.chatQuery
import fraktal.io.android.demo.chat.ui.ChatPageUI
import fraktal.io.android.demo.chat.ui.asChatPageUI
import fraktal.io.android.demo.nav.NavLocator
import fraktal.io.android.demo.shared.SharedLocator
import fraktal.io.ext.Aggregate
import fraktal.io.ext.EventBus
import fraktal.io.ext.MaterializedQuery
import fraktal.io.ext.fViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

object ChatLocator {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val eventBus: EventBus<ChatPageCSE.Event> = EventBus()
    private val decider = chatPageDecider(
        SharedLocator.chatUpdateService,
        SharedLocator.messagesRepository
    )
    private val query = chatQuery()
    private val aggregate: Aggregate<ChatPageCSE.Command, ChatPageCSE.State, ChatPageCSE.Event> =
        Aggregate(decider, eventBus, NavLocator.navManager, scope = coroutineScope)
    private val materializedView: MaterializedQuery<ChatPageUI, ChatPageCSE.Event> = MaterializedQuery(
        query.dimapOnState(
            ChatPageUI::asChatQSEState,
            ChatQSE.State::asChatPageUI,
        ),
        eventBus,
        coroutineScope
    )

    val chatPageVmFactory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            fViewModel(materializedView, aggregate)
        }
    }
}