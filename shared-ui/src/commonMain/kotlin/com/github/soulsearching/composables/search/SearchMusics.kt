package com.github.soulsearching.composables.search

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Music
import com.github.soulsearching.Constants
import com.github.soulsearching.composables.MusicItemComposable
import com.github.soulsearching.composables.PlayerSpacer
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.strings.strings
import com.github.soulsearching.theme.SoulSearchingColorTheme
import com.github.soulsearching.types.BottomSheetStates
import com.github.soulsearching.utils.PlayerUtils
import com.github.soulsearching.viewmodel.PlayerMusicListViewModel
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun SearchMusics(
    playerDraggableState: SwipeableState<BottomSheetStates>,
    searchText: String,
    musicState: MusicState,
    onMusicEvent: (MusicEvent) -> Unit,
    playerMusicListViewModel: PlayerMusicListViewModel,
    isMainPlaylist: Boolean,
    focusManager: FocusManager,
    retrieveCoverMethod: (UUID?) -> ImageBitmap?,
    primaryColor: Color = SoulSearchingColorTheme.colorScheme.primary,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary
) {
    val coroutineScope = rememberCoroutineScope()

    LazyColumn {
        val foundedMusics = musicState.musics.filter {
            it.name.lowercase().contains(searchText.lowercase())
                    || it.artist.lowercase().contains(searchText.lowercase())
                    || it.album.lowercase().contains(searchText.lowercase())

        }

        if (foundedMusics.isNotEmpty()) {
            stickyHeader {
                SearchType(
                    title = strings.musics,
                    primaryColor = primaryColor,
                    textColor = textColor
                )
            }
            items(foundedMusics) { music ->
                MusicItemComposable(
                    music = music,
                    onClick = { selectedMusic ->
                        coroutineScope.launch {
                            focusManager.clearFocus()
                            playerDraggableState.animateTo(
                                BottomSheetStates.EXPANDED,
                                tween(Constants.AnimationDuration.normal)
                            )
                        }.invokeOnCompletion { _ ->
                            PlayerUtils.playerViewModel.handler.setCurrentPlaylistAndMusic(
                                music = selectedMusic,
                                playlist = foundedMusics as ArrayList<Music>,
                                playlistId = null,
                                isMainPlaylist = isMainPlaylist,
                                isForcingNewPlaylist = true
                            )
                            playerMusicListViewModel.handler.savePlayerMusicList(PlayerUtils.playerViewModel.handler.currentPlaylist.map { it.musicId })
                        }
                    },
                    onLongClick = {
                        coroutineScope.launch {
                            onMusicEvent(
                                MusicEvent.SetSelectedMusic(
                                    music
                                )
                            )
                            onMusicEvent(
                                MusicEvent.BottomSheet(
                                    isShown = true
                                )
                            )
                        }
                    },
                    musicCover = retrieveCoverMethod(music.coverId),
                    textColor = textColor
                )
            }
        }
        item {
            PlayerSpacer()
        }
    }
}
