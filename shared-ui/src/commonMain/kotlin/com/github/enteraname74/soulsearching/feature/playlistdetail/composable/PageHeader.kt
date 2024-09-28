package com.github.enteraname74.soulsearching.feature.playlistdetail.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.github.enteraname74.soulsearching.composables.SoulImage
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistDetail
import com.github.enteraname74.soulsearching.feature.playlistdetail.domain.PlaylistVIewUiUtils


@Composable
fun PageHeader(
    playlistDetail: PlaylistDetail,
    onSubTitleClicked: () -> Unit = {},
) {
    if (!PlaylistVIewUiUtils.canShowVerticalMainInformation()) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(UiConstants.Spacing.large),
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
        ) {
            SoulImage(
                coverId = playlistDetail.coverId,
                size = UiConstants.ImageSize.huge,
                roundedPercent = 5
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = UiConstants.Spacing.large,
                ),
            verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SoulImage(
                coverId = playlistDetail.coverId,
                size = UiConstants.ImageSize.veryHuge,
                roundedPercent = 5
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