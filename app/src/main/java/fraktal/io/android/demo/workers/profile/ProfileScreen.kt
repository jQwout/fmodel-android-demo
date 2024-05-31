package fraktal.io.android.demo.workers.profile


import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fraktal.io.android.demo.R
import fraktal.io.android.demo.shared.models.Gender
import fraktal.io.android.demo.shared.models.Position
import fraktal.io.android.demo.shared.utils.parseLocalDate
import fraktal.io.android.demo.theme.DateTransformation
import fraktal.io.android.demo.workers.profile.domain.WorkerCommand
import fraktal.io.android.demo.workers.profile.domain.WorkerEvent
import fraktal.io.ext.InputItem


@Composable
fun ProfileView(viewModel: WorkerViewModel) {
    val state by viewModel.state.collectAsState()
    val events by viewModel.events.collectAsState(initial = null)

    RenderToast(event = events)
    Render(state = state, onButtonClick = {
        it ?: return@Render
        viewModel.post(it)
    })

    LaunchedEffect(Unit) {
        viewModel.post(WorkerCommand.LoadById(1))
    }
}

@Composable
fun RenderToast(event: WorkerEvent?) {

    val text = when (event) {
        is WorkerEvent.OnSavedById -> "User data edited"
        is WorkerEvent.OnUserCreated -> "New user created"
        else -> return
    }

    val ctx = LocalContext.current

    LaunchedEffect(text) {
        Toast.makeText(ctx, text, Toast.LENGTH_LONG).show()
    }
}

@Composable
private fun Render(
    state: WorkerDataUI,
    onButtonClick: (WorkerCommand.TrySave?) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var mutableState by remember(state.hashCode()) { mutableStateOf(state) }

    Scaffold(
        modifier = Modifier
            .imePadding()
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .verticalScroll(rememberScrollState()),
            ) {
                InputTextFiled(
                    mutableState = mutableState.firstName,
                    placeHolder = "Enter first name",
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                    onStateChange = {
                        mutableState = mutableState.copy(firstName = it)
                    }
                )
                InputTextFiled(
                    mutableState = mutableState.lastName,
                    placeHolder = "Enter last name",
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                    onStateChange = {
                        mutableState = mutableState.copy(lastName = it)
                    }
                )
                InputTextFiled(
                    mutableState = mutableState.middleName,
                    placeHolder = "Enter middle name",
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                    onStateChange = {
                        mutableState = mutableState.copy(middleName = it)
                    }
                )
                InputTextFiled(
                    mutableState = mutableState.email,
                    placeHolder = "Enter email",
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Email),
                    onStateChange = {
                        mutableState = mutableState.copy(email = it)
                    }
                )
                DropDownMenu(item = mutableState.gender, variants = genders, placeHolder = "Gender") {
                    mutableState = mutableState.copy(gender = mutableState.gender.putNewData(it))
                }
                DropDownMenu(item = mutableState.position, variants = positions, placeHolder = "Position") {
                    mutableState = mutableState.copy(position = mutableState.position.putNewData(it))
                }
                InputTextFiled(
                    mutableState = mutableState.phoneNumber,
                    placeHolder = "Enter phone number",
                    maxSymbols = 15,
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Phone),
                    onStateChange = {
                        mutableState = mutableState.copy(phoneNumber = it)
                    }
                )
                InputTextFiled(
                    mutableState = mutableState.date,
                    placeHolder = "Enter date of birth",
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    maxSymbols = 8,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Number),
                    supportText = "dd/mm/yyyy",
                    visualTransformation = DateTransformation,
                    onStateChange = {
                        mutableState = mutableState.copy(date = it)
                    }
                )
            }
            Button(
                onClick = {
                    onButtonClick(
                        prepareCommandIfValid(mutableState) {
                            mutableState = it
                        }
                    )
                },
                modifier = Modifier.align(BottomCenter)
            ) {
                Text(text = if (mutableState.isNew()) "Save" else "Edit", modifier = Modifier.padding(horizontal = 32.dp))
            }
        }
    }
}

@Composable
private fun <T> InputTextFiled(
    mutableState: InputItem<T>,

    placeHolder: String,
    keyboardActions: KeyboardActions,
    keyboardOptions: KeyboardOptions,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isEditMode: Boolean = true,
    supportText: String? = null,
    maxSymbols: Int? = null,

    onStateChange: (InputItem<T>) -> Unit
) {
    val input = mutableState

    TextField(
        modifier = Modifier
            .fillMaxWidth(),
        value = input.dataString,
        isError = input.hasValidationError,
        onValueChange = {
            if (maxSymbols != null && it.length > maxSymbols) {
                return@TextField
            }
            val newData = input.putNewData(it)
            onStateChange(newData)
        },
        placeholder = {
            Text(text = placeHolder, fontSize = 14.sp, color = Color.Gray)
        },
        enabled = isEditMode,
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        trailingIcon = {
            if (input.data != null) {
                IconButton(onClick = {
                    val newData = input.putNewData(null)
                    onStateChange(newData)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_clear_24),
                        contentDescription = "clear",
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        },
        visualTransformation = visualTransformation,
        maxLines = 1,
        supportingText = {
            when {
                input.hasValidationError && input.errorText != null -> Text(input.errorText)
                supportText != null -> Text(supportText)
            }
        }
    )
}

@Composable
private fun PlaceholderText(text: String) {
    Text(text = text, fontSize = 14.sp, color = Color.Gray)
}

@Composable
private fun <T> DropDownMenu(
    variants: List<InputItem<T>>,
    item: InputItem<T>?,
    placeHolder: String,
    onChange: (T) -> Unit
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
                        onChange(it.requiredData())
                        expanded = false
                    },
                )
            }
        }
        TextField(
            modifier = Modifier
                .fillMaxWidth()
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
            trailingIcon = {
                if (item?.data != null) {
                    IconButton(onClick = {
                        expanded = expanded.not()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_create_24),
                            contentDescription = "change",
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            },
            supportingText = { },
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        )
    }
}

private fun prepareCommandIfValid(
    state: WorkerDataUI,
    onChange: (WorkerDataUI) -> Unit
): WorkerCommand.TrySave? {
    val newValidate = state.validate()
    onChange(newValidate)
    return if (newValidate.hasValidationErrors()) {
        null
    } else {
        // Create and return the WorkerCommand.TrySave command
        WorkerCommand.TrySave(
            firstName = newValidate.firstName.requiredData(),
            lastName = newValidate.lastName.requiredData(),
            middleName = newValidate.middleName.data.orEmpty(),
            email = newValidate.email.requiredData(),
            phoneNumber = newValidate.phoneNumber.requiredData(),
            date = parseLocalDate(newValidate.date.requiredData())!!,
            position = newValidate.position.requiredData(),
            gender = newValidate.gender.requiredData()
        )
    }
}


@Composable
@Preview
private fun Preview() {
    Scaffold {
        Box(modifier = Modifier.padding(it)) {
            Render(state = WorkerDataUI(null)) {

            }
        }
    }
}


private val genders = Gender.entries.map { InputItem(data = it) }
private val positions = Position.entries.map { InputItem(data = it) }
