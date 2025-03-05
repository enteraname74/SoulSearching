package com.github.enteraname74.soulsearching.coreui.list

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.ScrollbarAdapter
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
actual fun SoulHorizontalScrollBar(
    lazyListState: LazyListState,
    modifier: Modifier,
) {
    Column {
        Spacer(
            modifier = Modifier
                .height(SPACER_HEIGHT)
        )
        HorizontalScrollbar(
            modifier = modifier,
            adapter = ScrollbarAdapter(scrollState = lazyListState),
            style = defaultScrollbarStyle().copy(
                unhoverColor = SoulSearchingColorTheme.colorScheme.subPrimaryText,
                hoverColor = SoulSearchingColorTheme.colorScheme.onPrimary,
            )
        )
    }
}

private val SPACER_HEIGHT: Dp = 16.dp
