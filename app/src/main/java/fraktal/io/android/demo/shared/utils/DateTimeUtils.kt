package fraktal.io.android.demo.shared.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.char


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