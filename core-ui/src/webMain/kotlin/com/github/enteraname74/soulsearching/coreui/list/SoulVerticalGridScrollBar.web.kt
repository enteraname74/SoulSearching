package com.github.enteraname74.soulsearching.coreui.list

import androidx.compose.foundation.ScrollbarAdapter
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize

@Composable
actual fun SoulVerticalGridScrollBar(
    lazyGridState: LazyGridState,
    modifier: Modifier
) {
    val windowSize = rememberWindowSize()
    if (windowSize == WindowSize.Small) return

    VerticalScrollbar(
        modifier = modifier,
        adapter = ScrollbarAdapter(scrollState = lazyGridState),
        style = defaultScrollbarStyle().copy(
            unhoverColor = SoulSearchingColorTheme.colorScheme.subPrimaryText,
            hoverColor = SoulSearchingColorTheme.colorScheme.onPrimary,
        )
    )
}
