package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

@Composable
fun CoverFolderRetrieverIncomplete(
    isComplete: Boolean,
) {
    AnimatedVisibility(
        visible = isComplete,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
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
}