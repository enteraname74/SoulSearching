package com.github.enteraname74.soulsearching.coreui.textfield

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager

abstract class SoulTextFieldHolder(
    val id: String,
    initialValue: String = "",
    private val getLabel: @Composable () -> String?,
    private val getError: @Composable () -> String?,
    private val getColors: @Composable () -> SoulTextFieldColors,
    private val onChange: (newValue: String) -> Unit,
    private val isValid: (value: String) -> Boolean = { true },
) {
    var value: String by mutableStateOf(initialValue)
        protected set

    private var forceNoError: Boolean by mutableStateOf(true)

    val label: String?
        @Composable
        get() = getLabel()

    val error: String?
        @Composable
        get() = getError()

    val isInError: Boolean by derivedStateOf { !isValid(value) && !forceNoError }

    val colors: SoulTextFieldColors
        @Composable
        get() = getColors()

    fun isValid(): Boolean = isValid(value)

    open fun onValueChanged(newValue: String) {
        value = newValue
        forceNoError = false
        onChange(newValue)
    }

    @Composable
    abstract fun TextField(
        modifier: Modifier,
        focusManager: FocusManager,
    )
}