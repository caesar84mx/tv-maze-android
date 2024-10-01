package com.caesar84mx.tvmaze.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun TvMazeRatingView(
    modifier: Modifier = Modifier,
    rating: Float?,
    showNumericalValue: Boolean = false
) {
    val maxRating = 10
    val numStars = 5

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val filledStars = rating?.let { (it / maxRating * numStars).roundToInt() } ?: 0

        for (i in 1..filledStars) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Filled Star",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        val emptyStars = numStars - filledStars
        for (i in 1..emptyStars) {
            Icon(
                imageVector = Icons.Outlined.Star,
                contentDescription = "Empty Star",
                tint = Color.Gray
            )
        }

        rating?.let {
            Spacer(modifier = Modifier.width(8.dp))
            if (showNumericalValue) {
                Text(text = "($it)", fontSize = 12.sp)
            }
        }
    }
}