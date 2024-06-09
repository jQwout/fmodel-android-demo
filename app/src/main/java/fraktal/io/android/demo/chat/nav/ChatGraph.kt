package fraktal.io.android.demo.chat.nav

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import fraktal.io.android.demo.chat.di.ChatLocator
import fraktal.io.android.demo.chat.ui.ChatPageScreen
import fraktal.io.android.demo.chat.ui.ChatPageScreenNav
import fraktal.io.android.demo.workers.nav.WorkersGraph
import fraktal.io.ext.NavigationResult
import kotlinx.serialization.Serializable


@Serializable
object ChatGraph : NavigationResult

fun NavGraphBuilder.chatGraph(navController: NavController) {
    navigation<ChatGraph>(
        startDestination = ChatPageScreenNav(true),
        builder = {
            composable<ChatPageScreenNav> {
                ChatPageScreen(viewModels = viewModel(factory = ChatLocator.chatPageVmFactory))
            }
        }
    )
}