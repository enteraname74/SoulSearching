package com.github.enteraname74.soulsearching.feature.playlistdetail.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.enteraname74.soulsearching.composables.SoulImage
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetail
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistViewUiUtils


@Composable
fun PageHeader(
    playlistDetail: PlaylistDetail,
    onCoverLoaded: (cover: ImageBitmap?) -> Unit,
    modifier: Modifier = Modifier,
    onSubTitleClicked: () -> Unit = {},
) {
    if (!PlaylistViewUiUtils.canShowVerticalMainInformation()) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(UiConstants.Spacing.large),
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
        ) {
            SoulImage(
                contentScale = ContentScale.Fit,
                cover = playlistDetail.cover,
                size = UiConstants.ImageSize.huge,
                roundedPercent = 5,
                onSuccess = onCoverLoaded,
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small)
            ) {
                Text(
                    color = SoulSearchingColorTheme.colorScheme.onPrimary,
                    text = playlistDetail.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp
                )
                playlistDetail.subTitle?.let {
                    Text(
                        modifier = Modifier.clickableWithHandCursor {
                            onSubTitleClicked()
                        },
                        color = SoulSearchingColorTheme.colorScheme.subPrimaryText,
                        text = it,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                }
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
                contentScale = ContentScale.Fit,
                cover = playlistDetail.cover,
                size = if (windowSize == WindowSize.Large) {
                    300.dp
                } else {
                    225.dp
                },
                roundedPercent = 5,
                onSuccess = onCoverLoaded,
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    color = SoulSearchingColorTheme.colorScheme.onPrimary,
                    text = playlistDetail.title,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp
                )
                playlistDetail.subTitle?.let {
                    Text(
                        modifier = Modifier.clickableWithHandCursor {
                            onSubTitleClicked()
                        },
                        textAlign = TextAlign.Center,
                        color = SoulSearchingColorTheme.colorScheme.subPrimaryText,
                        text = it,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}