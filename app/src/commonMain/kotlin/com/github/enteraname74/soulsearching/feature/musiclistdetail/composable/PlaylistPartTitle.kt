package com.github.enteraname74.soulsearching.feature.musiclistdetail.composable

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec

@Composable
fun PlaylistPartTitle(
    title: SoulTextSpec
) {
    val windowSize = rememberWindowSize()
    val canShowColumnLayout = MusicListDetailUiUtils.canShowColumnLayout() && windowSize != WindowSize.Small
    SoulText(
        modifier = Modifier
            .padding(
                start = if (canShowColumnLayout) {
                    UiConstants.Spacing.huge
                } else {
                    UiConstants.Spacing.medium
                }
            ),
        text = title,
        color = SoulSearchingColorTheme.colorScheme.text,
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

