package com.github.enteraname74.soulsearching.feature.editableelement.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.image.SoulBitmapImage
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.coversprovider.ImageCoverRetriever
import com.github.enteraname74.soulsearching.feature.editableelement.domain.EditableElement

@Composable
fun EditableElementCoverSection(
    title: String,
    editableElement: EditableElement,
    onSelectImage: () -> Unit,
    modifier: Modifier = Modifier,
    imageCoverRetriever: ImageCoverRetriever = injectElement(),
) {

    val legacyCover: ImageBitmap? by imageCoverRetriever.getImageBitmap(
        coverId = editableElement.initialCoverId,
    ).collectAsState(null)

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
        SoulBitmapImage(
            bitmap = editableElement.newCover ?: legacyCover,
            size = UiConstants.CoverSize.huge,
            modifier = Modifier.clickableWithHandCursor { onSelectImage() }
        )
    }
}