package com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.list.LazyRowCompat
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.ext.navigationTitle
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
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.mediumPlus),
    ) {
        items(
            items = visibleElements,
            key = { it.name },
            contentType = { HORIZONTAL_SHORTCUT_BUTTONS_CONTENT_TYPE }
        ) {visibleElement ->
            val pos = visibleElements.indexOf(visibleElement)
            val isCurrentPosition = pos == currentPage ||
                    ((pos == visibleElements.size - 1) && (currentPage >= pos))

            Text(
                modifier = Modifier
                    .clickableWithHandCursor {
                        switchPageAction(pos)
                    },
                text = visibleElement.navigationTitle(),
                color = if (isCurrentPosition) {
                    SoulSearchingColorTheme.colorScheme.onPrimary
                } else {
                    SoulSearchingColorTheme.colorScheme.subPrimaryText
                },
                fontWeight = if (isCurrentPosition) {
                    FontWeight.ExtraBold
                } else {
                    FontWeight.Normal
                }
            )
        }
    }
}

private const val HORIZONTAL_SHORTCUT_BUTTONS_CONTENT_TYPE: String = "HORIZONTAL_SHORTCUT_BUTTONS_CONTENT_TYPE"