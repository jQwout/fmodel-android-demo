package fraktal.io.android.demo.workers.nav

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import fraktal.io.android.demo.workers.list.ui.WorkersListScreen
import fraktal.io.android.demo.workers.list.ui.WorkersListScreenNav
import fraktal.io.android.demo.workers.list.WorkersListServiceLocator
import fraktal.io.android.demo.workers.profile.ui.CreateWorkerNav
import fraktal.io.android.demo.workers.profile.ui.EditWorkerNav
import fraktal.io.android.demo.workers.profile.ui.WorkerScreen
import fraktal.io.android.demo.workers.profile.WorkerServiceLocator
import fraktal.io.ext.NavigationResult
import kotlinx.serialization.Serializable

@Serializable
object WorkersGraph : NavigationResult

fun NavGraphBuilder.workerGraph(navController: NavController) {
    navigation<WorkersGraph>(
        startDestination = WorkersListScreenNav(true),
        builder = {
            composable<EditWorkerNav> {
                WorkerScreen(
                    workerViewModel = viewModel(factory = WorkerServiceLocator.workerProfile),
                    workerId = it.toRoute<EditWorkerNav>().workerId
                )
            }
            composable<CreateWorkerNav> {
                WorkerScreen(
                    workerViewModel = viewModel(factory = WorkerServiceLocator.workerProfile),
                    workerId = null
                )
            }
            composable<WorkersListScreenNav> {
                val needLoad = it.toRoute<WorkersListScreenNav>().needLoad
                WorkersListScreen(
                    viewModel = viewModel(factory = WorkersListServiceLocator.workerList),
                    needLoad = needLoad,
                    onCreateNew = { navController.navigate(CreateWorkerNav) }
                )
            }
        }
    )
}