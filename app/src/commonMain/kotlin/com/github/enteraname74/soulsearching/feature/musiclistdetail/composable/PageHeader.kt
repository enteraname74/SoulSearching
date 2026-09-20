package com.github.enteraname74.soulsearching.feature.musiclistdetail.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.enteraname74.soulsearching.composables.image.SoulImage
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.optionalClickable
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.feature.musiclistdetail.MusicListDetailState

@Composable
fun PageHeader(
    data: MusicListDetailState.Data,
    modifier: Modifier = Modifier,
) {
    if (!MusicListDetailUiUtils.canShowColumnLayout()) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(UiConstants.Spacing.large),
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
        ) {
            SoulImage(
                contentScale = ContentScale.Crop,
                cover = data.cover,
                size = UiConstants.ImageSize.huge,
                roundedPercent = 5,
                onSuccess = data.onCoverLoaded,
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small)
            ) {
                Text(
                    color = SoulSearchingColorTheme.colorScheme.onPrimary,
                    text = data.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp
                )
                Text(
                    modifier = Modifier.optionalClickable(data.onSubtitleClicked),
                    color = SoulSearchingColorTheme.colorScheme.subPrimaryText,
                    text = data.subTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp
                )
            }
        }
    } else {
        val windowSize = rememberWindowSize()

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(
                    horizontal = UiConstants.Spacing.large,
                ),
            verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SoulImage(
                contentScale = ContentScale.Crop,
                cover = data.cover,
                size = if (windowSize == WindowSize.Large) {
                    300.dp
                } else {
                    225.dp
                },
                roundedPercent = 5,
                onSuccess = data.onCoverLoaded,
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    color = SoulSearchingColorTheme.colorScheme.onPrimary,
                    text = data.title,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp
                )
                Text(
                    modifier = Modifier.optionalClickable(data.onSubtitleClicked),
                    textAlign = TextAlign.Center,
                    color = SoulSearchingColorTheme.colorScheme.subPrimaryText,
                    text = data.subTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
