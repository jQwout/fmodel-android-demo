package fraktal.io.ext

data class InputItem<T>(
    private var toString: (T?) -> String = { it?.toString().orEmpty() },
    val data: T?,
    val hasValidationError: Boolean = false,
    val errorText: String? = null,
    private var parseFromString: (String?) -> T? = { null },
    private var validator: (T?) -> Boolean = { true } // var for exclude in hashcode\equals
) {
    fun requiredData() = data!!

    val dataString: String = toString(data)

    fun putNewData(value: String): InputItem<T> {
        val newData = parseFromString(value)
        return copy(data = newData, hasValidationError = false)
    }

    fun putNewData(data: T?): InputItem<T> {
        return copy(data = data, hasValidationError = false)
    }

    fun validate(): InputItem<T> {
        return copy(
            hasValidationError = validator(data).not()
        )
    }
}