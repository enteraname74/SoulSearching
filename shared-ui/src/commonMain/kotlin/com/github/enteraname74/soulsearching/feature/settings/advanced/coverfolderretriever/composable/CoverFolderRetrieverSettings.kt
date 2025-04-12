package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable

import androidx.compose.foundation.background
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
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.CoverFolderRetrieverActions

@Composable
fun CoverFolderRetrieverSettings(
    actions: CoverFolderRetrieverActions,
    coverFolderRetriever: CoverFolderRetriever,
) {
    Column {
        PathResultExample(
            path = coverFolderRetriever.buildDynamicCoverPath(
                dynamicName = EXAMPLE_ARTIST_NAME,
            )
        )
    }
}

@Composable
private fun PathResultExample(
    path: String,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = UiConstants.Spacing.medium)
            .background(
                color = SoulSearchingColorTheme.colorScheme.secondary,
                shape = RoundedCornerShape(size = 10.dp),
            )
    ) {
        Text(
            text = path,
            color = SoulSearchingColorTheme.colorScheme.onSecondary,
            style = UiConstants.Typography.bodyLarge,
        )
    }
}

private const val EXAMPLE_ARTIST_NAME = "John Doe"