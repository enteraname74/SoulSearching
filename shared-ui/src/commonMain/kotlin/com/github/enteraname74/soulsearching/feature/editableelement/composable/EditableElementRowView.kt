package com.github.enteraname74.soulsearching.feature.editableelement.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.editableelement.domain.EditableElement

@Composable
fun EditableElementRowView(
    coverSectionTitle: String,
    editableElement: EditableElement,
    onSelectImage: () -> Unit,
    focusManager: FocusManager,
    textFields: List<SoulTextFieldHolder>,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(UiConstants.Spacing.medium)
            .background(SoulSearchingColorTheme.colorScheme.primary)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus()
                    }
                )
            }
    ) {
        EditableElementCoverSection(
            modifier = Modifier
                .weight(1F),
            title = coverSectionTitle,
            editableElement = editableElement,
            onSelectImage = onSelectImage,
        )
        Box(
            modifier = Modifier
                .weight(2f)
                .verticalScroll(rememberScrollState())
                .padding(
                    top = UiConstants.Spacing.medium
                )
        ) {
            EditableElementTextFieldsView(
                focusManager = focusManager,
                textFields = textFields,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .align(Alignment.TopCenter),
            )
        }
    }
}