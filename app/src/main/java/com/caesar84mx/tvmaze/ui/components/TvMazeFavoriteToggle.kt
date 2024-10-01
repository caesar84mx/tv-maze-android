package com.caesar84mx.tvmaze.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.caesar84mx.tvmaze.R

@Composable
fun TvMazeFavoriteToggle(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    onFavoriteClicked: () -> Unit
) {
    val icon = if (isFavorite) {
        R.drawable.ic_favorite_fill
    } else {
        R.drawable.ic_favorite_empty
    }

    IconButton(
        modifier = modifier,
        onClick = onFavoriteClicked
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = if (isFavorite) "remove from favorites" else "add to favorites",
            tint = if (isFavorite) {
                androidx.compose.ui.graphics.Color.Red
            } else {
                androidx.compose.ui.graphics.Color.LightGray
            }
        )
    }
}