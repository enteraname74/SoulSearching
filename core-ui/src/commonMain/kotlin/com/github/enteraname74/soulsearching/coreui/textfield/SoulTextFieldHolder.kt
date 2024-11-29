package com.github.enteraname74.soulsearching.coreui.textfield

import androidx.compose.runtime.*
import androidx.compose.ui.focus.FocusManager

abstract class SoulTextFieldHolder(
    val id: String,
    initialValue: String = "",
    private val getLabel: @Composable () -> String?,
    private val getError: @Composable () -> String?,
    private val getColors: @Composable () -> SoulTextFieldColors,
    private val isValid: (value: String) -> Boolean = { true },
) {
    var value: String by mutableStateOf(initialValue)
        protected set

    val label: String?
        @Composable
        get() = getLabel()

    val error: String?
        @Composable
        get() = getError()

    val isInError: Boolean by derivedStateOf { !isValid(value) }

    val colors: SoulTextFieldColors
        @Composable
        get() = getColors()

    fun isValid(): Boolean = isValid(value)

    open fun onValueChanged(newValue: String) {
        value = newValue
    }

    @Composable
    abstract fun TextField(
        focusManager: FocusManager,
    )
}