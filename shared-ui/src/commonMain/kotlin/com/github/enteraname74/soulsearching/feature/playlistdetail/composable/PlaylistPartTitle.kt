package com.github.enteraname74.soulsearching.feature.playlistdetail.composable

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistViewUiUtils

@Composable
fun PlaylistPartTitle(
    title: String
) {
    val canShowColumnLayout = PlaylistViewUiUtils.canShowColumnLayout()
    Text(
        modifier = Modifier
            .padding(
                start = if (canShowColumnLayout) {
                    UiConstants.Spacing.huge
                } else {
                    UiConstants.Spacing.medium
                }
            ),
        text = title,
        color = SoulSearchingColorTheme.colorScheme.onPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    )
    if (canShowColumnLayout) {
        Spacer(
            modifier = Modifier
                .height(SPACER_HEIGHT)
        )
    }
}

private val SPACER_HEIGHT: Dp = 16.dp

