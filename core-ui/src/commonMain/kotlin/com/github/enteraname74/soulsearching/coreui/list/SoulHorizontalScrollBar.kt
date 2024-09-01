package com.github.enteraname74.soulsearching.coreui.list

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun SoulHorizontalScrollBar(
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
)