package fraktal.io.android.demo.shared

import fraktal.io.android.demo.shared.db.DbLocator
import fraktal.io.android.demo.shared.repository.AdminRepository
import fraktal.io.android.demo.shared.repository.MessagesRepository
import fraktal.io.android.demo.shared.repository.WorkerRepository
import fraktal.io.android.demo.shared.service.ChatUpdateService
import fraktal.io.android.demo.shared.service.FakeChatService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

object SharedLocator {

    val adminRepository: AdminRepository by lazy {
        AdminRepository.HardcodedAdminRepository()
    }

    val workerRepository: WorkerRepository by lazy {
        WorkerRepository.Db(DbLocator.database.workersQueries)
    }

    val messagesRepository: MessagesRepository by lazy {
        MessagesRepository.Db(DbLocator.database.messagesQueries)
    }

    val chatUpdateService: ChatUpdateService by lazy {
        FakeChatService(
            workerRepository, messagesRepository, adminRepository, CoroutineScope(Dispatchers.IO)
        )
    }
}