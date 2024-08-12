package com.github.enteraname74.soulsearching

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import cafe.adriel.voyager.navigator.Navigator
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.coversprovider.AllImageCoversViewModel
import com.github.enteraname74.soulsearching.feature.elementpage.albumpage.presentation.SelectedAlbumScreen
import com.github.enteraname74.soulsearching.feature.elementpage.artistpage.presentation.SelectedArtistScreen
import com.github.enteraname74.soulsearching.feature.modifyelement.modifymusic.presentation.ModifyMusicScreen
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerViewModel
import com.github.enteraname74.soulsearching.feature.player.presentation.PlayerDraggableView

@Composable
fun PlayerViewScaffold(
    allImageCoversViewModel: AllImageCoversViewModel = injectElement(),
    playerViewModel: PlayerViewModel = injectElement(),
    generalNavigator: Navigator?,
    content: @Composable () -> Unit,
) {
    val coversState by allImageCoversViewModel.state.collectAsState()

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
            retrieveCoverMethod = allImageCoversViewModel::getImageCover,
            navigateToAlbum = { albumId ->
                generalNavigator?.push(
                    SelectedAlbumScreen(selectedAlbumId = albumId)
                )
            },
            navigateToArtist = { artistId ->
                generalNavigator?.push(
                    SelectedArtistScreen(selectedArtistId = artistId)
                )
            },
            navigateToModifyMusic = { musicId ->
                generalNavigator?.push(
                    ModifyMusicScreen(
                        selectedMusicId = musicId
                    )
                )
            },
            playerViewModel = playerViewModel,
            coverList = coversState.covers
        )
    }
}