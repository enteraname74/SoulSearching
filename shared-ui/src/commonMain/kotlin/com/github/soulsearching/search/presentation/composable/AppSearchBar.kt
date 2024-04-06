package com.github.soulsearching.search.presentation.composable

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
import com.github.soulsearching.colortheme.domain.model.SoulSearchingColorTheme

@Composable
fun AppSearchBar(
    searchText: String,
    placeholder: String,
    updateTextMethod: (String) -> Unit,
    focusManager: FocusManager,
    primaryColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary
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
                    tint = textColor,
                    modifier = Modifier
                        .clickable {
                            updateTextMethod("")
                        }
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "",
                    tint = textColor,
                )
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(percent = 50),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = primaryColor,
            focusedTextColor = textColor,
            cursorColor = textColor,
            focusedIndicatorColor = Color.Transparent,
            focusedLabelColor = textColor,
            unfocusedTextColor = textColor,
            focusedContainerColor = primaryColor,
            selectionColors = TextSelectionColors(
                handleColor = textColor,
                backgroundColor = primaryColor
            ),
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedLabelColor = textColor,
            focusedPlaceholderColor = textColor,
            unfocusedPlaceholderColor = textColor
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() }
        )
    )
}