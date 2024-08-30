package com.github.enteraname74.soulsearching.coreui.textfield

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun SoulTextField(
    value : String,
    onValueChange : (String) -> Unit,
    labelName : String?,
    focusManager : FocusManager,
    keyboardType : KeyboardType = KeyboardType.Text,
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
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        )
    )
}

class SoulTextFieldHolderImpl(
    id: String,
    initialValue: String = "",
    isValid: (value: String) -> Boolean = { true },
    getLabel: @Composable () -> String?,
    private val onValueChange: ((String) -> Unit)? = null,
    private val keyboardType: KeyboardType = KeyboardType.Text,
): SoulTextFieldHolder(
    initialValue = initialValue,
    isValid = isValid,
    id = id,
    getLabel = getLabel,
) {

    override fun onValueChanged(newValue: String) {
        super.onValueChanged(newValue)
        this.onValueChange?.let { it(newValue) }
    }

    @Composable
    override fun TextField(
        focusManager: FocusManager
    ) {
        SoulTextField(
            value = value,
            onValueChange = ::onValueChanged,
            labelName = label,
            focusManager = focusManager,
            keyboardType = keyboardType,
        )
    }
}