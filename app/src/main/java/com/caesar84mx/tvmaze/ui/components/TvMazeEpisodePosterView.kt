package com.caesar84mx.tvmaze.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.caesar84mx.tvmaze.R
import com.caesar84mx.tvmaze.viewmodels.EpisodeUi

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun TvMazeEpisodePosterView(
    episode: EpisodeUi,
    onClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        onClick = { onClicked(episode.id) }
    ) {
        Box {
            if (episode.image.isNotEmpty()) {
                GlideImage(
                    model = episode.image,
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    loading = placeholder(painter = painterResource(R.drawable.ic_landscape)),
                    failure = placeholder(painter = painterResource(R.drawable.ic_landscape)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_landscape),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                )
            }

            Column {
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f))
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TvMazeRatingView(
                        rating = episode.rating,
                        modifier = Modifier.padding(vertical = 5.dp)
                    )

                    Text(
                        text = episode.name,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(
                            start = 10.dp,
                            end = 10.dp,
                            bottom = 5.dp,
                        )
                    )
                }
            }
        }
    }
}