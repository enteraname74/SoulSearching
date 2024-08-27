package com.github.enteraname74.soulsearching.coreui.textfield

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager

abstract class SoulTextFieldHolder(
    val id: String,
    initialValue: String = "",
    private val isValid: (value: String) -> Boolean = { true },
) {
    var value: String by mutableStateOf(initialValue)
        protected set

    fun isValid(): Boolean = isValid(value)

    open fun onValueChanged(newValue: String) {
        value = newValue
    }

    @Composable
    abstract fun TextField(
        focusManager: FocusManager,
        label: String?,
    )
}