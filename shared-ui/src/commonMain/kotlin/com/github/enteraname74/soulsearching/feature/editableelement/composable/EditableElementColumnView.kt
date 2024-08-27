package com.github.enteraname74.soulsearching.feature.editableelement.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.editableelement.domain.EditableElement

@Composable
fun EditableElementColumnView(
    coverSectionTitle: String,
    editableElement: EditableElement,
    onSelectImage: () -> Unit,
    focusManager: FocusManager,
    textFields: List<SoulTextFieldHolder>,
    textFieldsLabels: List<String>,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoulSearchingColorTheme.colorScheme.secondary)
            .padding(top = UiConstants.Spacing.medium)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EditableElementCoverSection(
            modifier = Modifier
                .padding(UiConstants.Spacing.medium),
            title = coverSectionTitle,
            editableElement = editableElement,
            onSelectImage = onSelectImage,
        )
        EditableElementTextFieldsView(
            focusManager = focusManager,
            textFields = textFields,
            textFieldsLabels = textFieldsLabels,
            Modifier
                .fillMaxSize()
                .weight(1F)
                .clip(RoundedCornerShape(topStart = 50f, topEnd = 50f))
                .background(color = SoulSearchingColorTheme.colorScheme.primary)
                .padding(UiConstants.Spacing.medium),
        )
    }
}