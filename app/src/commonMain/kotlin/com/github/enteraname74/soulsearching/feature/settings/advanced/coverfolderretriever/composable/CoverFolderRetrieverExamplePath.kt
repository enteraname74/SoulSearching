package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun CoverFolderRetrieverExamplePath(
    path: String?,
) {

    AnimatedContent(
        targetState = path != null,
    ) { isPathCorrect ->
        if (isPathCorrect) {
            Data(path = path.orEmpty())
        } else {
            Error()
        }
    }
}

@Composable
private fun Data(
    path: String,
) {
    Column(
        modifier = Modifier
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
            text = path,
            color = SoulSearchingColorTheme.colorScheme.onSecondary,
            style = UiConstants.Typography.body,
        )
    }
}

@Composable
private fun Error() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
    ) {
        SoulIcon(
            icon = Icons.Rounded.Warning,
            size = UiConstants.ImageSize.mediumPlus,
        )
        Text(
            text = strings.coverFolderRetrieverFolderIncomplete,
            style = UiConstants.Typography.body,
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
        )
    }
}

private val RoundedCorners = 10.dp