package fraktal.io.android.demo.workers.list

import fraktal.io.android.demo.shared.models.worker.Worker
import fraktal.io.android.demo.timer.domain.TimerCommand
import fraktal.io.android.demo.timer.domain.TimerEvent
import fraktal.io.android.demo.timer.domain.TimerState
import fraktal.io.android.demo.timer.ui.TimerViewStateUI
import fraktal.io.android.demo.workers.list.domain.WorkerListCommand
import fraktal.io.android.demo.workers.list.domain.WorkerListEvent
import fraktal.io.android.demo.workers.list.domain.WorkerListQueryState
import fraktal.io.ext.FViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

typealias WorkersListViewModel = FViewModel<WorkerListCommand, *, WorkersListUI, WorkerListEvent>

class WorkersListUI(
    val items: ImmutableList<WorkerItemUI>,
    val isLoading: Boolean,
    val hasError: String?
) {
    class WorkerItemUI(
        private val worker: Worker,
    ) {
        val id: Long = worker.id
        val name: String get() = worker.firstName + " " + worker.lastName
        val role: String get() = worker.position.toString()

        fun toWorker() = worker
    }

    fun asWorkerListQueryState() = WorkerListQueryState(
        workers = items.map { it.toWorker() },
        isLoading = isLoading,
        hasError = hasError
    )
}

fun WorkerListQueryState.asWorkersListUI() : WorkersListUI = WorkersListUI(
    isLoading = isLoading,
    hasError = hasError,
    items = workers.map { WorkersListUI.WorkerItemUI(it) }.toPersistentList(),
)
