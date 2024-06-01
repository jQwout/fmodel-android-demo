package fraktal.io.android.demo.nav

import androidx.navigation.NavHostController
import com.fraktalio.fmodel.domain.saga
import fraktal.io.android.demo.workers.list.domain.WorkerListEvent
import fraktal.io.android.demo.workers.profile.CreateWorkerNav
import fraktal.io.android.demo.workers.profile.EditWorkerNav
import fraktal.io.ext.NavigationAR
import fraktal.io.ext.NavigationResult
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf


val navigationSaga = saga<NavigationAR, NavigationResult> {

    when (it) {
        is WorkerListEvent.EditNav -> flowOf(EditWorkerNav(it.workerId))
        is WorkerListEvent.CreateNav -> flowOf(CreateWorkerNav)
        else -> emptyFlow()
    }
}

fun NavHostController.handle(result: NavigationResult?) = when (result) {
    null -> Unit

    is EditWorkerNav -> this.navigate(result)
    is CreateWorkerNav -> this.navigate(result)
    else -> throw IllegalAccessException("cannot handle $result")
}

