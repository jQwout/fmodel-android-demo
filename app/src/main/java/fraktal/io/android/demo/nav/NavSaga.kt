package fraktal.io.android.demo.nav


import androidx.navigation.NavHostController
import com.fraktalio.fmodel.domain.saga
import fraktal.io.android.demo.workers.list.WorkersListScreenNav
import fraktal.io.android.demo.workers.list.domain.WorkerListEvent
import fraktal.io.android.demo.workers.profile.CreateWorkerNav
import fraktal.io.android.demo.workers.profile.EditWorkerNav
import fraktal.io.android.demo.workers.profile.domain.WorkerEvent
import fraktal.io.ext.NavigationAR
import fraktal.io.ext.NavigationResult
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf


val navigationSaga = saga<NavigationAR, NavigationResult> {

    when (it) {
        is WorkerListEvent.EditNav -> flowOf(EditWorkerNav(it.workerId))
        is WorkerListEvent.CreateNav -> flowOf(CreateWorkerNav)

        is WorkerEvent.OnBackPressed -> flowOf(WorkersListScreenNav(it.hasChanged))
        else -> emptyFlow()
    }
}

fun NavHostController.handle(result: NavigationResult?): Unit = when (result) {
    null -> Unit

    is EditWorkerNav -> navigate(result)
    is CreateWorkerNav -> navigate(result)
    is WorkersListScreenNav -> {
        navigate(result) {
            popUpTo(0) {
                inclusive = true
            }
        }
    }

    else -> throw IllegalAccessException("cannot handle $result")
}

