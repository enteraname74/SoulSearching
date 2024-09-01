package com.github.enteraname74.soulsearching.feature.editableelement.composable

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder

@Composable
fun EditableElementTextFieldsView(
    focusManager: FocusManager,
    textFields: List<SoulTextFieldHolder>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1F)
        )
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(4F),
            verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium)
        ) {
            textFields.forEach { textField ->
                textField.TextField(
                    focusManager = focusManager,
                )
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1F)
        )
    }
}