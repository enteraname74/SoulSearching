package com.github.enteraname74.soulsearching.coreui.list

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun LazyRowCompat(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    horizontalArrangement: Arrangement.Horizontal =
        if (!reverseLayout) Arrangement.Start else Arrangement.End,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    content: LazyListScope.() -> Unit
) {
    Column {
        LazyRow(
            modifier = modifier,
            state = state,
            contentPadding = contentPadding,
            horizontalArrangement = horizontalArrangement,
            verticalAlignment = verticalAlignment,
            flingBehavior = flingBehavior,
            reverseLayout = reverseLayout,
            userScrollEnabled = userScrollEnabled,
        ) {
            content()
        }
        SoulHorizontalScrollBar(
            modifier = Modifier
                .padding(
                    start = contentPadding.calculateStartPadding(LayoutDirection.Rtl),
                    end = contentPadding.calculateEndPadding(LayoutDirection.Rtl),
                ),
            lazyListState = state,
        )
    }
}