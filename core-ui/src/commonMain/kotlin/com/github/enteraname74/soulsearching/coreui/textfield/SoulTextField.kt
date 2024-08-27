package com.github.enteraname74.soulsearching.coreui.textfield

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SoulTextField(
    value : String,
    onValueChange : (String) -> Unit,
    labelName : String?,
    focusManager : FocusManager
) {
    TextField(
        value = value,
        onValueChange = { onValueChange(it) },
        label = {
            labelName?.let { Text(text = it) }
        },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedTextColor = SoulSearchingColorTheme.colorScheme.onPrimary,
            cursorColor = SoulSearchingColorTheme.colorScheme.onPrimary,
            focusedIndicatorColor = SoulSearchingColorTheme.colorScheme.onPrimary,
            focusedLabelColor = SoulSearchingColorTheme.colorScheme.onPrimary,
            unfocusedTextColor = SoulSearchingColorTheme.colorScheme.onPrimary,
            focusedContainerColor = Color.Transparent,
            selectionColors = TextSelectionColors(
                handleColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                backgroundColor = SoulSearchingColorTheme.colorScheme.secondary
            ),
            unfocusedIndicatorColor = SoulSearchingColorTheme.colorScheme.onPrimary,
            unfocusedLabelColor = SoulSearchingColorTheme.colorScheme.onPrimary
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() }
        )
    )
}

class SoulTextFieldHolderImpl(
    id: String,
    initialValue: String = "",
    isValid: (value: String) -> Boolean = { true },
): SoulTextFieldHolder(
    initialValue = initialValue,
    isValid = isValid,
    id = id,
) {
    @Composable
    override fun TextField(
        focusManager: FocusManager,
        label: String?,
    ) {
        SoulTextField(
            value = value,
            onValueChange = ::onValueChanged,
            labelName = label,
            focusManager = focusManager
        )
    }
}