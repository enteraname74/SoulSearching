package com.github.enteraname74.soulsearching.feature.editableelement.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.composables.SoulByteArrayImage
import com.github.enteraname74.soulsearching.composables.SoulImage
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.editableelement.domain.EditableElement

@Composable
fun EditableElementCoverSection(
    title: String,
    editableElement: EditableElement,
    onSelectImage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(bottom = UiConstants.Spacing.medium),
            text = title,
            color = SoulSearchingColorTheme.colorScheme.onSecondary
        )

        if (editableElement.newCover == null) {
            SoulImage(
                cover = editableElement.initialCover,
                size = UiConstants.CoverSize.huge,
                modifier = Modifier.clickableWithHandCursor { onSelectImage() }
            )
        } else {
            SoulByteArrayImage(
                data = editableElement.newCover,
                size = UiConstants.CoverSize.huge,
                modifier = Modifier.clickableWithHandCursor { onSelectImage() }
            )
        }
    }
}