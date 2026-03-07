package com.github.enteraname74.soulsearching

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.main.MusicBottomSheetDestination
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.presentation.ModifyMusicDestination
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerViewModel
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.SwipeableViewManagerHandler
import com.github.enteraname74.soulsearching.feature.player.presentation.PlayerDraggableView
import com.github.enteraname74.soulsearching.feature.playlistdetail.albumpage.presentation.SelectedAlbumDestination
import com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.presentation.SelectedArtistDestination
import com.github.enteraname74.soulsearching.feature.settings.advanced.SettingsAdvancedDestination
import com.github.enteraname74.soulsearching.feature.settings.advanced.SettingsAdvancedScreenFocusedElement
import com.github.enteraname74.soulsearching.navigation.Navigator
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayerViewScaffold(
    navigator: Navigator,
    playerViewModel: PlayerViewModel = koinViewModel(),
    playerViewManager: PlayerViewManager = injectElement(),
    content: @Composable () -> Unit,
) {
    SwipeableViewManagerHandler(
        swipeableViewManager = playerViewManager,
    )

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
                navigator.push(
                    SelectedAlbumDestination(selectedAlbumId = albumId)
                )
            },
            navigateToArtist = { artistId ->
                navigator.push(
                    SelectedArtistDestination(selectedArtistId = artistId)
                )
            },
            navigateToModifyMusic = { musicId ->
                navigator.push(
                    ModifyMusicDestination(
                        selectedMusicId = musicId
                    )
                )
            },
            navigateToRemoteLyricsSettings = {
                navigator.push(
                    SettingsAdvancedDestination(
                        focusedElement = SettingsAdvancedScreenFocusedElement.LyricsPermission,
                    )
                )
            },
            showMusicBottomSheet = {
                navigator.push(MusicBottomSheetDestination(it))
            },
            playerViewModel = playerViewModel,
        )
    }
}