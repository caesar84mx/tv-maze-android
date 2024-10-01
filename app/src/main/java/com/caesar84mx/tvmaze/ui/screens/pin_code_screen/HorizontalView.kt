package com.caesar84mx.tvmaze.ui.screens.pin_code_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.caesar84mx.tvmaze.R
import com.caesar84mx.tvmaze.ui.components.TvMazePinCodeView
import com.caesar84mx.tvmaze.ui.components.TvMazePinKeyboard
import com.caesar84mx.tvmaze.viewmodels.PadInput
import com.caesar84mx.tvmaze.viewmodels.PinCodeMode
import com.caesar84mx.tvmaze.viewmodels.PinCodeViewModel

@Composable
fun HorizontalView(
    pinCodeMode: PinCodeMode,
    pinCodeInput: String,
    keyboardLayout: Array<Array<PadInput>>,
    shake: Boolean,
    viewModel: PinCodeViewModel,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Spacer(modifier = Modifier.fillMaxHeight(0.1f))

        Text(
            text = when(pinCodeMode) {
                PinCodeMode.EnterPinCode -> stringResource(R.string.enter_pin_code_title)
                PinCodeMode.SetNewPinCode -> stringResource(R.string.set_new_pin_code_title)
            },
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
        )

        TvMazePinCodeView(
            input = pinCodeInput,
            shake = shake
        )

        Spacer(modifier = Modifier.fillMaxHeight(0.1f))

        TvMazePinKeyboard(
            layout = keyboardLayout,
            onButtonPressed = viewModel::onButtonPressed,
            modifier = Modifier.fillMaxWidth(0.3f)
        )
    }
}