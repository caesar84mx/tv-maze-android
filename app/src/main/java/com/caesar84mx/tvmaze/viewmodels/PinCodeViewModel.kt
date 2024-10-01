package com.caesar84mx.tvmaze.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.viewModelScope
import com.caesar84mx.tvmaze.data.model.backbone.UiState
import com.caesar84mx.tvmaze.util.Constants.PIN_MAX_LENGTH
import com.caesar84mx.tvmaze.util.navigation.Home
import com.caesar84mx.tvmaze.util.navigation.Navigator
import com.caesar84mx.tvmaze.util.navigation.Quit
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

abstract class PinCodeViewModel(navigator: Navigator): TvMazeViewModel(navigator) {
    abstract val pinCodeMode: StateFlow<PinCodeMode>
    abstract val pinCodeEvent: SharedFlow<PinCodeEvent>
    abstract val pinCodeInput: StateFlow<String>
    abstract val keyboardLayout: StateFlow<Array<Array<PadInput>>>

    abstract fun onButtonPressed(input: PadInput)
    abstract fun onChangePinCodePressed()
}

internal class PinCodeViewModelImpl(
    navigator: Navigator,
    private val sharedPreferences: SharedPreferences,
): PinCodeViewModel(navigator) {
    private lateinit var pinCode: String
    
    private val _pinCodeMode = MutableStateFlow<PinCodeMode>(PinCodeMode.EnterPinCode)
    override val pinCodeMode: StateFlow<PinCodeMode> = _pinCodeMode.asStateFlow() 
    
    private val _pinCodeEvent = MutableSharedFlow<PinCodeEvent>()
    override val pinCodeEvent: SharedFlow<PinCodeEvent> = _pinCodeEvent.asSharedFlow()

    private val _pinCodeInput = MutableStateFlow("")
    override val pinCodeInput: StateFlow<String> = _pinCodeInput.asStateFlow()

    private val _keyboardLayout = MutableStateFlow<Array<Array<PadInput>>>(emptyArray())
    override val keyboardLayout: StateFlow<Array<Array<PadInput>>> = _keyboardLayout.asStateFlow()

    override fun <T> initialize(initData: T?) {
        viewModelScope.launch { 
            _keyboardLayout.emit(defaultLayout)
            
            pinCode = sharedPreferences.getString(PIN_CODE_KEY, "") ?: ""
            
            _pinCodeMode.emit(
                if (pinCode.isEmpty()) {
                    PinCodeMode.SetNewPinCode
                } else {
                    PinCodeMode.EnterPinCode
                }
            )

            _pinCodeInput.onEach { code ->
                setClearButtonEnabled(code.isNotEmpty())
                setAcceptButtonEnabled(code.length == PIN_MAX_LENGTH)
            }.launchIn(this)
        }
    }

    override fun onButtonPressed(input: PadInput) {
        viewModelScope.launch {
            when(input) {
                is PadInput.Symbol -> {
                    if (_pinCodeInput.value.length < PIN_MAX_LENGTH) {
                        _pinCodeInput.value += input.symbol
                    }

                    _pinCodeEvent.emit(PinCodeEvent.Idle)
                }
                
                is PadInput.Accept -> {
                    when(_pinCodeMode.value) {
                        is PinCodeMode.EnterPinCode -> {
                            if (_pinCodeInput.value == pinCode) {
                                updateState(UiState.Success("Unlocked!"))
                                delay(1000)
                                navigateTo(Home)
                            } else {
                                _pinCodeEvent.emit(PinCodeEvent.InvalidPinCode)
                            }
                        }
                        is PinCodeMode.SetNewPinCode -> {
                            pinCode = _pinCodeInput.value
                            sharedPreferences.edit().putString(PIN_CODE_KEY, pinCode).apply()
                            _pinCodeInput.value = ""
                            _pinCodeMode.emit(PinCodeMode.EnterPinCode)
                        }
                    }
                }

                is PadInput.Clear -> {
                    _pinCodeInput.value = ""
                    _keyboardLayout.emit(defaultLayout)
                    _pinCodeEvent.emit(PinCodeEvent.Idle)
                }
            }
        }
    }

    override fun onChangePinCodePressed() {
        viewModelScope.launch {
            _pinCodeMode.emit(PinCodeMode.SetNewPinCode)
            sharedPreferences.edit().remove(PIN_CODE_KEY).apply()
        }
    }

    override fun onBackPressed() {
        navigateTo(Quit)
    }

    private suspend fun setAcceptButtonEnabled(isEnabled: Boolean) {
        if (_keyboardLayout.value[3][2].isEnabled != isEnabled) {
            val newLayout = _keyboardLayout.value.clone()
            newLayout[3][2] = PadInput.Accept(isEnabled)
            _keyboardLayout.emit(newLayout)
        }
    }

    private suspend fun setClearButtonEnabled(isEnabled: Boolean) {
        if (_keyboardLayout.value[3][0].isEnabled != isEnabled) {
            val newLayout = _keyboardLayout.value.clone()
            newLayout[3][0] = PadInput.Clear(isEnabled)
            _keyboardLayout.emit(newLayout)
        }
    }

    companion object {
        const val PIN_CODE_KEY = "pin_code"

        val defaultLayout: Array<Array<PadInput>> = arrayOf(
            arrayOf(
                PadInput.Symbol('1'),
                PadInput.Symbol('2'),
                PadInput.Symbol('3'),
            ),
            arrayOf(
                PadInput.Symbol('4'),
                PadInput.Symbol('5'),
                PadInput.Symbol('6'),
            ),
            arrayOf(
                PadInput.Symbol('7'),
                PadInput.Symbol('8'),
                PadInput.Symbol('9'),
            ),
            arrayOf(
                PadInput.Clear(),
                PadInput.Symbol('0'),
                PadInput.Accept(false),
            ),
        )
    }
}

sealed class PadInput(open val isEnabled: Boolean) {
    data class Symbol(val symbol: Char, override val isEnabled: Boolean = true): PadInput(isEnabled)
    data class Accept(override val isEnabled: Boolean = true): PadInput(isEnabled)
    data class Clear(override val isEnabled: Boolean = true): PadInput(isEnabled)
}

sealed interface PinCodeEvent {
    data object InvalidPinCode: PinCodeEvent
    data object Idle: PinCodeEvent
}

sealed interface PinCodeMode {
    data object SetNewPinCode: PinCodeMode
    data object EnterPinCode: PinCodeMode
}
