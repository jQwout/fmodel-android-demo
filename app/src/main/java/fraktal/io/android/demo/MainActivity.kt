package fraktal.io.android.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val navManager = NavLocator.navManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val uiScope = rememberCoroutineScope()
            NavHost(navController, startDestination = WorkersListScreenNav(true)) {
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
                        onCreateNew =  { navController.navigate(CreateWorkerNav) }
                    )
                }
            }
            LaunchedEffect(Unit) {
                uiScope.launch {
                    navManager.navResult.collect {
                        navController.handle(it)
                    }
                }
            }
        }
    }
}

