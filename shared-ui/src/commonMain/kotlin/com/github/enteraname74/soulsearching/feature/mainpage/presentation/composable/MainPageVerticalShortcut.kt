package com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.ext.navigationFilledIcon
import com.github.enteraname74.soulsearching.ext.navigationTitle
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum

@Composable
fun MainPageVerticalShortcut(
    currentPage: Int,
    switchPageAction: (Int) -> Unit,
    visibleElements: List<ElementEnum>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        items(
            items = visibleElements,
            key = { it.name },
            contentType = { VERTICAL_SHORTCUT_TABS_CONTENT_TYPE },
        ) {
            val pos = visibleElements.indexOf(it)
            val isCurrentPosition = pos == currentPage ||
                    ((pos == visibleElements.size - 1) && (currentPage >= pos))

            Row(
                modifier = Modifier
                    .padding(
                        top = UiConstants.Spacing.large,
                        bottom = UiConstants.Spacing.large,
                        start = UiConstants.Spacing.medium,
                        end = UiConstants.Spacing.large
                    )
                    .clickableWithHandCursor {
                        switchPageAction(pos)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .size(UiConstants.ImageSize.medium)
                        .alpha(
                            if (!isCurrentPosition) {
                                0F
                            } else {
                                1F
                            }
                        ),
                    colorFilter = ColorFilter.tint(SoulSearchingColorTheme.colorScheme.onPrimary),
                    imageVector = it.navigationFilledIcon(),
                    contentDescription = ""
                )
                Text(
                    modifier = Modifier
                        .rotate(-90f)
                        .vertical(),
                    text = it.navigationTitle(),
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
        item(
            key = VERTICAL_SHORTCUT_SPACER_KEY,
            contentType = VERTICAL_SHORTCUT_SPACER_CONTENT_TYPE,
        ) {
            SoulPlayerSpacer()
        }
    }
}

fun Modifier.vertical() =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.height, placeable.width) {
            placeable.place(
                x = -(placeable.width / 2 - placeable.height / 2),
                y = -(placeable.height / 2 - placeable.width / 2)
            )
        }
    }

private const val VERTICAL_SHORTCUT_TABS_CONTENT_TYPE: String = "VERTICAL_SHORTCUT_TABS_CONTENT_TYPE"
private const val VERTICAL_SHORTCUT_SPACER_KEY: String = "VERTICAL_SHORTCUT_SPACER_KEY"
private const val VERTICAL_SHORTCUT_SPACER_CONTENT_TYPE: String = "VERTICAL_SHORTCUT_SPACER_CONTENT_TYPE"
