package com.github.enteraname74.soulsearching.feature.editableelement.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
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
    extraFormTopContent: @Composable (() -> Unit)?,
    extraFormBottomContent: @Composable (() -> Unit)?,
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
                .padding(
                    top = UiConstants.Spacing.medium
                )
        ) {
            LazyColumnCompat(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                editableElementTextFieldsView(
                    focusManager = focusManager,
                    textFields = textFields,
                    extraFormBottomContent = extraFormBottomContent,
                    extraFormTopContent = extraFormTopContent,
                )
            }
        }
    }
}