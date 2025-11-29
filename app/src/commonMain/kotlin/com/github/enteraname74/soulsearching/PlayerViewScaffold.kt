package com.github.enteraname74.soulsearching

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.presentation.ModifyMusicDestination
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerViewModel
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.player.presentation.PlayerDraggableView
import com.github.enteraname74.soulsearching.feature.playlistdetail.albumpage.presentation.SelectedAlbumDestination
import com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.presentation.SelectedArtistDestination
import com.github.enteraname74.soulsearching.feature.settings.advanced.SettingsAdvancedDestination
import com.github.enteraname74.soulsearching.feature.settings.advanced.SettingsAdvancedScreenFocusedElement
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayerViewScaffold(
    playerViewModel: PlayerViewModel,
    navigator: Navigator,
    playerViewManager: PlayerViewManager = injectElement(),
    content: @Composable () -> Unit,
) {
    LaunchedEffect(playerViewManager.currentValue) {
        playerViewManager.updateState(newState = playerViewManager.currentValue)
    }

    LaunchedEffect(playerViewManager.playerDraggableState.targetValue) {
        playerViewManager.updateTargetState(newState = playerViewManager.playerDraggableState.targetValue)
    }

    val nextState by playerViewManager.nextState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(nextState) {
        nextState?.let {
            coroutineScope.launch {
                playerViewManager.playerDraggableState.animateTo(
                    targetValue = it,
                    anim = tween(UiConstants.AnimationDuration.normal),
                )
                playerViewManager.consumeNextState()
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = SoulSearchingColorTheme.colorScheme.primary
            )
    ) {
        val constraintsScope = this
        val maxHeight = with(LocalDensity.current) {
            constraintsScope.maxHeight.toPx()
        }

        content()

        PlayerDraggableView(
            maxHeight = maxHeight,
            navigateToAlbum = { albumId ->
                navigator.navigate(
                    SelectedAlbumDestination(selectedAlbumId = albumId)
                )
            },
            navigateToArtist = { artistId ->
                navigator.navigate(
                    SelectedArtistDestination(selectedArtistId = artistId)
                )
            },
            navigateToModifyMusic = { musicId ->
                navigator.navigate(
                    ModifyMusicDestination(
                        selectedMusicId = musicId
                    )
                )
            },
            navigateToRemoteLyricsSettings = {
                navigator.navigate(
                    SettingsAdvancedDestination(
                        focusedElement = SettingsAdvancedScreenFocusedElement.LyricsPermission,
                    )
                )
            },
            playerViewModel = playerViewModel,
        )
    }
}