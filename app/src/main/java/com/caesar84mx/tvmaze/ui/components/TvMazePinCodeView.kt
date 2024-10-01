package com.caesar84mx.tvmaze.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.caesar84mx.tvmaze.util.Constants.PIN_MAX_LENGTH


@Composable
fun TvMazePinCodeView(
    input: String,
    shake: Boolean
) {
    Row(
        modifier = Modifier.shake(shake)
    ) {
        repeat(PIN_MAX_LENGTH) { index ->
            val isFilled = index < input.length
            PinDot(isFilled)
        }
    }
}

@Composable
private fun PinDot(isFilled: Boolean) {
    val color = MaterialTheme.colorScheme.onBackground

    Box(
        modifier = Modifier.size(60.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(15.dp)) {
            val radius = size.minDimension / 2
            val stroke = 2.dp.toPx()

            drawCircle(
                color = color,
                radius = radius,
                style = if(isFilled) {
                    Fill
                } else {
                    Stroke(stroke)
                },
            )
        }
    }
}

private fun Modifier.shake(shouldShake: Boolean): Modifier = composed {
    val density = LocalDensity.current.density
    val animatable = remember { Animatable(Offset(0f, 0f), Offset.VectorConverter) }

    LaunchedEffect(shouldShake) {
        if (shouldShake) {
            animatable.animateTo(
                targetValue = Offset(0f, 0f),
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 500
                        Offset(-10f * density, 0f) at 100 using FastOutSlowInEasing
                        Offset(10f * density, 0f) at 200 using FastOutSlowInEasing
                        Offset(-10f * density, 0f) at 300 using FastOutSlowInEasing
                        Offset(10f * density, 0f) at 400 using FastOutSlowInEasing
                        Offset(0f, 0f) at 500 using FastOutSlowInEasing
                    },
                    repeatMode = RepeatMode.Restart,
                ),
            )
        }
    }

    then(
        Modifier.graphicsLayer {
            translationX = animatable.value.x
        },
    )
}
