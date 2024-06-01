package fraktal.io.android.demo.workers.list

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import fraktal.io.android.demo.nav.NavLocator
import fraktal.io.android.demo.shared.db.DbLocator
import fraktal.io.android.demo.shared.models.worker.Worker
import fraktal.io.android.demo.shared.repository.WorkerRepository
import fraktal.io.android.demo.workers.list.domain.WorkerList
import fraktal.io.android.demo.workers.list.domain.WorkerListCommand
import fraktal.io.android.demo.workers.list.domain.WorkerListEvent
import fraktal.io.android.demo.workers.list.domain.WorkerListQueryState
import fraktal.io.android.demo.workers.list.domain.workerListDecider
import fraktal.io.android.demo.workers.list.domain.workerListQuery
import fraktal.io.android.demo.workers.profile.asWorkerDataUI
import fraktal.io.android.demo.workers.profile.domain.WorkerCommand
import fraktal.io.android.demo.workers.profile.domain.WorkerEvent
import fraktal.io.android.demo.workers.profile.domain.WorkerQueryState
import fraktal.io.android.demo.workers.profile.domain.workerDecider
import fraktal.io.android.demo.workers.profile.domain.workerQuery
import fraktal.io.ext.Aggregate
import fraktal.io.ext.EventBus
import fraktal.io.ext.MaterializedQuery
import fraktal.io.ext.fViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

object WorkersListServiceLocator {

    private val workerRepository: WorkerRepository
            by lazy {
                WorkerRepository.Db(
                    DbLocator.database.workersQueries
                )
            }

    private val eventBus: EventBus<WorkerListEvent> = EventBus()
    private val decider = workerListDecider(workerRepository)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val aggregate: Aggregate<WorkerListCommand, WorkerList, WorkerListEvent> =
        Aggregate(decider, eventBus, NavLocator.navManager, coroutineScope)
    private val materializedView: MaterializedQuery<WorkersListUI, WorkerListEvent> = MaterializedQuery(
        workerListQuery().dimapOnState(
            fl = WorkersListUI::asWorkerListQueryState,
            fr = WorkerListQueryState::asWorkersListUI
        ),
        eventBus,
        coroutineScope
    )

    val workerList: ViewModelProvider.Factory = viewModelFactory {
        initializer<WorkersListViewModel> {
            fViewModel(materializedView, aggregate)
        }
    }
}