package com.github.enteraname74.soulsearching.feature.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.composables.MusicItemComposable
import com.github.enteraname74.soulsearching.coreui.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.search.composable.SearchType
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchMusics(
    searchText: String,
    allMusics: List<Music>,
    isMainPlaylist: Boolean,
    focusManager: FocusManager,
    onSelectedMusicForBottomSheet: (Music) -> Unit,
    primaryColor: Color = SoulSearchingColorTheme.colorScheme.primary,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary,
    playbackManager: PlaybackManager = injectElement(),
    playerViewManager: PlayerViewManager = injectElement(),
) {
    val coroutineScope = rememberCoroutineScope()
    val currentPlayedSong: Music? by playbackManager.currentSong.collectAsState()

    LazyColumnCompat {
        val foundedMusics = allMusics.filter {
            it.name.lowercase().contains(searchText.lowercase())
                    || it.artist.lowercase().contains(searchText.lowercase())
                    || it.album.lowercase().contains(searchText.lowercase())

        }

        if (foundedMusics.isNotEmpty()) {
            stickyHeader(
                key = SEARCH_MUSIC_STICKY_HEADER_KEY,
                contentType = SEARCH_MUSIC_STICKY_HEADER_CONTENT_TYPE,
            ) {
                SearchType(
                    modifier = Modifier
                        .animateItem(),
                    title = strings.musics,
                    primaryColor = primaryColor,
                    textColor = textColor
                )
            }
            items(
                key = { it.musicId },
                contentType = { SEARCH_MUSIC_CONTENT_TYPE },
                items = foundedMusics,
            ) { music ->
                MusicItemComposable(
                    modifier = Modifier
                        .animateItem(),
                    music = music,
                    onClick = { selectedMusic ->
                        coroutineScope.launch {
                            playbackManager.setCurrentPlaylistAndMusic(
                                music = selectedMusic,
                                musicList = foundedMusics as ArrayList<Music>,
                                playlistId = null,
                                isMainPlaylist = isMainPlaylist,
                                isForcingNewPlaylist = true
                            )
                            focusManager.clearFocus()
                            playerViewManager.animateTo(BottomSheetStates.EXPANDED)
                        }
                    },
                    onMoreClicked = {
                        coroutineScope.launch {
                            onSelectedMusicForBottomSheet(music)
                        }
                    },
                    textColor = textColor,
                    isPlayedMusic = currentPlayedSong?.musicId == music.musicId,
                )
            }
        }
        item(
            key = SEARCH_MUSIC_SPACER_KEY,
            contentType = SEARCH_MUSIC_SPACER_CONTENT_TYPE,
        ) {
            SoulPlayerSpacer()
        }
    }
}

private const val SEARCH_MUSIC_STICKY_HEADER_KEY: String = "SEARCH_MUSIC_STICKY_HEADER_KEY"
private const val SEARCH_MUSIC_STICKY_HEADER_CONTENT_TYPE: String = "SEARCH_MUSIC_STICKY_HEADER_CONTENT_TYPE"
private const val SEARCH_MUSIC_CONTENT_TYPE: String = "SEARCH_MUSIC_CONTENT_TYPE"
private const val SEARCH_MUSIC_SPACER_KEY: String = "SEARCH_MUSIC_SPACER_KEY"
private const val SEARCH_MUSIC_SPACER_CONTENT_TYPE: String = "SEARCH_MUSIC_SPACER_CONTENT_TYPE"
