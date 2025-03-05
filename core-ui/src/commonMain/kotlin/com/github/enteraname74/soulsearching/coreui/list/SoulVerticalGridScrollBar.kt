package com.github.enteraname74.soulsearching.coreui.list

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun SoulVerticalGridScrollBar(
    lazyGridState: LazyGridState,
    modifier: Modifier = Modifier,
)