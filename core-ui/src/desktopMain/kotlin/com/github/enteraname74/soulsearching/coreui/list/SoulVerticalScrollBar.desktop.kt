package com.github.enteraname74.soulsearching.coreui.list

import androidx.compose.foundation.ScrollbarAdapter
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
actual fun SoulVerticalScrollBar(
    lazyListState: LazyListState,
    modifier: Modifier
) {
    VerticalScrollbar(
        modifier = modifier,
        adapter = ScrollbarAdapter(scrollState = lazyListState),
        style = defaultScrollbarStyle().copy(
            unhoverColor = SoulSearchingColorTheme.colorScheme.subPrimaryText,
            hoverColor = SoulSearchingColorTheme.colorScheme.onPrimary,
        )
    )
}