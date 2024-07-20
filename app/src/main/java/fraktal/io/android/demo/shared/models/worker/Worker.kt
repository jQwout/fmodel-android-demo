package fraktal.io.android.demo.shared.models.worker

import fraktal.io.android.demo.shared.models.Gender
import fraktal.io.android.demo.shared.models.Position
import kotlinx.datetime.LocalDate

class Worker(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val middleName: String,
    val email: String,
    val phoneNumber: String,
    val position: Position,
    val gender: Gender,
    val date: LocalDate
)