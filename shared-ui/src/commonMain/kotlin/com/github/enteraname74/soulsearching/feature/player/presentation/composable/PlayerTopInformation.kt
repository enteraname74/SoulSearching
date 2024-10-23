package com.github.enteraname74.soulsearching.feature.player.presentation.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.clickableIf
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.WindowSize
import com.github.enteraname74.soulsearching.coreui.utils.getStatusBarPadding
import com.github.enteraname74.soulsearching.coreui.utils.rememberWindowSize
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerViewState
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerMusicListViewManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerTopInformation(
    modifier: Modifier = Modifier,
    alphaTransition: Float,
    onTopInformationHeightChange: (Int) -> Unit,
    state: PlayerViewState.Data,
    onShowPanel: (() -> Unit)?,
    onArtistClicked: () -> Unit,
    onAlbumClicked: () -> Unit,
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

        Image(
            imageVector = Icons.Rounded.KeyboardArrowDown,
            contentDescription = "",
            modifier = Modifier
                .size(UiConstants.ImageSize.medium)
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
            colorFilter = ColorFilter.tint(SoulSearchingColorTheme.colorScheme.onPrimary),
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

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = state.currentMusic.artist.formatTextForEllipsis(),
                    color = SoulSearchingColorTheme.colorScheme.subPrimaryText,
                    fontSize = 15.sp,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clickableIf(enabled = playerViewManager.currentValue == BottomSheetStates.EXPANDED) {
                            onArtistClicked()
                        },
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = " | ",
                    color = SoulSearchingColorTheme.colorScheme.subPrimaryText,
                    fontSize = 15.sp,
                )
                Text(
                    text = state.currentMusic.album.formatTextForEllipsis(),
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
        }
        if (onShowPanel != null) {
            Image(
                imageVector = Icons.Rounded.Menu,
                contentDescription = "",
                modifier = Modifier
                    .size(UiConstants.ImageSize.medium)
                    .clickableWithHandCursor { onShowPanel() },
                colorFilter = ColorFilter.tint(SoulSearchingColorTheme.colorScheme.onPrimary),
            )
        } else {
            Spacer(
                modifier = Modifier.size(UiConstants.ImageSize.medium)
            )
        }
    }
}

@Composable
private fun String.formatTextForEllipsis(): String {
    val windowSize = rememberWindowSize()
    if (windowSize != WindowSize.Small) {
        return this
    }
    return if (this.length > 16) {
        "${this.subSequence(0, 16)}…"
    } else {
        this
    }
}