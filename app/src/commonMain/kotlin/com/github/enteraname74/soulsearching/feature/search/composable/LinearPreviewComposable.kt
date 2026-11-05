package com.github.enteraname74.soulsearching.feature.search.composable

import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.github.enteraname74.soulsearching.domain.model.Cover
import com.github.enteraname74.soulsearching.composables.image.SoulImage
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_more_vertical
import com.github.enteraname74.soulsearching.coreui.ext.optionalClickable
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun LinearPreviewComposable(
    title: SoulTextSpec,
    text: SoulTextSpec,
    cover: Cover?,
    onLongClick: (() -> Unit)?,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(UiConstants.Spacing.medium)
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .optionalClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(padding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SoulImage(
                cover = cover,
                size = UiConstants.CoverSize.small
            )
            Column(
                modifier = Modifier
                    .height(UiConstants.CoverSize.small)
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                SoulText(
                    text = title,
                    color = SoulSearchingColorTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                SoulText(
                    text = text,
                    color = SoulSearchingColorTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

            }
            onLongClick?.let {
                SoulIcon(
                    modifier = Modifier.optionalClickable(onClick = it),
                    icon = CoreRes.drawable.ic_more_vertical,
                    contentDescription = CoreRes.string.more_button.toTextSpec().string,
                )
            }
        }
    }
}
