package com.github.enteraname74.soulsearching.feature.player.presentation.composable

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.clickableIf
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.getStatusBarPadding
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerMusicListViewManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.player.domain.state.PlayerViewState
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlayerTopInformation(
    modifier: Modifier = Modifier,
    alphaTransition: Float,
    onTopInformationHeightChange: (Int) -> Unit,
    state: PlayerViewState.Data,
    onShowPanel: (() -> Unit)?,
    onArtistClicked: (selectedArtist: Artist) -> Unit,
    onAlbumClicked: () -> Unit,
    onSongInfoClicked: () -> Unit,
    playerViewManager: PlayerViewManager = injectElement(),
    playerMusicListViewManager: PlayerMusicListViewManager = injectElement(),
) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .alpha(alphaTransition),
        verticalAlignment = Alignment.CenterVertically
    ) {

        val statusBarPadding: Int = getStatusBarPadding()

        SoulIcon(
            icon = Icons.Rounded.KeyboardArrowDown,
            modifier = Modifier
                .clickableIf(enabled = playerViewManager.currentValue == BottomSheetStates.EXPANDED) {
                    coroutineScope.launch {
                        if (playerMusicListViewManager.currentValue != BottomSheetStates.COLLAPSED) {
                            playerMusicListViewManager.animateTo(
                                newState = BottomSheetStates.COLLAPSED,
                            )
                        }
                        playerViewManager.animateTo(
                            newState = BottomSheetStates.MINIMISED,
                        )
                    }
                },
            size = UiConstants.ImageSize.medium,
            tint = SoulSearchingColorTheme.colorScheme.onPrimary,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .onGloballyPositioned { layoutCoordinates ->
                    onTopInformationHeightChange(
                        layoutCoordinates.size.height + statusBarPadding,
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = state.currentMusic.name,
                color = SoulSearchingColorTheme.colorScheme.onPrimary,
                maxLines = 1,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .basicMarquee()
            )

            FlowRow(
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.Center,
            ) {
                state.artistsOfCurrentMusic.forEachIndexed { index, artist ->

                    val formattedText = if (index == state.artistsOfCurrentMusic.lastIndex) {
                        artist.artistName
                    } else {
                        "${artist.artistName}, "
                    }

                    Text(
                        text = formattedText,
                        color = SoulSearchingColorTheme.colorScheme.subPrimaryText,
                        fontSize = 15.sp,
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clickableIf(enabled = playerViewManager.currentValue == BottomSheetStates.EXPANDED) {
                                onArtistClicked(artist)
                            },
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Text(
                text = state.currentMusic.album,
                color = SoulSearchingColorTheme.colorScheme.subPrimaryText,
                fontSize = 15.sp,
                maxLines = 1,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickableIf(enabled = playerViewManager.currentValue == BottomSheetStates.EXPANDED) {
                    onAlbumClicked()
                },
                overflow = TextOverflow.Ellipsis
            )
        }
        if (onShowPanel != null) {
            SoulIcon(
                icon = Icons.Rounded.Menu,
                modifier = Modifier
                    .clickableIf(enabled = playerViewManager.currentValue == BottomSheetStates.EXPANDED) {
                        onShowPanel()
                    },
                size = UiConstants.ImageSize.medium,
                tint = SoulSearchingColorTheme.colorScheme.onPrimary,
            )
        } else {
            SoulIcon(
                icon = Icons.Rounded.MoreVert,
                modifier = Modifier
                    .clickableIf(enabled = playerViewManager.currentValue == BottomSheetStates.EXPANDED) {
                        onSongInfoClicked()
                    },
                size = UiConstants.ImageSize.medium,
                tint = SoulSearchingColorTheme.colorScheme.onPrimary,
            )
        }
    }
}