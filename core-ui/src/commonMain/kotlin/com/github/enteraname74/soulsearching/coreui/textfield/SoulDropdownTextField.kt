package com.github.enteraname74.soulsearching.coreui.textfield

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoulDropdownTextField(
    values: List<String>,
    value: String,
    onValueChange: (String) -> Unit,
    labelName: String?,
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

    val focusRequester = remember { FocusRequester() }

    ExposedDropdownMenuBox(
        expanded = isExpanded && isFocused,
        onExpandedChange = { isExpanded = it }
    ) {
        TextField(
            interactionSource = interactionSource,
            modifier = Modifier
                .menuAnchor()
                .focusRequester(focusRequester),
            value = value,
            onValueChange = {
                isDropdownExpanded = it != value
                focusRequester.requestFocus()
                onValueChange(it)
            },
            label = {
                labelName?.let {
                    Text(text = it)
                }
            },
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

class SoulDropdownTextFieldHolderImpl(
    id: String,
    initialValue: String = "",
    isValid: (value: String) -> Boolean = { true },
    private val updateProposedValues: suspend (fieldValue: String) -> List<String>,
): SoulTextFieldHolder(
    initialValue = initialValue,
    isValid = isValid,
    id = id,
) {
    private var values: List<String> by mutableStateOf(emptyList())
    private var updateJob: Job? = null

    override fun onValueChanged(newValue: String) {
        super.onValueChanged(newValue)
        if (updateJob?.isActive == true) {
            updateJob?.cancel()
        }
        updateJob = CoroutineScope(Dispatchers.IO).launch {
            values = updateProposedValues(newValue)
        }
    }

    @Composable
    override fun TextField(
        focusManager: FocusManager,
        label: String?,
    ) {
        SoulDropdownTextField(
            value = value,
            onValueChange = ::onValueChanged,
            labelName = label,
            focusManager = focusManager,
            values = values,
        )
    }
}