package com.github.enteraname74.soulsearching.feature.editableelement.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder

@Composable
fun EditableElementTextFieldsView(
    focusManager: FocusManager,
    textFields: List<SoulTextFieldHolder>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        textFields.forEach { textField ->
            textField.TextField(
                focusManager = focusManager,
            )
        }
        SoulPlayerSpacer()
    }
}