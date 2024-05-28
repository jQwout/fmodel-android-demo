package fraktal.io.android.demo.shared.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val formatterDdMmYyyy = DateTimeFormatter.ofPattern("dd/MM/yyyy")

fun parseDate(dateString: String): LocalDate? {
    return try {
        LocalDate.parse(dateString, formatterDdMmYyyy)
    } catch (e: DateTimeParseException) {
        e.printStackTrace()
        null
    }
}

fun parseDateTime(dateString: String): LocalDateTime? {
    return try {
        val localDate = LocalDate.parse(dateString, formatterDdMmYyyy)
        localDate.atStartOfDay()
    } catch (e: DateTimeParseException) {
        e.printStackTrace()
        null
    }
}