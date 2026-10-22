package com.github.enteraname74.soulsearching.feature.editableelement.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.editableelement.domain.CoverEditMode

@Composable
fun EditableElementColumnView(
    coverSectionTitle: String,
    coverEditMode: CoverEditMode,
    onSelectImage: (pos: Int) -> Unit,
    onSwitchModeType: (new: CoverEditMode.Type) -> Unit,
    focusManager: FocusManager,
    textFields: List<SoulTextFieldHolder>,
    extraFormBottomContent: @Composable (() -> Unit)?,
    extraFormTopContent: @Composable (() -> Unit)?,
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        LazyColumnCompat(
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
                    coverEditMode = coverEditMode,
                    onSelectImage = onSelectImage,
                    onSwitchModeType = onSwitchModeType,
                )
            }
            editableElementTextFieldsView(
                focusManager = focusManager,
                textFields = textFields,
                extraFormBottomContent = extraFormBottomContent,
                extraFormTopContent = extraFormTopContent,
            )
        }
    }
}
