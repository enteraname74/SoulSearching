package com.github.enteraname74.soulsearching.feature.editableelement.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
fun EditableElementColumnView(
    coverSectionTitle: String,
    editableElement: EditableElement,
    onSelectImage: () -> Unit,
    focusManager: FocusManager,
    textFields: List<SoulTextFieldHolder>,
    extraContent: @Composable (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        LazyColumn(
            modifier = Modifier
                .background(SoulSearchingColorTheme.colorScheme.primary)
                .padding(
                    top = UiConstants.Spacing.small,
                    start = UiConstants.Spacing.medium,
                    end = UiConstants.Spacing.medium,
                )
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
                .fillMaxHeight()
                .fillMaxWidth(0.9f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                EditableElementCoverSection(
                    modifier = Modifier
                        .animateItem()
                        .padding(
                            bottom = UiConstants.Spacing.large
                        ),
                    title = coverSectionTitle,
                    editableElement = editableElement,
                    onSelectImage = onSelectImage,
                )
            }
            editableElementTextFieldsView(
                focusManager = focusManager,
                textFields = textFields,
                extraContent = extraContent,
            )
        }
    }
}