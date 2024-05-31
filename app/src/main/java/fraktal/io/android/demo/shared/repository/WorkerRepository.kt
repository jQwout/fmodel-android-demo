package fraktal.io.android.demo.shared.repository


import fraktal.io.android.demo.shared.models.worker.Worker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

interface WorkerRepository {

    suspend fun getAll(): List<Worker>

    suspend fun get(id: Long): Worker?

    suspend fun put(
       workerState: Worker
    )

    class InMemory(
        private val mutableState : MutableStateFlow<List<Worker>> = MutableStateFlow(emptyList())
    ) : WorkerRepository {

        override suspend fun put(workerState: Worker) {
            mutableState.update { list ->
                list + workerState
            }
        }

        override suspend fun getAll(): List<Worker> {
            return mutableState.value
        }

        override suspend fun get(id: Long): Worker? {
            return mutableState.value.firstOrNull {
                it.id == id
            }
        }
    }
}