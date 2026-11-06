package com.github.enteraname74.soulsearching.feature.editableelement.composable.views

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.editableelement.composable.EditableElementTextFieldsView
import com.github.enteraname74.soulsearching.feature.editableelement.composable.cover.EditableElementCoverSection
import com.github.enteraname74.soulsearching.feature.editableelement.domain.CoverEditMode

@Composable
fun EditableElementLargeView(
    coverSectionTitle: SoulTextSpec,
    coverEditMode: CoverEditMode,
    onSelectImage: (pos: Int) -> Unit,
    onSwitchModeType: (new: CoverEditMode.Type) -> Unit,
    focusManager: FocusManager,
    textFields: List<SoulTextFieldHolder>,
    extraFormTopContent: @Composable (() -> Unit)?,
    extraFormBottomContent: @Composable (() -> Unit)?,
) {
    BoxWithConstraints {
        val maxWidthWithoutPadding = maxWidth - (UiConstants.Spacing.large * 2) - UiConstants.Spacing.veryLarge

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(UiConstants.Spacing.large)
                    .background(SoulSearchingColorTheme.colorScheme.background)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                focusManager.clearFocus()
                            }
                        )
                    },
                horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.veryLarge),
            ) {
                val coverWidth = maxWidthWithoutPadding * .45f
                EditableElementCoverSection(
                    modifier = Modifier.width(coverWidth),
                    title = coverSectionTitle,
                    coverEditMode = coverEditMode,
                    onSelectImage = onSelectImage,
                    onSwitchModeType = onSwitchModeType,
                    coverSize = coverWidth,
                )
                EditableElementTextFieldsView(
                    focusManager = focusManager,
                    textFields = textFields,
                    extraFormBottomContent = extraFormBottomContent,
                    extraFormTopContent = extraFormTopContent,
                )
            }
        }
    }
}
