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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import fraktal.io.ext.Reducer
import kotlinx.coroutines.launch


@Composable
fun TimerView(
    reducer: Reducer<TimerCommand, TimerState, TimerViewStateUI, TimerEvent>
) {
    val uiScope = rememberCoroutineScope()
    val state by reducer.uiStates.collectAsState()
    val event by reducer.events.collectAsState(initial = null)

    Render(timerState = state, timerAnimation = event is TimerEvent.OnNewTimerCreated) {
        uiScope.launch {
            reducer.emit(it)
        }
    }
}

@Composable
private fun Render(
    timerState: TimerViewStateUI,
    timerAnimation: Boolean,
    onClick: (TimerCommand) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        RenderText(timerState.timerText, timerAnimation)

        Row(
            modifier = Modifier
                .padding(bottom = 32.dp)
                .align(Alignment.BottomCenter)
                .animateContentSize()
        ) {
            timerState.buttons.forEachIndexed { index, it ->
                RenderButton(text = it.text) {
                    onClick(it.command)
                }
                if (index != timerState.buttons.lastIndex) {
                    Spacer(modifier = Modifier.width(24.dp))
                }
            }
        }
    }
}

@Composable
private fun BoxScope.RenderText(timerText: String, animateAlpha: Boolean) {
    val alphaAnimation = remember { Animatable(1f) }

    if (animateAlpha) {
        LaunchedEffect("animateAlpha") {
            alphaAnimation.animateTo(0.1f, animationSpec = tween(300))
            alphaAnimation.animateTo(1f, animationSpec = tween(300))
        }
    }

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
            timerState = TimerViewStateUI(
                "02:54",
                100,
                listOf(
                    TimerViewStateUI.ButtonState("reset", TimerCommand.Reset),
                    TimerViewStateUI.ButtonState("resume", TimerCommand.Resume),
                )
            ),
            false,
            onClick = {}
        )
    }
}