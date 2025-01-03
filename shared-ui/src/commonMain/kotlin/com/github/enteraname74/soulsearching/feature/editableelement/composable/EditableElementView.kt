package com.github.enteraname74.soulsearching.feature.editableelement.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarValidateAction
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.feature.editableelement.domain.EditableElement

@Composable
fun EditableElementView(
    title: String,
    coverSectionTitle: String,
    editableElement: EditableElement,
    navigateBack: () -> Unit,
    onSelectCover: () -> Unit,
    onValidateModification: () -> Unit,
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
            SoulTopBar(
                title = title,
                leftAction = TopBarNavigationAction(
                    onClick = navigateBack,
                ),
                rightAction = TopBarValidateAction(
                    onClick = onValidateModification,
                )
            )
            when (windowSize) {
                WindowSize.Small -> EditableElementColumnView(
                    editableElement = editableElement,
                    onSelectImage = onSelectCover,
                    focusManager = focusManager,
                    textFields = textFields,
                    coverSectionTitle = coverSectionTitle,
                    extraFormTopContent = extraFormTopContent,
                    extraFormBottomContent = extraFormBottomContent,
                )

                else -> EditableElementRowView(
                    editableElement = editableElement,
                    onSelectImage = onSelectCover,
                    focusManager = focusManager,
                    textFields = textFields,
                    coverSectionTitle = coverSectionTitle,
                    extraFormTopContent = extraFormTopContent,
                    extraFormBottomContent = extraFormBottomContent,
                )
            }
        }
    }
}