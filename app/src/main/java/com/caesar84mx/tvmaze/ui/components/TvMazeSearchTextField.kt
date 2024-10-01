package com.caesar84mx.tvmaze.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.caesar84mx.tvmaze.R

@Composable
fun TvMazeSearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    placeholder: String,
    onSearch: (String) -> Unit,
) {
    OutlinedTextField(
        modifier = modifier,
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null,
            )
        },
        maxLines = 1,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        value = value,
        onValueChange = onValueChange,
        keyboardActions = KeyboardActions(
            onSearch = { onSearch(value) }
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Search,
        ),
    )
}