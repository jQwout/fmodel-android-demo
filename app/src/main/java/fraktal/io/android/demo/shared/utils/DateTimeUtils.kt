package fraktal.io.android.demo.shared.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime


private val formatterDdMmYyyy = LocalDate.Format {
    dayOfMonth()
    monthNumber();
     year()
}

fun parseLocalDate(dateString: String?): LocalDate? {
    dateString ?: return null
    return try {
        formatterDdMmYyyy.parse(dateString)
    } catch (e: Throwable) {
        e.printStackTrace()
        null
    }
}

fun LocalDate.toDdMmYyyy(): String {
    return format(formatterDdMmYyyy)
}

fun LocalDateNow() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date