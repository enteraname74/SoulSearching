package com.github.enteraname74.soulsearching.feature.editableelement.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.textfield.SoulTextFieldHolder
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarValidateAction
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.feature.editableelement.domain.EditableElement
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile

@Composable
fun EditableElementView(
    title: String,
    coverSectionTitle: String,
    editableElement: EditableElement,
    navigateBack: () -> Unit,
    onNewImageSet: (imageFile: PlatformFile) -> Unit,
    onValidateModification: () -> Unit,
    textFields: List<SoulTextFieldHolder>,
    textFieldsLabels: List<String>,
) {
    val focusManager = LocalFocusManager.current
    val windowSize = rememberWindowSize()

    val imagePickerLauncher = rememberFilePickerLauncher(
        type = PickerType.Image,
    ) { file ->
        if (file == null) return@rememberFilePickerLauncher
        onNewImageSet(file)
    }

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
                    onSelectImage = { imagePickerLauncher.launch() },
                    focusManager = focusManager,
                    textFields = textFields,
                    coverSectionTitle = coverSectionTitle,
                    textFieldsLabels = textFieldsLabels,
                )

                else -> EditableElementRowView(
                    editableElement = editableElement,
                    onSelectImage = { imagePickerLauncher.launch() },
                    focusManager = focusManager,
                    textFields = textFields,
                    coverSectionTitle = coverSectionTitle,
                    textFieldsLabels = textFieldsLabels,
                )
            }
        }
    }
}