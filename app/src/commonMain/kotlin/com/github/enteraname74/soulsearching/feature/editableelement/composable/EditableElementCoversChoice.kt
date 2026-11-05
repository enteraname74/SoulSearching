package com.github.enteraname74.soulsearching.feature.editableelement.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.github.enteraname74.soulsearching.composables.image.SoulByteArrayImage
import com.github.enteraname74.soulsearching.coreui.composable.SoulCircularProgressIndicator
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.editableelement.domain.CoverListState
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec


fun LazyGridScope.editableElementCoversChoice(
    coverState: CoverListState,
    onCoverSelected: (ByteArray) -> Unit,
    sectionTitle: SoulTextSpec?,
) {
    sectionTitle?.let { title ->
        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            SoulText(
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
                        SoulText(
                            text = CoreRes.string.no_available_covers.toTextSpec(),
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
