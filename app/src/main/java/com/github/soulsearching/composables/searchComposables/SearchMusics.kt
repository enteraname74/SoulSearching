package com.github.soulsearching.composables.searchComposables

import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.github.enteraname74.domain.model.Music
import com.github.soulsearching.R
import com.github.soulsearching.classes.utils.PlayerUtils
import com.github.soulsearching.classes.draggablestates.PlayerDraggableState
import com.github.soulsearching.classes.enumsAndTypes.BottomSheetStates
import com.github.soulsearching.composables.MusicItemComposable
import com.github.soulsearching.composables.PlayerSpacer
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.ui.theme.DynamicColor
import com.github.soulsearching.viewModels.PlayerMusicListViewModel
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchMusics(
    playerDraggableState: PlayerDraggableState,
    searchText: String,
    musicState: MusicState,
    onMusicEvent: (MusicEvent) -> Unit,
    playerMusicListViewModel: PlayerMusicListViewModel,
    isMainPlaylist: Boolean,
    focusManager: FocusManager,
    retrieveCoverMethod: (UUID?) -> Bitmap?,
    primaryColor: Color = DynamicColor.primary,
    textColor: Color = DynamicColor.onPrimary
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
                    title = stringResource(id = R.string.musics),
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
                            playerDraggableState.animateTo(BottomSheetStates.EXPANDED)
                        }.invokeOnCompletion { _ ->
                            PlayerUtils.playerViewModel.setCurrentPlaylistAndMusic(
                                music = selectedMusic,
                                playlist = foundedMusics as ArrayList<Music>,
                                playlistId = null,
                                isMainPlaylist = isMainPlaylist,
                                isForcingNewPlaylist = true
                            )
                            playerMusicListViewModel.savePlayerMusicList(PlayerUtils.playerViewModel.currentPlaylist.map { it.musicId } as ArrayList<UUID>)
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
