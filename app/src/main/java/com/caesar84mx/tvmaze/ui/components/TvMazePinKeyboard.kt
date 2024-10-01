package com.caesar84mx.tvmaze.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.caesar84mx.tvmaze.R
import com.caesar84mx.tvmaze.ui.theme.TvMazeTheme
import com.caesar84mx.tvmaze.viewmodels.PadInput
import com.caesar84mx.tvmaze.viewmodels.PinCodeViewModelImpl.Companion.defaultLayout

@Composable
fun TvMazePinKeyboard(
    modifier: Modifier = Modifier,
    layout: Array<Array<PadInput>> = defaultLayout,
    onButtonPressed: (PadInput) -> Unit = {}
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        layout.forEach { row ->
            Row {
                row.forEach { button ->
                    OutlinedButton(
                        onClick = { onButtonPressed(button) },
                        enabled = button.isEnabled,
                        modifier = Modifier.weight(1f)
                    ) {
                        when(button) {
                            is PadInput.Symbol -> Text(text = button.symbol.toString())
                            else -> Icon(
                                painter = painterResource(
                                    id = when(button) {
                                        is PadInput.Clear -> R.drawable.ic_close
                                        is PadInput.Accept -> R.drawable.ic_enter
                                        else -> 0
                                    }
                                ),
                                contentDescription = "enter"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    TvMazeTheme {
        TvMazePinKeyboard()
    }
}