package com.caesar84mx.tvmaze.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.caesar84mx.tvmaze.ui.components.TvMazeMainNavigationView
import com.caesar84mx.tvmaze.ui.theme.TvMazeTheme
import com.caesar84mx.tvmaze.util.navigation.NavigationEvent
import com.caesar84mx.tvmaze.util.navigation.Navigator
import com.caesar84mx.tvmaze.util.navigation.Quit
import com.caesar84mx.tvmaze.util.navigation.SystemNavigationEvent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val navigator: Navigator by inject()
    private var latestDestination: NavigationEvent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        navigator.navigationCommand.onEach { destination ->
            if (destination !is SystemNavigationEvent) {
                latestDestination = destination
            }

            when (destination) {
                is Quit -> finish()
            }
        }.launchIn(lifecycleScope)

        setContent {
            TvMazeTheme {
                TvMazeMainNavigationView(navigator)
            }
        }
    }
}
