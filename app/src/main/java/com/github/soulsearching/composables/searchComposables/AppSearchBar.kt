package com.github.soulsearching.composables.searchComposables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.github.soulsearching.ui.theme.DynamicColor

@Composable
fun AppSearchBar(
    searchText: String,
    placeholder: String,
    updateTextMethod: (String) -> Unit,
    focusManager: FocusManager
) {
    TextField(
        modifier = Modifier
            .fillMaxWidth(),
        value = searchText,
        onValueChange = updateTextMethod,
        placeholder = {
            Text(
                text = placeholder,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingIcon = {
            if (searchText.isNotBlank()) {
                Icon(
                    imageVector = Icons.Rounded.Cancel,
                    contentDescription = "",
                    tint = DynamicColor.onSecondary,
                    modifier = Modifier
                        .clickable {
                            updateTextMethod("")
                        }
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "",
                    tint = DynamicColor.onSecondary,
                )
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(percent = 50),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = DynamicColor.secondary,
            focusedTextColor = DynamicColor.onSecondary,
            cursorColor = DynamicColor.onSecondary,
            focusedIndicatorColor = Color.Transparent,
            focusedLabelColor = DynamicColor.onSecondary,
            unfocusedTextColor = DynamicColor.onSecondary,
            focusedContainerColor = DynamicColor.secondary,
            selectionColors = TextSelectionColors(
                handleColor = DynamicColor.onSecondary,
                backgroundColor = DynamicColor.secondary
            ),
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedLabelColor = DynamicColor.onSecondary,
            focusedPlaceholderColor = DynamicColor.onSecondary,
            unfocusedPlaceholderColor = DynamicColor.onSecondary
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() }
        )
    )
}