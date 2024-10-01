package com.caesar84mx.tvmaze.ui.components

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun TvMazeBackPressListener(onBackPressed: (() -> Unit)?) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val currentOnBack by rememberUpdatedState {
        onBackPressed?.invoke()
    }

    val backCallback = onBackPressed?.let {
        remember {
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    currentOnBack()
                }
            }
        }.apply { isEnabled = true }
    }

    val backDispatcher = onBackPressed?.let { LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher }

    DisposableEffect(backDispatcher, lifecycleOwner) {
        backCallback?.let { callback -> backDispatcher?.addCallback(lifecycleOwner, callback) }
        onDispose {
            onBackPressed?.let { backCallback?.remove() }
        }
    }
}