package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.enteraname74.domain.model.CoverFolderRetriever
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun CoverFolderRetrieverExamplePath(
    coverFolderRetriever: CoverFolderRetriever,
    modifier: Modifier = Modifier,
) {
    val path = coverFolderRetriever.buildDynamicCoverPath(
        dynamicName = EXAMPLE_ARTIST_NAME,
    )
    AnimatedVisibility(
        visible = path != null,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = SoulSearchingColorTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(size = RoundedCorners),
                )
                .padding(all = UiConstants.Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
        ) {
            Text(
                text = strings.artistCoverMethodExampleTitle(artist = EXAMPLE_ARTIST_NAME),
                color = SoulSearchingColorTheme.colorScheme.onSecondary,
                style = UiConstants.Typography.bodyTitle,
            )
            Text(
                text = path.orEmpty(),
                color = SoulSearchingColorTheme.colorScheme.onSecondary,
                style = UiConstants.Typography.body,
            )
        }
    }
}

private const val EXAMPLE_ARTIST_NAME = "John Doe"
private val RoundedCorners = 10.dp