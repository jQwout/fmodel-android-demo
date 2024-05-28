package fraktal.io.android.demo.workers.profile

import androidx.compose.runtime.Stable
import fraktal.io.android.demo.shared.models.Gender
import fraktal.io.android.demo.shared.models.Position


@Stable
data class ProfileData(
    val firstName: Item<String>,
    val lastName: Item<String>,
    val middleName: Item<String>,
    val email: Item<String>,
    val phoneNumber: Item<String>,
    val position: Item<Gender>,
    val gender: Item<Position>,
) {
    @Stable
    data class Item<T>(
        val data: T?,
        val dataString: String = data.toString(),
        val isValidData: Boolean = true,
    ) {
        fun validate(validator: (T?) -> Boolean) = copy(isValidData = validator(data))
    }

    val isValid: Boolean
        get() {
            return firstName.isValidData &&
                    lastName.isValidData &&
                    middleName.isValidData &&
                    email.isValidData &&
                    phoneNumber.isValidData &&
                    position.isValidData &&
                    gender.isValidData
        }
}

