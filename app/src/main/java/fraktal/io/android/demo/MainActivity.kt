package fraktal.io.android.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import fraktal.io.android.demo.nav.NavLocator
import fraktal.io.android.demo.nav.handle
import fraktal.io.android.demo.workers.list.WorkersListScreen
import fraktal.io.android.demo.workers.list.WorkersListScreenNav
import fraktal.io.android.demo.workers.list.WorkersListServiceLocator
import fraktal.io.android.demo.workers.profile.CreateWorkerNav
import fraktal.io.android.demo.workers.profile.EditWorkerNav
import fraktal.io.android.demo.workers.profile.WorkerScreen
import fraktal.io.android.demo.workers.profile.WorkerServiceLocator

class MainActivity : ComponentActivity() {

    private val navManager = NavLocator.navManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(navController, startDestination = WorkersListScreenNav) {
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
                composable<WorkersListScreenNav> { backStackEntry ->
                    WorkersListScreen(
                        viewModel = viewModel(factory = WorkersListServiceLocator.workerList),
                    )
                }
            }
            LaunchedEffect(Unit) {
                navManager.navResult.collect {
                    navController.handle(it)
                }
            }
        }
    }
}

