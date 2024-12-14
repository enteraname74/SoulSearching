package com.github.enteraname74.soulsearching.coreui.textfield

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import com.github.enteraname74.soulsearching.coreui.UiConstants


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoulTextField(
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    isInError: Boolean,
    labelName: String?,
    focusManager: FocusManager,
    modifier: Modifier = Modifier,
    colors: SoulTextFieldColors = SoulTextFieldDefaults.secondaryColors(),
    style: SoulTextFieldStyle,
    keyboardType: KeyboardType = KeyboardType.Text,
    keyboardActions: KeyboardActions = KeyboardActions(
        onDone = { focusManager.clearFocus() }
    ),
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val textSelectionColors = TextSelectionColors(
        handleColor = colors.selectionContentColor,
        backgroundColor = colors.selectionContainerColor,
    )

    CompositionLocalProvider(
        LocalTextSelectionColors provides textSelectionColors
    ) {
        SoulTextFieldViewHolder(
            colors = colors,
            style = style,
            isFocused = isFocused,
        ) {
//        Column {
//            labelName?.let {
//                Text(
//                    text = it,
//                    style = UiConstants.Typography.bodyVerySmall.copy(color = colors.labelColor),
//                )
//            }
//            TextField(
//                modifier = modifier,
//                value = value,
//                onValueChange = onValueChange,
//            )
//            AnimatedVisibility(
//                enter = slideInVertically(),
//                exit = slideOutVertically(),
//                visible = isInError,
//            ) {
//                Text(
//                    text = error.orEmpty(),
//                    style = UiConstants.Typography.bodyVerySmall.copy(color = colors.labelColor),
//                )
//            }
//        }
            BasicTextField(
                modifier = modifier,
                value = value,
                onValueChange = onValueChange,
                interactionSource = interactionSource,
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType,
                ),
                textStyle = UiConstants.Typography.bodyLarge.copy(color = colors.contentColor),
                keyboardActions = keyboardActions,
                cursorBrush = SolidColor(colors.contentColor),
                decorationBox = { innerTextField ->
                    TextFieldDefaults.DecorationBox(
                        isError = isInError,
                        enabled = true,
                        value = value,
                        singleLine = true,
                        interactionSource = interactionSource,
                        visualTransformation = VisualTransformation.None,
                        contentPadding = PaddingValues(
                            vertical = UiConstants.Spacing.mediumPlus,
                            horizontal = UiConstants.Spacing.mediumPlus,
                        ),
                        trailingIcon = trailingIcon,
                        innerTextField = {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small)
                            ) {
                                labelName?.let {
                                    Text(
                                        text = it,
                                        style = UiConstants.Typography.bodyVerySmall.copy(color = colors.labelColor),
                                    )
                                }
                                innerTextField()
                                AnimatedVisibility(
                                    enter = slideInVertically(),
                                    exit = slideOutVertically(),
                                    visible = isInError,
                                ) {
                                    Text(
                                        text = error.orEmpty(),
                                        style = UiConstants.Typography.bodyVerySmall.copy(color = colors.labelColor),
                                    )
                                }
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                            focusedTextColor = colors.contentColor,
                            unfocusedTextColor = colors.contentColor,
                            selectionColors = TextSelectionColors(
                                handleColor = colors.selectionContentColor,
                                backgroundColor = colors.selectionContainerColor,
                            ),
                        )
                    )
                },
            )
        }
    }
}

class SoulTextFieldHolderImpl(
    id: String,
    initialValue: String = "",
    isValid: (value: String) -> Boolean = { true },
    getLabel: @Composable () -> String?,
    getError: @Composable () -> String?,
    getColors: @Composable () -> SoulTextFieldColors = { SoulTextFieldDefaults.secondaryColors() },
    private val modifier: Modifier = Modifier,
    private val style: SoulTextFieldStyle = SoulTextFieldStyle.Unique,
    private val onValueChange: ((String) -> Unit)? = null,
    private val keyboardType: KeyboardType = KeyboardType.Text,
) : SoulTextFieldHolder(
    initialValue = initialValue,
    isValid = isValid,
    id = id,
    getLabel = getLabel,
    getColors = getColors,
    getError = getError,
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
            modifier = modifier,
            value = value,
            onValueChange = ::onValueChanged,
            labelName = label,
            focusManager = focusManager,
            keyboardType = keyboardType,
            style = style,
            colors = colors,
            error = error,
            isInError = isInError,
        )
    }
}