package fraktal.io.android.demo.workers.profile

import androidx.compose.runtime.Stable
import fraktal.io.android.demo.shared.models.Gender
import fraktal.io.android.demo.shared.models.Position
import fraktal.io.android.demo.shared.models.worker.Worker
import fraktal.io.android.demo.shared.utils.Validation
import fraktal.io.android.demo.shared.utils.parseLocalDate
import fraktal.io.android.demo.shared.utils.toDdMmYyyy
import fraktal.io.android.demo.workers.profile.domain.WorkerCommand
import fraktal.io.android.demo.workers.profile.domain.WorkerEvent
import fraktal.io.android.demo.workers.profile.domain.WorkerQueryState
import fraktal.io.ext.FViewModel
import fraktal.io.ext.InputItem

typealias WorkerViewModel = FViewModel<WorkerCommand, *, WorkerDataUI, WorkerEvent>

@Stable
data class WorkerDataUI(
    val id: Long?,
    val firstName: InputItem<String>,
    val lastName: InputItem<String>,
    val middleName: InputItem<String>,
    val email: InputItem<String>,
    val phoneNumber: InputItem<String>,
    val position: InputItem<Position>,
    val gender: InputItem<Gender>,
    val date: InputItem<String>
) {

    fun isNew() = id == null

    fun validate() = WorkerDataUI(
        id,
        firstName.validate(),
        lastName.validate(),
        middleName.validate(),
        email.validate(),
        phoneNumber.validate(),
        position.validate(),
        gender.validate(),
        date.validate()
    )

    fun hasValidationErrors(): Boolean {
        val hasValidationError = firstName.hasValidationError or
                lastName.hasValidationError or
                middleName.hasValidationError or
                email.hasValidationError or
                phoneNumber.hasValidationError or
                position.hasValidationError or
                gender.hasValidationError or
                date.hasValidationError


        return hasValidationError
    }
}

fun WorkerDataUI(
    id: Long?,
    firstName: String?,
    lastName: String?,
    middleName: String?,
    email: String?,
    phoneNumber: String?,
    position: Position?,
    gender: Gender?,
    date: String?,
) = WorkerDataUI(
    id = id,
    firstName = InputItem(
        data = firstName,
        parseFromString = { it.orEmpty() },
        validator = { it.isNullOrEmpty().not() }
    ),
    lastName = InputItem(
        data = lastName,
        parseFromString = { it.orEmpty() },
        validator = { it.isNullOrEmpty().not() }
    ),
    middleName = InputItem(
        data = middleName,
        parseFromString = { it.orEmpty() },
    ),
    email = InputItem(
        data = email,
        parseFromString = { it.orEmpty() },
        validator = { Validation.isValidEmail(it) }
    ),
    phoneNumber = InputItem(
        data = phoneNumber,
        parseFromString = { it.orEmpty() },
        validator = { Validation.isValidPhoneNumber(it) }
    ),
    position = InputItem(
        data = position,
        parseFromString = {
            it ?: return@InputItem Position.DRIVER
            Position.valueOf(it)
        }
    ),
    gender = InputItem(
        data = gender,
        parseFromString = {
            it ?: return@InputItem Gender.MALE
            Gender.valueOf(it)
        }
    ),
    date = InputItem<String>(
        data = date,
        parseFromString = {
            it.orEmpty()
        },
        validator = {
            Validation.isValidDate(parseLocalDate(it))
        },
    )
)

fun WorkerDataUI(
    worker: Worker?
): WorkerDataUI = WorkerDataUI(
    worker?.id,
    worker?.firstName,
    worker?.lastName,
    worker?.middleName,
    worker?.email,
    worker?.phoneNumber,
    worker?.position,
    worker?.gender,
    worker?.date?.toDdMmYyyy()
)

fun WorkerDataUI.asWorkerQueryState(): WorkerQueryState = WorkerQueryState(
    worker = if (hasValidationErrors()) {
        id?.let {
            Worker(
                it,
                firstName = firstName.requiredData(),
                lastName = lastName.requiredData(),
                middleName = middleName.requiredData(),
                email = email.requiredData(),
                phoneNumber = phoneNumber.requiredData(),
                date = parseLocalDate(date.requiredData())!!,
                position = position.requiredData(),
                gender = gender.requiredData(),
            )
        }
    } else {
        null
    },
    hasPhoneNumberError = false
)

fun WorkerQueryState?.asWorkerDataUI(): WorkerDataUI {
    return if (this != null) {
        val ui = WorkerDataUI(worker)
        ui.copy(phoneNumber = ui.phoneNumber.copy(hasValidationError = hasPhoneNumberError))
    } else {
        WorkerDataUI(null)
    }
}
