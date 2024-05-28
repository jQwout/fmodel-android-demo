package fraktal.io.android.demo.workers.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fraktal.io.android.demo.R
import fraktal.io.android.demo.shared.models.Gender
import fraktal.io.android.demo.shared.models.Position
import fraktal.io.android.demo.theme.DateTransformation

fun ProfileScreen(

) {

}


@Composable
private fun Render(
    firstName: String?,
    lastName: String?,
    middleName: String?,
    email: String?,
    phoneNumber: String?,
    date: String?,
    position: ProfileData.Item<Position>? = null,
    gender: ProfileData.Item<Gender>? = null,

    firstNameError: Boolean = false,
    lastNameError: Boolean = false,
    middleNameError: Boolean = false,
    emailError: Boolean = false,
    phoneNumberError: Boolean = false,
    dateError: Boolean = false,

    isEditMode: Boolean = true,
    onButtonClick: (Boolean) -> Unit
) {
    val focusManager = LocalFocusManager.current

    var firstNameState by remember { mutableStateOf(firstName) }
    var lastNameState by remember { mutableStateOf(lastName) }
    var middleNameState by remember { mutableStateOf(middleName) }
    var emailState by remember { mutableStateOf(email) }
    var phoneNumberState by remember { mutableStateOf(phoneNumber) }
    var positionState by remember {
        mutableStateOf(position)
    }
    var genderState by remember {
        mutableStateOf(gender)
    }
    var dateState by remember {
        mutableStateOf(date)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .imePadding()
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
        ) {
            TextField(
                modifier = CommonTextFieldModifier(),
                value = firstNameState.orEmpty(),
                isError = firstNameError,
                onValueChange = { firstNameState = it },
                placeholder = { PlaceholderText("Enter first name") },
                enabled = isEditMode,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                trailingIcon = {
                    CommonClearIcon(firstNameState.isNullOrEmpty().not()) {
                        firstNameState = null
                    }
                }
            )
            TextField(
                modifier = CommonTextFieldModifier(),
                value = lastNameState.orEmpty(),
                isError = lastNameError,
                onValueChange = { lastNameState = it },
                placeholder = { PlaceholderText("Enter last name") },
                enabled = isEditMode,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                trailingIcon = {
                    CommonClearIcon(lastNameState.isNullOrEmpty().not()) {
                        lastNameState = null
                    }
                }
            )
            TextField(
                modifier = CommonTextFieldModifier(),
                value = middleNameState.orEmpty(),
                isError = middleNameError,
                onValueChange = { middleNameState = it },
                placeholder = { PlaceholderText("Enter middle name") },
                enabled = isEditMode,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                trailingIcon = {
                    CommonClearIcon(middleNameState.isNullOrEmpty().not()) {
                        middleNameState = null
                    }
                }
            )
            TextField(
                modifier = CommonTextFieldModifier(),
                value = emailState.orEmpty(),
                isError = emailError,
                onValueChange = { emailState = it },
                placeholder = { PlaceholderText("Enter email") },
                enabled = isEditMode,
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Email),
                trailingIcon = {
                    CommonClearIcon(emailState.isNullOrEmpty().not()) {
                        emailState = null
                    }
                }
            )
            DropDownMenu(item = genderState, variants = genders, placeHolder = "Gender") {
                genderState = it
            }
            DropDownMenu(item = positionState, variants = positions, placeHolder = "Position") {
                positionState = it
            }
            TextField(
                modifier = CommonTextFieldModifier(),
                value = phoneNumberState.orEmpty(),
                isError = phoneNumberError,
                onValueChange = { phoneNumberState = it },
                placeholder = { PlaceholderText("Enter phone number") },
                enabled = isEditMode,
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Phone),
                trailingIcon = {
                    CommonClearIcon(phoneNumberState.isNullOrEmpty().not()) {
                        phoneNumberState = null
                    }
                }
            )
            TextField(
                modifier = CommonTextFieldModifier(),
                value = dateState.orEmpty(),
                isError = dateError,
                onValueChange = { if (it.length <= DateTransformation.MAX_DATE_SIZE) dateState = it },
                placeholder = { PlaceholderText("Enter date of birth") },
                enabled = isEditMode,
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Decimal),
                trailingIcon = {
                    CommonClearIcon(dateState.isNullOrEmpty().not()) {
                        dateState = null
                    }
                },
                visualTransformation = DateTransformation,
                supportingText = {
                    Text("dd/mm/yyyy")
                }
            )
        }
        Button(
            onClick = { onButtonClick(isEditMode) },
            modifier = Modifier.align(alignment = Alignment.BottomCenter)
        ) {
            Text(text = if (isEditMode) "Save" else "Edit", modifier = Modifier.padding(horizontal = 32.dp))
        }
    }
}

@Composable
private fun PlaceholderText(text: String) {
    Text(text = text, fontSize = 14.sp, color = Color.Gray)
}

@Composable
private fun CommonTextFieldModifier() = Modifier
    .padding(bottom = 4.dp)
    .fillMaxWidth()

@Composable
private fun CommonClearIcon(
    isVisible: Boolean,
    onClick: () -> Unit
) = IconButton(onClick = onClick) {
    if (isVisible) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_clear_24),
            contentDescription = "clear",
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun <T> DropDownMenu(
    variants: List<ProfileData.Item<T>>,
    item: ProfileData.Item<T>?,
    placeHolder: String,
    onChange: (ProfileData.Item<T>) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var expanded by remember { mutableStateOf(false) }
    Box {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            variants.forEach {
                DropdownMenuItem(
                    text = { Text(it.dataString) },
                    onClick = {
                        onChange(it)
                        expanded = false
                    },
                )
            }
        }
        TextField(
            modifier = CommonTextFieldModifier()
                .clickable {
                    expanded = expanded.not()
                }
                .onFocusChanged {
                    if (it.isFocused) {
                        expanded = true
                    }
                },
            value = item?.dataString ?: "",
            onValueChange = {
                expanded = true
            },
            placeholder = { PlaceholderText(placeHolder) },
            enabled = true,
            colors = TextFieldDefaults.colors(disabledTextColor = Color.Black),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        )
    }
}


@Preview
@Composable
fun Preview() {
    Scaffold {
        Box(modifier = Modifier.padding(it)) {
            Render(
                firstName = "Ivan",
                lastName = "Ivanov",
                middleName = "Ivanovich",
                email = null,
                phoneNumber = null,
                date = null,
                onButtonClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun PreviewDD() {
    Scaffold {
        Box(modifier = Modifier.padding(it)) {
            DropDownMenu(
                variants = Position.entries.map { ProfileData.Item(it, it.toString()) },
                item = null,
                "Position",
                onChange = {}
            )
        }
    }
}

private val genders = Gender.entries.map { ProfileData.Item(it, it.toString()) }
private val positions = Position.entries.map { ProfileData.Item(it, it.toString()) }
