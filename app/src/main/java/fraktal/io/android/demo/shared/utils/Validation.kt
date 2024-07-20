package fraktal.io.android.demo.shared.utils

import android.telephony.PhoneNumberUtils
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

object Validation {

    fun isValidEmail(email: String?): Boolean {
        email ?: return false
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    fun isValidPhoneNumber(phone: String?): Boolean {
        phone ?: return false
        return PhoneNumberUtils.isGlobalPhoneNumber(phone)
    }

    fun isValidDate(localDate: LocalDate?): Boolean {
        localDate ?: return false
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val ageThreshold = now.minus(18, DateTimeUnit.YEAR)
        return localDate <= ageThreshold
    }

    fun isValidDateFromString(date: String) : Boolean {
        // Регулярное выражение для проверки формата "dd/MM/yyyy"
        val regex = """^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\d{4}$""".toRegex()
        return regex.matches(date)
    }
}