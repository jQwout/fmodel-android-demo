package fraktal.io.android.demo.workers.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import fraktal.io.android.demo.R
import fraktal.io.android.demo.shared.models.Gender
import fraktal.io.android.demo.shared.models.Position
import fraktal.io.android.demo.shared.models.worker.Worker
import fraktal.io.android.demo.workers.list.domain.WorkerListCommand
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
object WorkersListScreenNav

@Composable
fun WorkersListScreen(viewModel: WorkersListViewModel) {

    val state by viewModel.state.collectAsState()

    RenderLoader(state.isLoading)
    Render(workers = state.items, errorText = state.hasError) {
        viewModel.post(it)
    }
    LaunchedEffect(Unit) {
        viewModel.post(WorkerListCommand.Load)
    }
}

@Composable
fun RenderLoader(isLoading: Boolean) {
    var showDialog by remember(isLoading) {
        mutableStateOf(isLoading)
    }

    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false },
            DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(8.dp))
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp).size(40.dp)
                )
            }
        }
    }
}

@Composable
fun Render(
    workers: ImmutableList<WorkersListUI.WorkerItemUI>,
    errorText: String?,
    onClick: (WorkerListCommand) -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {

            if (workers.isEmpty()) {
                EmptyContent(errorText)
            } else {
                ListContent(workers, onClick)
            }

            FloatingActionButton(
                onClick = { onClick(WorkerListCommand.OnCreate) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
            ) {
                Icon(Icons.Filled.Add, "Add worker")
            }
        }
    }
}

@Composable
fun BoxScope.EmptyContent(errorText: String?) {
    Text(
        text = errorText ?: "No workers found",
        modifier = Modifier.align(Alignment.Center),
        fontSize = 24.sp,
        color = Color.Gray
    )
}

@Composable
fun ListContent(workers: ImmutableList<WorkersListUI.WorkerItemUI>, onClick: (WorkerListCommand) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp)
    ) {
        items(workers, key = { it.id }) {
            WorkerItem(name = it.name, role = it.role) { onClick(WorkerListCommand.OnEdit(it.id)) }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun WorkerItem(name: String, role: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = name)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = role, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(16.dp))
            Icon(painter = painterResource(id = R.drawable.id_dots_edit), contentDescription = "edit")
        }
    }
}

@Preview
@Composable
fun WorkerListPreview() {
    Scaffold {
        Box(modifier = Modifier.padding(it)) {
            Render(
                workers = listOf(
                    WorkersListUI.WorkerItemUI(
                        Worker(
                            1,
                            "Estaban",
                            "Collman",
                            "",
                            "",
                            "",
                            Position.MANAGER,
                            Gender.MALE,
                            LocalDate(4, 4, 1990)
                        )
                    ),
                    WorkersListUI.WorkerItemUI(
                        Worker(
                            1,
                            "David",
                            "Backham",
                            "",
                            "",
                            "",
                            Position.MOVER,
                            Gender.MALE,
                            LocalDate(4, 4, 1990)
                        )
                    ),
                    WorkersListUI.WorkerItemUI(
                        Worker(
                            1,
                            "Jason",
                            "S.",
                            "",
                            "",
                            "",
                            Position.DRIVER,
                            Gender.MALE,
                            LocalDate(4, 4, 1990)
                        )
                    )
                ).toPersistentList(),
                errorText = null,
                onClick = {}
            )
        }
    }
}

@Preview
@Composable
fun WorkerEmptyListPreview() {
    Scaffold {
        Box(modifier = Modifier.padding(it)) {
            Render(
                workers = emptyList<WorkersListUI.WorkerItemUI>().toPersistentList(),
                errorText = null,
                onClick = {}
            )
        }
    }
}


@Preview
@Composable
fun WorkerItemPreview() {
    WorkerItem("Estaban Collman", "Manager") {}
}

@Preview
@Composable
fun WorkersListProgress() {
    Scaffold {
        Box(modifier = Modifier.padding(it)) {
            RenderLoader(true)
        }
    }
}
