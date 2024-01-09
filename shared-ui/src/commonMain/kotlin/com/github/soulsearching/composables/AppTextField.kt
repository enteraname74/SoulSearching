package com.github.soulsearching.composables

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import com.github.soulsearching.theme.DynamicColor

@Composable
fun AppTextField(
    value : String,
    onValueChange : (String) -> Unit,
    labelName : String,
    focusManager : FocusManager
) {
    TextField(
        value = value,
        onValueChange = { onValueChange(it) },
        label = { Text(text = labelName) },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedTextColor = DynamicColor.onPrimary,
            cursorColor = DynamicColor.onPrimary,
            focusedIndicatorColor = DynamicColor.onPrimary,
            focusedLabelColor = DynamicColor.onPrimary,
            unfocusedTextColor = DynamicColor.onPrimary,
            focusedContainerColor = Color.Transparent,
            selectionColors = TextSelectionColors(
                handleColor = DynamicColor.onPrimary,
                backgroundColor = DynamicColor.secondary
            ),
            unfocusedIndicatorColor = DynamicColor.onPrimary,
            unfocusedLabelColor = DynamicColor.onPrimary
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() }
        )
    )
}