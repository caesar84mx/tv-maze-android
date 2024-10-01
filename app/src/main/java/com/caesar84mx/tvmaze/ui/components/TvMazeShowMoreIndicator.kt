package com.caesar84mx.tvmaze.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ContextualFlowRowOverflowScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ContextualFlowRowOverflowScope.TvMazeShowMoreIndicator(
    modifier: Modifier = Modifier,
    totalItems: Int,
    onMoreClicked: () -> Unit,
) {
    val remainingItems = totalItems - shownItemCount
    Card(
        modifier = modifier,
        onClick = onMoreClicked,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+$remainingItems",
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}