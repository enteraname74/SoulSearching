package com.github.enteraname74.soulsearching.feature.editableelement.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.coreui.topbar.SoulAdaptiveTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBarAction
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.feature.editableelement.composable.views.EditableElementLargeView
import com.github.enteraname74.soulsearching.feature.editableelement.composable.views.EditableElementSmallView
import com.github.enteraname74.soulsearching.feature.editableelement.domain.CoverEditMode

@Composable
fun EditableElementView(
    title: SoulTextSpec,
    coverSectionTitle: SoulTextSpec,
    coverEditMode: CoverEditMode,
    navigateBack: () -> Unit,
    onSelectCover: (pos: Int) -> Unit,
    onSwitchModeType: (new: CoverEditMode.Type) -> Unit,
    onValidateModification: () -> Unit,
    isValid: () -> Boolean,
    textFields: List<SoulTextFieldHolder>,
    extraFormBottomContent: @Composable (() -> Unit)? = null,
    extraFormTopContent: @Composable (() -> Unit)? = null,
) {
    val focusManager = LocalFocusManager.current
    val windowSize = rememberWindowSize()

    SoulScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            SoulAdaptiveTopBar(
                title = title,
                leftAction = SoulTopBarAction.navigation(
                    onClick = navigateBack,
                ),
                rightAction = SoulTopBarAction.validate(
                    onClick = onValidateModification,
                    enabled = isValid(),
                )
            )
            when (windowSize) {
                WindowSize.Small -> EditableElementSmallView(
                    coverEditMode = coverEditMode,
                    onSelectImage = onSelectCover,
                    focusManager = focusManager,
                    textFields = textFields,
                    coverSectionTitle = coverSectionTitle,
                    extraFormTopContent = extraFormTopContent,
                    extraFormBottomContent = extraFormBottomContent,
                    onSwitchModeType = onSwitchModeType,
                )

                else -> EditableElementLargeView(
                    coverEditMode = coverEditMode,
                    onSelectImage = onSelectCover,
                    focusManager = focusManager,
                    textFields = textFields,
                    coverSectionTitle = coverSectionTitle,
                    extraFormTopContent = extraFormTopContent,
                    extraFormBottomContent = extraFormBottomContent,
                    onSwitchModeType = onSwitchModeType,
                )
            }
        }
    }
}
