package com.github.enteraname74.soulsearching.feature.mainpage.presentation.tab

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.github.enteraname74.domain.model.*
import com.github.enteraname74.soulsearching.composables.BigPreviewComposable
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.ElementEnum
import com.github.enteraname74.soulsearching.feature.mainpage.domain.model.PagerScreen
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.AllQuickAccessState
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.MainPageViewModel
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.composable.MainPageList
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
fun allQuickAccessTab(
    mainPageViewModel: MainPageViewModel,
    onClick: (quickAccessible: QuickAccessible) -> Unit,
    showQuickAccessBottomSheet: (quickAccessible: QuickAccessible) -> Unit,
): PagerScreen = PagerScreen(
    type = ElementEnum.QUICK_ACCESS,
    screen = {
        val quickAccessState: AllQuickAccessState by mainPageViewModel.allQuickAccessState.collectAsState()

        MainPageList(
            list = quickAccessState.allQuickAccess,
            title = strings.quickAccess,
            isUsingSort = false,
            key = null,
            contentType = { ALL_QUICK_ACCESS_CONTENT_TYPE }
        ) { element ->
            element.toPreview(
                modifier = Modifier
                    .animateItem(),
                onClick = onClick,
                onLongClick = showQuickAccessBottomSheet,
            )
        }
    }
)

@Composable
private fun QuickAccessible.toPreview(
    modifier: Modifier,
    onClick: (quickAccessible: QuickAccessible) -> Unit,
    onLongClick: (quickAccessible: QuickAccessible) -> Unit,
    playerViewManager: PlayerViewManager = injectElement(),
    playbackManager: PlaybackManager = injectElement(),
) {

    val coroutineScope = rememberCoroutineScope()

    when(this) {
        is AlbumWithArtist -> {
            BigPreviewComposable(
                modifier = modifier,
                cover = this.cover,
                title = this.album.albumName,
                text = this.artist?.artistName.orEmpty(),
                onClick = { onClick(this) },
                onLongClick = { onLongClick(this) }
            )
        }
        is ArtistWithMusics -> {
            BigPreviewComposable(
                modifier = modifier,
                cover = this.cover,
                title = this.artist.artistName,
                text = strings.musics(total = this.musics.size),
                onClick = { onClick(this) },
                onLongClick = { onLongClick(this) }
            )
        }
        is Music -> {
            BigPreviewComposable(
                modifier = modifier,
                cover = this.cover,
                title = this.name,
                text = this.album,
                onClick = {
                    coroutineScope.launch {
                        val musicListSingleton = arrayListOf(this@toPreview)
                        CoroutineScope(Dispatchers.IO).launch {
                            playbackManager.setCurrentPlaylistAndMusic(
                                music = this@toPreview,
                                musicList = musicListSingleton,
                                isMainPlaylist = false,
                                playlistId = null,
                                isForcingNewPlaylist = true
                            )
                        }
                        playerViewManager.animateTo(
                            newState = BottomSheetStates.EXPANDED,
                        )
                    }
                },
                onLongClick = { onLongClick(this) }
            )
        }
        is PlaylistWithMusicsNumber -> {
            BigPreviewComposable(
                modifier = modifier,
                cover = this.playlist.cover,
                title = this.playlist.name,
                text = strings.musics(total = this.musicsNumber),
                onClick = { onClick(this) },
                onLongClick = { onLongClick(this) }
            )
        }
    }
}

private const val ALL_QUICK_ACCESS_CONTENT_TYPE = "ALL_QUICK_ACCESS_CONTENT_TYPE"