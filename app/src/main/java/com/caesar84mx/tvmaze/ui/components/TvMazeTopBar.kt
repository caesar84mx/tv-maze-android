package com.caesar84mx.tvmaze.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.caesar84mx.tvmaze.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvMazeTopBar(properties: TopBarProperties) {
    TopAppBar(
        navigationIcon = {
            if (properties.showBackButton) {
                IconButton(properties.onBackClicked) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = "Back"
                    )
                }
            }
        },
        title = { Text(text = properties.title) },
        actions = {
            properties.actions.forEach { (label, action) ->
                IconButton(action) {
                    Icon(
                        painter = painterResource(label),
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = "Back"
                    )
                }
            }
        }
    )
}