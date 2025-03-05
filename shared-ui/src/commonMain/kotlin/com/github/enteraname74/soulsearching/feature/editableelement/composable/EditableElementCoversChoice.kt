package com.github.enteraname74.soulsearching.feature.editableelement.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.github.enteraname74.soulsearching.composables.SoulByteArrayImage
import com.github.enteraname74.soulsearching.coreui.SoulCircularProgressIndicator
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.editableelement.domain.CoverListState


fun LazyGridScope.editableElementCoversChoice(
    coverState: CoverListState,
    onCoverSelected: (ByteArray) -> Unit,
    sectionTitle: String?,
) {
    sectionTitle?.let { title ->
        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            Text(
                text = title,
                maxLines = 2,
                color = SoulSearchingColorTheme.colorScheme.onSecondary,
                style = UiConstants.Typography.bodyMediumTitle,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }

    when (coverState) {
        CoverListState.Loading -> {
            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    SoulCircularProgressIndicator()
                }
            }
        }

        is CoverListState.Data -> {
            if (coverState.covers.isEmpty()) {
                item(
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = UiConstants.Spacing.medium,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = strings.noAvailableCovers,
                            color = SoulSearchingColorTheme.colorScheme.onSecondary,
                            style = UiConstants.Typography.body,
                        )
                    }
                }
            } else {
                items(
                    items = coverState.covers,
                ) {
                    CoverImage(
                        cover = it,
                        onClick = { onCoverSelected(it) },
                    )
                }
            }
        }
    }
}

@Composable
private fun CoverImage(
    cover: ByteArray,
    onClick: () -> Unit,
) {
    SoulByteArrayImage(
        data = cover,
        size = null,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickableWithHandCursor { onClick() }
    )
}