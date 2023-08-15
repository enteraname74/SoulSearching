package com.github.soulsearching.classes

import android.util.Log
import com.github.soulsearching.database.dao.*
import com.github.soulsearching.database.model.Artist
import com.github.soulsearching.database.model.ImageCover
import com.github.soulsearching.database.model.MusicPlaylist
import com.github.soulsearching.database.model.Playlist
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.events.PlaylistEvent
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.states.PlaylistState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

object EventUtils {
    fun onMusicEvent(
        event: MusicEvent,
        _state: MutableStateFlow<MusicState>,
        _sortType: MutableStateFlow<Int> = MutableStateFlow(SortType.NAME),
        _sortDirection: MutableStateFlow<Int> = MutableStateFlow(SortDirection.ASC),
        state: StateFlow<MusicState>,
        musicDao: MusicDao,
        playlistDao: PlaylistDao,
        albumDao: AlbumDao,
        artistDao: ArtistDao,
        musicPlaylistDao: MusicPlaylistDao,
        musicAlbumDao: MusicAlbumDao,
        musicArtistDao: MusicArtistDao,
        albumArtistDao: AlbumArtistDao,
        imageCoverDao: ImageCoverDao,
    ) {
        when (event) {
            is MusicEvent.DeleteDialog -> {
                _state.update {
                    it.copy(
                        isDeleteDialogShown = event.isShown
                    )
                }
            }
            is MusicEvent.RemoveFromPlaylistDialog -> {
                _state.update {
                    it.copy(
                        isRemoveFromPlaylistDialogShown = event.isShown
                    )
                }
            }
            MusicEvent.DeleteMusic -> {
                CoroutineScope(Dispatchers.IO).launch {
                    Utils.removeMusicFromApp(
                        musicDao = musicDao,
                        albumDao = albumDao,
                        artistDao = artistDao,
                        albumArtistDao = albumArtistDao,
                        musicAlbumDao = musicAlbumDao,
                        musicArtistDao = musicArtistDao,
                        musicToRemove = state.value.selectedMusic
                    )
                }
            }
            is MusicEvent.SetSelectedMusic -> {
                _state.update {
                    it.copy(
                        selectedMusic = event.music
                    )
                }
            }
            is MusicEvent.BottomSheet -> {
                _state.update {
                    it.copy(
                        isBottomSheetShown = event.isShown
                    )
                }
            }
            is MusicEvent.AddToPlaylistBottomSheet -> {
                _state.update {
                    it.copy(
                        isAddToPlaylistBottomSheetShown = event.isShown
                    )
                }
            }
            is MusicEvent.SetCover -> {
                _state.update {
                    it.copy(
                        cover = event.cover,
                        hasCoverBeenChanged = true
                    )
                }
            }
            is MusicEvent.SetName -> {
                _state.update {
                    it.copy(
                        name = event.name
                    )
                }
            }
            is MusicEvent.SetArtist -> {
                _state.update {
                    it.copy(
                        artist = event.artist
                    )
                }
            }
            is MusicEvent.SetAlbum -> {
                _state.update {
                    it.copy(
                        album = event.album
                    )
                }
            }
            is MusicEvent.UpdateMusic -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val coverId = if (state.value.hasCoverBeenChanged) {
                        val id = UUID.randomUUID()
                        imageCoverDao.insertImageCover(
                            ImageCover(
                                coverId = id,
                                cover = state.value.cover
                            )
                        )
                        id
                    } else {
                        state.value.selectedMusic.coverId
                    }
                    if (state.value.selectedMusic.artist != state.value.artist.trim()) {
                        val legacyArtist = artistDao.getArtistFromInfo(
                            artistName = state.value.selectedMusic.artist
                        )
                        var newArtist = artistDao.getArtistFromInfo(
                            artistName = state.value.artist.trim()
                        )
                        if (newArtist == null) {
                            // C'est un nouvel artist, il faut le créer :
                            Log.d("Nouvel artiste", "nouvel artiste")
                            val newArtistId = UUID.randomUUID()
                            newArtist = Artist(
                                artistId = newArtistId,
                                artistName = state.value.artist.trim(),
                                coverId = coverId
                            )
                            artistDao.insertArtist(
                                newArtist
                            )
                        }

                        // On met les infos de la musique à jour :
                        musicArtistDao.updateArtistOfMusic(
                            musicId = state.value.selectedMusic.musicId,
                            newArtistId = newArtist.artistId
                        )

                        Utils.modifyMusicAlbum(
                            artist = newArtist,
                            musicAlbumDao = musicAlbumDao,
                            albumDao = albumDao,
                            albumArtistDao = albumArtistDao,
                            legacyMusic = state.value.selectedMusic,
                            currentAlbum = state.value.album.trim(),
                        )

                        Utils.checkAndDeleteArtist(
                            artistToCheck = legacyArtist!!,
                            artistDao = artistDao,
                            musicArtistDao = musicArtistDao
                        )
                    } else if (state.value.selectedMusic.album != state.value.album) {
                        val artist = Utils.getCorrespondingArtist(
                            musicId = state.value.selectedMusic.musicId,
                            artistDao = artistDao,
                            musicArtistDao = musicArtistDao
                        )

                        Utils.modifyMusicAlbum(
                            musicAlbumDao = musicAlbumDao,
                            albumDao = albumDao,
                            albumArtistDao = albumArtistDao,
                            legacyMusic = state.value.selectedMusic,
                            currentAlbum = state.value.album.trim(),
                            artist = artist!!
                        )
                    }
                    if (state.value.hasCoverBeenChanged) {
                        // On mets à jour la cover pour l'album et l'artiste :
                        val artist = artistDao.getArtistFromInfo(
                            state.value.artist.trim()
                        )
                        val album = albumDao.getCorrespondingAlbum(
                            albumName = state.value.album.trim(),
                            artistId = artist!!.artistId
                        )
                        // Si l'artiste n'a pas d'image, on lui donne la nouvelle cover
                        if (artist.coverId == null) {
                            artistDao.updateArtistCover(coverId!!, artist.artistId)
                        }
                        // Faison de même pour l'album :
                        if (album!!.coverId == null) {
                            albumDao.updateAlbumCover(coverId!!, album.albumId)
                        }
                    }
                    Log.d("UPDATE MUSIC", "")
                    musicDao.insertMusic(
                        state.value.selectedMusic.copy(
                            name = state.value.name.trim(),
                            album = state.value.album.trim(),
                            artist = state.value.artist.trim(),
                            coverId = coverId
                        )
                    )

                    PlayerUtils.playerViewModel.updateMusic(
                        state.value.selectedMusic.copy(
                            name = state.value.name.trim(),
                            album = state.value.album.trim(),
                            artist = state.value.artist.trim(),
                            coverId = coverId
                        )
                    )
                }
            }
            is MusicEvent.SetSortType -> {
                _sortType.value = event.type
                _state.update {
                    it.copy(
                        sortType = event.type
                    )
                }
                SharedPrefUtils.updateSort(
                    keyToUpdate = SharedPrefUtils.SORT_MUSICS_TYPE_KEY,
                    newValue = event.type
                )
            }
            is MusicEvent.SetSortDirection -> {
                _sortDirection.value = event.type

                SharedPrefUtils.updateSort(
                    keyToUpdate = SharedPrefUtils.SORT_MUSICS_DIRECTION_KEY,
                    newValue = event.type
                )
            }
            is MusicEvent.SetPlayedMusic -> {
                PlayerUtils.playerViewModel.currentMusic = event.music
            }
            is MusicEvent.SetFavorite -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val isInFavorite = musicDao.getMusicFromFavoritePlaylist(event.musicId) != null
                    val playlistId = playlistDao.getFavoritePlaylist().playlistId
                    if (isInFavorite) {
                        musicPlaylistDao.deleteMusicFromPlaylist(
                            musicId = event.musicId,
                            playlistId = playlistId
                        )
                    } else {
                        musicPlaylistDao.insertMusicIntoPlaylist(
                            MusicPlaylist(
                                musicId = event.musicId,
                                playlistId = playlistId
                            )
                        )
                    }
                }
            }
            is MusicEvent.UpdateQuickAccessState -> {
                CoroutineScope(Dispatchers.IO).launch {
                    musicDao.updateQuickAccessState(
                        musicId = state.value.selectedMusic.musicId,
                        newQuickAccessState = !state.value.selectedMusic.isInQuickAccess
                    )
                }
            }
            is MusicEvent.AddNbPlayed -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val currentNbPlayed = musicDao.getNbPlayedOfMusic(event.musicId)
                    musicDao.updateNbPlayed(
                        newNbPlayed = currentNbPlayed + 1,
                        musicId = event.musicId
                    )
                }
            }
            else -> {}
        }
    }

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
                SharedPrefUtils.updateSort(
                    keyToUpdate = SharedPrefUtils.SORT_PLAYLISTS_DIRECTION_KEY,
                    newValue = event.type
                )
            }
            is PlaylistEvent.SetSortType -> {
                _sortType.value = event.type
                SharedPrefUtils.updateSort(
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
            else -> {}
        }
    }
}