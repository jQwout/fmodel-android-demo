package fraktal.io.android.demo.chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import fraktal.io.android.demo.chat.domain.ChatPageCSE
import fraktal.io.android.demo.shared.models.chat.ChatMessage
import fraktal.io.android.demo.shared.ui.RenderLoader
import fraktal.io.ext.NavigationResult
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ChatPageScreenNav(val needLoad: Boolean) : NavigationResult

@Composable
fun ChatPageScreen(viewModels: ChatPagesViewModels) {
    val state by viewModels.state.collectAsState()

    RenderLoader(state.initLoading)
    RenderContent(state.messages, state.messagesErrorStr, viewModels::post)
}


@Composable
private fun RenderContent(
    messages: ImmutableList<ChatMessage>,
    errorMessage: String? = null,
    onCommand: (ChatPageCSE.Command) -> Unit,
) {
    var size by remember { mutableStateOf(Size.Zero) }
    Box {
        RenderInput(
            modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                size = layoutCoordinates.size.toSize()
            }
        ) {
            onCommand(it)
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
                .padding(bottom = size.height.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item(
                key = errorMessage,
                contentType = { "RenderChatError" }
            ) {
                RenderChatError(text = errorMessage)
            }
            items(
                count = messages.count(),
                key = { messages[it].messageId },
                contentType = { "RenderChatMessage" }
            ) {
                RenderChatMessage(
                    message = messages[it],
                    isLastMessage = messages.getOrNull(it + 1)?.senderId == messages[it].senderId
                )
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BoxScope.RenderInput(modifier: Modifier, onSend: (ChatPageCSE.Command.Send) -> Unit) {
    var input by rememberSaveable { mutableStateOf("") }
    val interactionSource = remember { MutableInteractionSource() }
    BasicTextField(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(
                start = 8.dp,
                end = 8.dp,
                bottom = 4.dp
            )
            .then(modifier),
        value = input,
        onValueChange = { newText -> input = newText },
        interactionSource = interactionSource,
    ) { innerTextField ->
        OutlinedTextFieldDefaults.DecorationBox(
            value = input,
            innerTextField = innerTextField,
            enabled = true,
            singleLine = true,
            interactionSource = interactionSource,
            visualTransformation = VisualTransformation.None,
            placeholder = {
                Text(text = "Write your message", color = MaterialTheme.colorScheme.secondary)
            },
            colors = TextFieldDefaults.colors(),
            container = {
                OutlinedTextFieldDefaults.ContainerBox(
                    enabled = true,
                    isError = false,
                    interactionSource = interactionSource,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(48f),
                    focusedBorderThickness = 2.dp,
                    unfocusedBorderThickness = 2.dp
                )
            },
            trailingIcon = {
                IconButton(
                    enabled = input.isNotBlank(),
                    onClick = {
                        onSend(ChatPageCSE.Command.Send(input))
                    }
                ) {
                    Icon(imageVector = Icons.Rounded.Send, contentDescription = "send to chat", tint = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }
}

@Composable
fun LazyItemScope.RenderChatError(text: String?) {
    text ?: return
    Column(
        modifier = Modifier
            .fillMaxSize()
            .animateItem(), verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                modifier = Modifier.padding(16.dp, 8.dp), text = text, fontSize = 24.sp, fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun LazyItemScope.RenderChatMessage(message: ChatMessage, isLastMessage: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .animateItem()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.85f)
                .align(
                    if (message.isMe) {
                        Alignment.CenterEnd
                    } else {
                        Alignment.CenterStart
                    }
                )
                .clip(
                    if (message.isMe) {
                        RoundedCornerShape(
                            topStart = 48f,
                            topEnd = 48f,
                            bottomStart = 48f,
                            bottomEnd = if (isLastMessage) 48f else 0f
                        )
                    } else {
                        RoundedCornerShape(
                            topStart = 48f,
                            topEnd = 48f,
                            bottomStart = if (isLastMessage) 48f else 0f,
                            bottomEnd = if (isLastMessage) 0f else 48f
                        )
                    }
                )
                .background(
                    if (message.isMe) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.inversePrimary
                )
                .padding(16.dp)
        ) {
            Text(text = message.content)
        }
    }
}

@Composable
@Preview
fun ChatMessagePreviewMy() {
    Scaffold {
        it
        RenderContent(
            messages = listOf(
                ChatMessage(
                    senderId = 1,
                    senderName = "Alice",
                    messageId = 103,
                    time = LocalDateTime(2023, 6, 3, 0, 0, 0),
                    content = "Doing well, just working on some projects.",
                    isMe = false
                ), ChatMessage(
                    senderId = 1,
                    senderName = "Alice",
                    messageId = 101,
                    time = LocalDateTime(2023, 6, 1, 0, 0, 0),
                    content = "Hi, how are you?",
                    isMe = false
                )
            ).toPersistentList()
        ) {}
    }
}

@Composable
@Preview
private fun ChatPagePreview() {
    Scaffold {
        it
        RenderContent(
            messages = listOf(
                ChatMessage(
                    senderId = 1,
                    senderName = "Alice",
                    messageId = 101,
                    time = LocalDateTime(2023, 6, 1, 0, 0, 0),
                    content = "Hi, how are you?",
                    isMe = false
                ), ChatMessage(
                    senderId = 2,
                    senderName = "Bob",
                    messageId = 102,
                    time = LocalDateTime(2023, 6, 2, 0, 0, 0),
                    content = "I'm good, thanks! And you?",
                    isMe = true
                ), ChatMessage(
                    senderId = 1,
                    senderName = "Alice",
                    messageId = 103,
                    time = LocalDateTime(2023, 6, 3, 0, 0, 0),
                    content = "Doing well, just working on some projects.",
                    isMe = false
                ), ChatMessage(
                    senderId = 3,
                    senderName = "Charlie",
                    messageId = 104,
                    time = LocalDateTime(2023, 6, 4, 0, 0, 0),
                    content = "Hey everyone, what did I miss?",
                    isMe = false
                ), ChatMessage(
                    senderId = 2,
                    senderName = "Bob",
                    messageId = 105,
                    time = LocalDateTime(2023, 6, 5, 0, 0, 0),
                    content = "Not much, just catching up.",
                    isMe = true
                ), ChatMessage(
                    senderId = 1,
                    senderName = "Alice",
                    messageId = 106,
                    time = LocalDateTime(2023, 6, 8, 0, 0, 0),
                    content = "Same here. Any plans for the weekend?",
                    isMe = false
                ),
                ChatMessage(
                    senderId = 1,
                    senderName = "Alice",
                    messageId = 107,
                    time = LocalDateTime(2023, 6, 8, 0, 0, 0),
                    content = "Anyone?",
                    isMe = false
                ), ChatMessage(
                    senderId = 2,
                    senderName = "Bob",
                    messageId = 108,
                    time = LocalDateTime(2023, 6, 8, 0, 0, 0),
                    content = "I'm thinking about going hiking.",
                    isMe = true
                ), ChatMessage(
                    senderId = 3,
                    senderName = "Charlie",
                    messageId = 109,
                    time = LocalDateTime(2023, 6, 8, 0, 0, 0),
                    content = "That sounds fun! Can I join?",
                    isMe = false
                ), ChatMessage(
                    senderId = 2,
                    senderName = "Bob",
                    messageId = 110,
                    time = LocalDateTime(2023, 6, 8, 0, 0, 0),
                    content = "Of course, the more the merrier!",
                    isMe = true
                ),
                ChatMessage(
                    senderId = 2,
                    senderName = "Bob",
                    messageId = 111,
                    time = LocalDateTime(2023, 6, 8, 0, 0, 0),
                    content = "See you later",
                    isMe = true
                ), ChatMessage(
                    senderId = 1,
                    senderName = "Alice",
                    messageId = 112,
                    time = LocalDateTime(2023, 6, 8, 0, 0, 0),
                    content = "Count me in too!",
                    isMe = false
                )
            ).toPersistentList()
        ) {}
    }
}