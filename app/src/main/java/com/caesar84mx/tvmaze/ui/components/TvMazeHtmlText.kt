package com.caesar84mx.tvmaze.ui.components

import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat

@Composable
fun TvMazeHtmlText(
    modifier: Modifier = Modifier,
    text: String,
    maxLines: Int = Int.MAX_VALUE,
    size: TextUnit = MaterialTheme.typography.bodySmall.fontSize,
    color: Color = MaterialTheme.colorScheme.onBackground,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                movementMethod = LinkMovementMethod.getInstance()
                textSize = size.value
                this.maxLines = maxLines
                ellipsize = TextUtils.TruncateAt.END
                setTextColor(color.toArgb())
            }
        },
        update = { it.text = text.formatHtml() }
    )
}

fun String.formatHtml(): Spanned {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY)
}