package com.github.soulsearching.modifyelement.composable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import com.github.soulsearching.colortheme.domain.model.SoulSearchingColorTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownTextField(
    values: List<String>,
    value: String,
    onValueChange: (String) -> Unit,
    labelName: String,
    focusManager: FocusManager
) {
    var isExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    var isDropdownExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        expanded = isExpanded && isFocused,
        onExpandedChange = { isExpanded = it }
    ) {
        TextField(
            interactionSource = interactionSource,
            modifier = Modifier.menuAnchor(),
            value = value,
            onValueChange = {
                isDropdownExpanded = it != value
                onValueChange(it)
            },
            label = { Text(text = labelName) },
            singleLine = true,
            colors = ExposedDropdownMenuDefaults.textFieldColors(
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
                unfocusedLabelColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                focusedLeadingIconColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                unfocusedLeadingIconColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                focusedTrailingIconColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                unfocusedTrailingIconColor = SoulSearchingColorTheme.colorScheme.onPrimary,
            ),
            trailingIcon = {
                val rotation by animateFloatAsState(targetValue = if (isExpanded && isFocused) 180f else 0f)
                Icon(
                    Icons.Filled.ArrowDropDown,
                    null,
                    Modifier.rotate(rotation)
                )
            },
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    isExpanded = false
                }
            )
        )
        ExposedDropdownMenu(
            expanded = isDropdownExpanded && values.isNotEmpty(),
            onDismissRequest = {
                isDropdownExpanded = false
            }
        ) {
            values.forEach { value ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = value
                        )
                    },
                    onClick = {
                        onValueChange(value)
                    }
                )
            }
        }
    }
}