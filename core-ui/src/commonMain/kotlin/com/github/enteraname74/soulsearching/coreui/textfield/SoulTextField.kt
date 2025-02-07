package com.github.enteraname74.soulsearching.coreui.textfield

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.animation.VerticalAnimatedVisibility
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoulTextField(
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    isInError: Boolean,
    isActive: Boolean,
    labelName: String?,
    focusManager: FocusManager,
    modifier: Modifier = Modifier,
    colors: SoulTextFieldColors = SoulTextFieldDefaults.secondaryColors(),
    style: SoulTextFieldStyle,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Done,
    ),
    keyboardActions: KeyboardActions = KeyboardActions(
        onDone = { focusManager.clearFocus() }
    ),
    isPassword: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val textSelectionColors = TextSelectionColors(
        handleColor = colors.selectionContentColor,
        backgroundColor = colors.selectionContainerColor,
    )

    var isPasswordShown by rememberSaveable {
        mutableStateOf(false)
    }

    val finalTrailingIcon: @Composable (() -> Unit)? = if (isPassword) {
        {
            PasswordTrailingIcon(
                isPasswordShown = isPasswordShown,
                togglePasswordVisibility = {
                    isPasswordShown = !isPasswordShown
                },
                tint = colors.contentColor,
            )
        }
    } else {
        trailingIcon
    }

    val visualTransformation = if (!isPassword || isPasswordShown) {
        VisualTransformation.None
    } else {
        PasswordVisualTransformation()
    }

    CompositionLocalProvider(
        LocalTextSelectionColors provides textSelectionColors
    ) {
        SoulTextFieldViewHolder(
            colors = colors,
            style = style,
            isFocused = isFocused,
        ) {
            BasicTextField(
                modifier = modifier,
                value = value,
                readOnly = isActive,
                onValueChange = onValueChange,
                interactionSource = interactionSource,
                maxLines = 1,
                singleLine = true,
                keyboardOptions = keyboardOptions,
                textStyle = UiConstants.Typography.bodyLarge.copy(color = colors.contentColor),
                keyboardActions = keyboardActions,
                cursorBrush = SolidColor(colors.contentColor),
                visualTransformation = visualTransformation,
                decorationBox = { innerTextField ->
                    TextFieldDefaults.DecorationBox(
                        isError = isInError,
                        enabled = true,
                        value = value,
                        singleLine = true,
                        interactionSource = interactionSource,
                        visualTransformation = visualTransformation,
                        contentPadding = PaddingValues(
                            vertical = UiConstants.Spacing.mediumPlus,
                            horizontal = UiConstants.Spacing.mediumPlus,
                        ),
                        trailingIcon = finalTrailingIcon,
                        leadingIcon = leadingIcon,
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
                                VerticalAnimatedVisibility(
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

@Composable
private fun PasswordTrailingIcon(
    isPasswordShown: Boolean,
    togglePasswordVisibility: () -> Unit,
    tint: Color,
) {
    SoulIcon(
        icon = if (isPasswordShown) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
        tint = tint,
        contentDescription = null,
        modifier = Modifier.clickableWithHandCursor {
            togglePasswordVisibility()
        }
    )
}

class SoulTextFieldHolderImpl(
    id: String,
    initialValue: String = "",
    isValid: (value: String) -> Boolean = { true },
    getLabel: @Composable () -> String?,
    getError: @Composable () -> String?,
    getColors: @Composable () -> SoulTextFieldColors = { SoulTextFieldDefaults.secondaryColors() },
    private val isActive: Boolean = false,
    private val isPassword: Boolean = false,
    private val leadingIconSpec: SoulTextFieldLeadingIconSpec? = null,
    private val modifier: Modifier = Modifier,
    private val style: SoulTextFieldStyle = SoulTextFieldStyle.Unique,
    private val keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Done,
    ),
    onChange: (String) -> Unit = {},
) : SoulTextFieldHolder(
    initialValue = initialValue,
    isValid = isValid,
    id = id,
    getLabel = getLabel,
    getColors = getColors,
    getError = getError,
    onChange = onChange,
) {

    @Composable
    override fun TextField(
        modifier: Modifier,
        focusManager: FocusManager
    ) {
        SoulTextField(
            modifier = this.modifier.then(modifier),
            value = value,
            onValueChange = ::onValueChanged,
            labelName = label,
            focusManager = focusManager,
            keyboardOptions = keyboardOptions,
            style = style,
            colors = colors,
            error = error,
            isInError = isInError,
            isPassword = isPassword,
            isActive = isActive,
            leadingIcon = leadingIconSpec?.let {
                {
                    SoulIcon(
                        icon = it.icon,
                        contentDescription = null,
                        tint = colors.contentColor,
                        modifier = Modifier.clickableWithHandCursor {
                            it.onClick()
                        }
                    )
                }
            }
        )
    }
}