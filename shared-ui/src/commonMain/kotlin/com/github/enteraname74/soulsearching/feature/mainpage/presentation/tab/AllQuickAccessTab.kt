package com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.Playlist
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.coversprovider.AllImageCoversViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.QuickAccessState
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.AllElementsComposable
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import kotlinx.coroutines.launch

@Composable
fun allQuickAccessTab(
    allImageCoversViewModel: AllImageCoversViewModel,
    quickAccessState: QuickAccessState,
    navigateToPlaylist: (playlistId: String) -> Unit,
    navigateToArtist: (artistId: String) -> Unit,
    navigateToAlbum: (albumId: String) -> Unit,
    artistBottomSheetAction: (ArtistWithMusics) -> Unit,
    playlistBottomSheetAction: (Playlist) -> Unit,
    albumBottomSheetAction: (Album) -> Unit,
    musicBottomSheetAction: (Music) -> Unit,
    playerViewManager: PlayerViewManager = injectElement(),
    playbackManager: PlaybackManager = injectElement(),
): PagerScreen = PagerScreen(
    title = strings.quickAccess,
    screen = {

        val coroutineScope = rememberCoroutineScope()

        AllElementsComposable(
            retrieveCoverMethod = allImageCoversViewModel::getImageCover,
            list = quickAccessState.allQuickAccess,
            title = strings.quickAccess,
            isUsingSort = false,
            artistBottomSheetAction = artistBottomSheetAction,
            playlistBottomSheetAction = playlistBottomSheetAction,
            albumBottomSheetAction = albumBottomSheetAction,
            navigateToArtist = navigateToArtist,
            navigateToPlaylist = navigateToPlaylist,
            navigateToAlbum = navigateToAlbum,
            playMusicAction = { music ->
                coroutineScope.launch {
                    playerViewManager.animateTo(
                        newState = BottomSheetStates.EXPANDED,
                    )
                }.invokeOnCompletion {
                    val musicListSingleton = arrayListOf(music)
                    playbackManager.setCurrentPlaylistAndMusic(
                        music = music,
                        musicList = musicListSingleton,
                        isMainPlaylist = false,
                        playlistId = null,
                        isForcingNewPlaylist = true
                    )
                }
            },
            musicBottomSheetAction = musicBottomSheetAction,
        )
    }
)