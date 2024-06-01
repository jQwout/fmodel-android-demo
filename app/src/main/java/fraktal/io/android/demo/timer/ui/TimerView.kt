package fraktal.io.android.demo.timer.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fraktal.io.android.demo.timer.domain.TimerCommand
import fraktal.io.android.demo.timer.domain.TimerEvent
import fraktal.io.android.demo.timer.domain.TimerState
import fraktal.io.ext.Aggregate
import fraktal.io.ext.MaterializedQuery
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch


@Composable
fun TimerView(
    viewModel: TimerViewModel
) {
    val state by viewModel.state.collectAsState()

    Render(
        timerText = state.timerText,
        isNewTimerCreated = state.isNewTimerCreated,
        buttons = state.buttons,
        onClick = viewModel::post,
    )
}

@Composable
private fun Render(
    timerText: String,
    isNewTimerCreated: Boolean,
    buttons: ImmutableList<TimerViewStateUI.ButtonState>,
    onClick: (TimerCommand) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        RenderText(timerText, isNewTimerCreated)

        Row(
            modifier = Modifier
                .padding(bottom = 32.dp)
                .align(Alignment.BottomCenter)
                .animateContentSize()
        ) {
            buttons.forEachIndexed { index, it ->
                RenderButton(text = it.text) {
                    onClick(it.command)
                }
                if (index != buttons.lastIndex) {
                    Spacer(modifier = Modifier.width(24.dp))
                }
            }
        }
    }
}

@Composable
private fun BoxScope.RenderText(timerText: String, needAnimateAlpha: Boolean) {
    // if build system without events - u must control your animations starts on
    // view - layer. its possible, but this is a very controversial topic between programmers.
    // Antonio Leiva has discussed this topic a lot.
    // However, I believe that this part refers to the in-team codestyle,
    // and a good api can make it possible to do this in different ways.
    // Let's leave it as it is for now.
    var animateAlphaState by rememberSaveable(needAnimateAlpha) {
        mutableStateOf(needAnimateAlpha)
    }
    val alphaAnimation = remember { Animatable(1f) }

    Text(
        text = timerText,
        fontSize = 36.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .graphicsLayer {
                this.alpha = alphaAnimation.value
            }
            .fillMaxWidth()
            .align(Alignment.Center)
    )

    if (animateAlphaState) {
        LaunchedEffect("animateAlpha") {
            alphaAnimation.animateTo(0.1f, animationSpec = tween(300))
            alphaAnimation.animateTo(1f, animationSpec = tween(300))
            animateAlphaState = false
        }
    }
}

@Composable
private fun RenderButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.width(120.dp)
    ) {
        Text(text = text)
    }
}

@Preview
@Composable
private fun PreviewButtons() {
    Scaffold {
        Box(modifier = Modifier.padding(it))
        Render(
            "02:54",
            false,
            persistentListOf(
                TimerViewStateUI.ButtonState("reset", TimerCommand.Reset),
                TimerViewStateUI.ButtonState("resume", TimerCommand.Resume),
            ),
            onClick = {}
        )
    }
}