package com.caesar84mx.tvmaze.ui.screens.pin_code_screen

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.caesar84mx.tvmaze.R
import com.caesar84mx.tvmaze.ui.components.StatefulView
import com.caesar84mx.tvmaze.ui.components.TvMazePinCodeView
import com.caesar84mx.tvmaze.ui.components.TvMazePinKeyboard
import com.caesar84mx.tvmaze.ui.components.TvMazeThemeGradientBackground
import com.caesar84mx.tvmaze.ui.theme.TvMazeTheme
import com.caesar84mx.tvmaze.util.navigation.TvMazeNavigator
import com.caesar84mx.tvmaze.viewmodels.PinCodeEvent
import com.caesar84mx.tvmaze.viewmodels.PinCodeMode
import com.caesar84mx.tvmaze.viewmodels.PinCodeViewModel
import com.caesar84mx.tvmaze.viewmodels.PinCodeViewModelImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun PinCodeScreen(viewModel: PinCodeViewModel = koinViewModel()) {
    val event by viewModel.pinCodeEvent.collectAsState(initial = PinCodeEvent.Idle)
    val pinCodeMode by viewModel.pinCodeMode.collectAsState()
    val pinCodeInput by viewModel.pinCodeInput.collectAsState()
    val keyboardLayout by viewModel.keyboardLayout.collectAsState()

    val scope = rememberCoroutineScope()

    var invalidInput by remember { mutableStateOf(event == PinCodeEvent.InvalidPinCode) }
    var shake by remember { mutableStateOf(event == PinCodeEvent.InvalidPinCode) }

    val orientation = LocalConfiguration.current.orientation

    LaunchedEffect(event) {
        scope.launch {
            if (shake) {
                shake = false
            }
            when (event) {
                PinCodeEvent.InvalidPinCode -> {
                    invalidInput = true
                    shake = true
                    delay(500)
                    shake = false
                }

                else -> {}
            }
        }
    }

    StatefulView(
        viewModel = viewModel,
        background = { TvMazeThemeGradientBackground() }
    ) {
        when(orientation) {
            Configuration.ORIENTATION_PORTRAIT -> VerticalView(
                pinCodeMode = pinCodeMode,
                pinCodeInput = pinCodeInput,
                keyboardLayout = keyboardLayout,
                shake = shake,
                viewModel = viewModel,
            )
            else -> HorizontalView(
                pinCodeMode = pinCodeMode,
                pinCodeInput = pinCodeInput,
                keyboardLayout = keyboardLayout,
                shake = shake,
                viewModel = viewModel,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewLight() {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("Mock", Context.MODE_PRIVATE)

    TvMazeTheme {
        PinCodeScreen(
            viewModel = PinCodeViewModelImpl(
                navigator = TvMazeNavigator(),
                sharedPreferences = prefs
            )
        )
    }
}

@Preview
@Composable
private fun PreviewDark() {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("Mock", Context.MODE_PRIVATE)

    TvMazeTheme(darkTheme = true) {
        PinCodeScreen(
            viewModel = PinCodeViewModelImpl(
                navigator = TvMazeNavigator(),
                sharedPreferences = prefs
            )
        )
    }
}
