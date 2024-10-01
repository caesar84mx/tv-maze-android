package com.caesar84mx.tvmaze.ui.components

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.caesar84mx.tvmaze.ui.screens.episode_details_screen.EpisodeDetailsScreen
import com.caesar84mx.tvmaze.ui.screens.pin_code_screen.PinCodeScreen
import com.caesar84mx.tvmaze.ui.screens.show_details_screen.ShowDetailsScreen
import com.caesar84mx.tvmaze.ui.screens.home_screen.HomeScreen
import com.caesar84mx.tvmaze.util.Constants.FADE_IN_DURATION
import com.caesar84mx.tvmaze.util.Constants.FADE_OUT_DURATION
import com.caesar84mx.tvmaze.util.Constants.SLIDE_IN_DURATION
import com.caesar84mx.tvmaze.util.Constants.SLIDE_OUT_DURATION
import com.caesar84mx.tvmaze.util.navigation.Back
import com.caesar84mx.tvmaze.util.navigation.Destination
import com.caesar84mx.tvmaze.util.navigation.EpisodeDetails
import com.caesar84mx.tvmaze.util.navigation.Home
import com.caesar84mx.tvmaze.util.navigation.Navigator
import com.caesar84mx.tvmaze.util.navigation.PinCode
import com.caesar84mx.tvmaze.util.navigation.ShowDetails

@Composable
fun TvMazeMainNavigationView(navigator: Navigator) {
    val navController = rememberNavController()

    LaunchedEffect("navigation") {
        navigator.navigationCommand.collect { destination ->
            when (destination) {
                is Destination -> {
                    navController.navigate(destination.route)
                }

                Back -> {
                    navController.navigateUp()
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = PinCode.route,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(SLIDE_IN_DURATION)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(SLIDE_OUT_DURATION)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(SLIDE_IN_DURATION)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(SLIDE_OUT_DURATION)
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) {
        composable(
            route = PinCode.route,

            enterTransition = {
                fadeIn(tween(FADE_IN_DURATION))
            },
            exitTransition = {
                fadeOut(tween(FADE_OUT_DURATION))
            },
        ) {
            PinCodeScreen()
        }
        composable(
            route = Home.route,
            enterTransition = {
                fadeIn(tween(FADE_IN_DURATION))
            },
            exitTransition = {
                fadeOut(tween(FADE_OUT_DURATION))
            },
        ) {
            HomeScreen()
        }
        composable(ShowDetails.CANONICAL_ROUTE) {
            val showId = it.arguments?.getString("showId")?.toInt()
            ShowDetailsScreen(showId = showId)
        }
        composable(EpisodeDetails.CANONICAL_ROUTE) {
            val episodeId = it.arguments?.getString("episodeId")?.toInt()
            EpisodeDetailsScreen(showId = episodeId)
        }
    }
}
