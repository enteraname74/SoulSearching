package com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.composable.SoulChip
import com.github.enteraname74.soulsearching.coreui.list.LazyRowCompat
import com.github.enteraname74.soulsearching.ext.text
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum

@Composable
fun MainPageHorizontalShortcut(
    currentPage: Int,
    switchPageAction: (Int) -> Unit,
    visibleElements: List<ElementEnum>
) {
    LazyRowCompat(
        modifier = Modifier
            .fillMaxWidth(),
        contentPadding = PaddingValues(
            horizontal = UiConstants.Spacing.medium,
            vertical = UiConstants.Spacing.mediumPlus,
        ),
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
    ) {
        items(
            items = visibleElements,
            key = { it.name },
            contentType = { HORIZONTAL_SHORTCUT_BUTTONS_CONTENT_TYPE }
        ) { visibleElement ->
            val pos = visibleElements.indexOf(visibleElement)
            val isCurrentPosition = pos == currentPage ||
                ((pos == visibleElements.size - 1) && (currentPage >= pos))

            SoulChip(
                title = visibleElement.text(),
                isSelected = isCurrentPosition,
                onClick = { switchPageAction(pos) },
            )
        }
    }
}

private const val HORIZONTAL_SHORTCUT_BUTTONS_CONTENT_TYPE: String = "HORIZONTAL_SHORTCUT_BUTTONS_CONTENT_TYPE"
