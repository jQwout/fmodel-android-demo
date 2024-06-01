package fraktal.io.android.demo.shared.repository


import fraktal.io.android.demo.Workers
import fraktal.io.android.demo.WorkersQueries
import fraktal.io.android.demo.shared.models.worker.Worker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate

interface WorkerRepository {

    suspend fun getAll(): List<Worker>

    suspend fun get(id: Long): Worker?

    suspend fun put(workerState: Worker)

    suspend fun update(workerState: Worker)

    class InMemory(
        private val mutableState: MutableStateFlow<List<Worker>> = MutableStateFlow(emptyList())
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

        override suspend fun update(workerState: Worker) {
            //
        }
    }

    class Db(
        private val workersQueries: WorkersQueries
    ) : WorkerRepository {

        override suspend fun getAll(): List<Worker> {
            return workersQueries.getByAll().executeAsList().map {
                it.map()
            }
        }

        override suspend fun get(id: Long): Worker? {
            return workersQueries.getById(id).executeAsOneOrNull()?.map()
        }

        override suspend fun put(workerState: Worker) {
            workersQueries.insert(workerState.map())
        }

        override suspend fun update(workerState: Worker) {
            workersQueries.updateById(
                first_name = workerState.firstName,
                last_name = workerState.lastName,
                middle_name = workerState.middleName,
                email = workerState.email,
                number = workerState.phoneNumber,
                birth_date = workerState.date.toEpochDays().toLong(),
                gender = workerState.gender,
                position = workerState.position,
                id = workerState.id // Ensure that you pass the worker's id to identify which record to update
            )
        }

        private fun Workers.map(): Worker {
            val it = this
            return Worker(
                it.id,
                it.first_name,
                it.last_name,
                it.middle_name ?: "",
                it.email,
                it.number,
                it.position,
                it.gender,
                LocalDate.fromEpochDays(it.birth_date.toInt())
            )
        }

        private fun Worker.map(): Workers {
            val it = this
            return Workers(
                it.id,
                it.firstName,
                it.lastName,
                it.middleName,
                it.email,
                it.phoneNumber,
                it.date.toEpochDays().toLong(),
                it.gender,
                it.position,
            )
        }
    }
}