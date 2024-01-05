package com.github.soulsearching.classes.utils

import android.util.Log
import com.github.soulsearching.classes.enumsAndTypes.SortDirection
import com.github.soulsearching.classes.enumsAndTypes.SortType
import com.github.soulsearching.database.dao.ImageCoverDao
import com.github.soulsearching.database.dao.MusicPlaylistDao
import com.github.soulsearching.database.dao.PlaylistDao
import com.github.soulsearching.database.model.ImageCover
import com.github.soulsearching.database.model.MusicPlaylist
import com.github.soulsearching.database.model.Playlist
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.PlaylistState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

object EventUtils {
    fun onPlaylistEvent(
        event: PlaylistEvent,
        _state: MutableStateFlow<PlaylistState>,
        _sortType: MutableStateFlow<Int> = MutableStateFlow(SortType.NAME),
        _sortDirection: MutableStateFlow<Int> = MutableStateFlow(SortDirection.ASC),
        state: StateFlow<PlaylistState>,
        playlistDao: PlaylistDao,
        musicPlaylistDao: MusicPlaylistDao,
        imageCoverDao: ImageCoverDao
    ) {
        when (event) {
            is PlaylistEvent.BottomSheet -> {
                _state.update {
                    it.copy(
                        isBottomSheetShown = event.isShown
                    )
                }
            }
            is PlaylistEvent.DeleteDialog -> {
                _state.update {
                    it.copy(
                        isDeleteDialogShown = event.isShown
                    )
                }
            }
            is PlaylistEvent.CreatePlaylistDialog -> {
                _state.update {
                    it.copy(
                        isCreatePlaylistDialogShown = event.isShown
                    )
                }
            }
            is PlaylistEvent.AddPlaylist -> {
                CoroutineScope(Dispatchers.IO).launch {
                    playlistDao.insertPlaylist(
                        Playlist(
                            playlistId = UUID.randomUUID(),
                            name = event.name
                        )
                    )
                }
            }
            is PlaylistEvent.AddMusicToPlaylists -> {
                CoroutineScope(Dispatchers.IO).launch {
                    for (selectedPlaylistId in state.value.multiplePlaylistSelected) {
                        musicPlaylistDao.insertMusicIntoPlaylist(
                            MusicPlaylist(
                                musicId = event.musicId,
                                playlistId = selectedPlaylistId
                            )
                        )
                    }
                    _state.update {
                        it.copy(
                            multiplePlaylistSelected = ArrayList()
                        )
                    }
                }
            }
            is PlaylistEvent.RemoveMusicFromPlaylist -> {
                CoroutineScope(Dispatchers.IO).launch {
                    Log.d("PLAYLIST EVENT", "REMOVE FROM PLAYLIST")
                    Log.d("MUSIC ID : ", "${event.musicId}")
                    Log.d("PLAYLIST ID : ", "${state.value.selectedPlaylist.playlistId}")
                    musicPlaylistDao.deleteMusicFromPlaylist(
                        musicId = event.musicId,
                        playlistId = state.value.selectedPlaylist.playlistId
                    )
                }
            }
            is PlaylistEvent.DeletePlaylist -> {
                CoroutineScope(Dispatchers.IO).launch {
                    playlistDao.deletePlaylist(state.value.selectedPlaylist)
                }
            }
            is PlaylistEvent.SetSelectedPlaylist -> {
                _state.update {
                    it.copy(
                        selectedPlaylist = event.playlist
                    )
                }
            }
            is PlaylistEvent.TogglePlaylistSelectedState -> {
                val newList = ArrayList(state.value.multiplePlaylistSelected)
                if (event.playlistId in newList) newList.remove(event.playlistId)
                else newList.add(event.playlistId)

                _state.update {
                    it.copy(
                        multiplePlaylistSelected = newList
                    )
                }
            }
            is PlaylistEvent.PlaylistsSelection -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val playlists = playlistDao.getAllPlaylistsWithMusicsSimple()
                        .filter { playlistWithMusics ->
                            playlistWithMusics.musics.find { it.musicId == event.musicId } == null
                        }
                    _state.update {
                        it.copy(
                            multiplePlaylistSelected = ArrayList(),
                            playlistsWithMusics = playlists
                        )
                    }
                }
            }
            PlaylistEvent.UpdatePlaylist -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val coverId = if (state.value.hasSetNewCover) {
                        val id = UUID.randomUUID()
                        imageCoverDao.insertImageCover(
                            ImageCover(
                                coverId = id,
                                cover = state.value.cover
                            )
                        )
                        id
                    } else {
                        state.value.selectedPlaylist.coverId
                    }
                    playlistDao.insertPlaylist(
                        state.value.selectedPlaylist.copy(
                            name = state.value.name.trim(),
                            coverId = coverId
                        )
                    )
                }
            }
            is PlaylistEvent.PlaylistFromId -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val playlist = playlistDao.getPlaylistFromId(event.playlistId)
                    val cover = if (playlist.coverId != null) {
                        imageCoverDao.getCoverOfElement(playlist.coverId!!)?.cover
                    } else {
                        null
                    }
                    _state.update {
                        it.copy(
                            selectedPlaylist = playlist,
                            name = playlist.name,
                            cover = cover,
                            hasSetNewCover = false
                        )
                    }
                }
            }
            is PlaylistEvent.SetName -> {
                _state.update {
                    it.copy(
                        name = event.name
                    )
                }
            }
            is PlaylistEvent.SetCover -> {
                _state.update {
                    it.copy(
                        cover = event.cover,
                        hasSetNewCover = true
                    )
                }
            }
            is PlaylistEvent.SetSortDirection -> {
                _sortDirection.value = event.type
                SharedPrefUtils.updateIntValue(
                    keyToUpdate = SharedPrefUtils.SORT_PLAYLISTS_DIRECTION_KEY,
                    newValue = event.type
                )
            }
            is PlaylistEvent.SetSortType -> {
                _sortType.value = event.type
                SharedPrefUtils.updateIntValue(
                    keyToUpdate = SharedPrefUtils.SORT_PLAYLISTS_TYPE_KEY,
                    newValue = event.type
                )
            }
            is PlaylistEvent.AddFavoritePlaylist -> {
                CoroutineScope(Dispatchers.IO).launch {
                    playlistDao.insertPlaylist(
                        Playlist(
                            playlistId = UUID.randomUUID(),
                            name = event.name,
                            isFavorite = true
                        )
                    )
                }
            }
            is PlaylistEvent.UpdateQuickAccessState -> {
                CoroutineScope(Dispatchers.IO).launch {
                    playlistDao.updateQuickAccessState(
                        newQuickAccessState = !state.value.selectedPlaylist.isInQuickAccess,
                        playlistId = state.value.selectedPlaylist.playlistId
                    )
                }
            }
            is PlaylistEvent.AddNbPlayed -> {
                CoroutineScope(Dispatchers.IO).launch {
                    playlistDao.updateNbPlayed(
                        newNbPlayed = playlistDao.getNbPlayedOfPlaylist(event.playlistId) + 1,
                        playlistId = event.playlistId
                    )
                }
            }
        }
    }
}