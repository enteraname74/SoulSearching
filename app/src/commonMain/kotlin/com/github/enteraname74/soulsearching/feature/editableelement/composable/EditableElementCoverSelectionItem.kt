package com.github.enteraname74.soulsearching.feature.editableelement.composable

import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.domain.model.Cover
import com.github.enteraname74.soulsearching.composables.BigPreviewComposable
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec

@Composable
fun LazyGridItemScope.EditableElementCoverSelectionItem(
    cover: Cover,
    title: SoulTextSpec,
    onClick: () -> Unit
) {
    BigPreviewComposable(
        titleMaxLines = 2,
        modifier = Modifier
            .animateItem(),
        imageSize = null,
        cover = cover,
        title = title,
        onClick = onClick,
        contentColor = SoulSearchingColorTheme.colorScheme.onSecondary,
    )
}
