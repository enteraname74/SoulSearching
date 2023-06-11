package com.github.soulsearching.classes

import android.util.Log
import com.github.soulsearching.database.dao.*
import com.github.soulsearching.database.model.*
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

class EventUtils {
    companion object {
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
                MusicEvent.AddToPlaylist -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        val firstPlaylistId = playlistDao.getFirstPlaylistId()
                        musicPlaylistDao.insertMusicIntoPlaylist(
                            MusicPlaylist(
                                musicId = state.value.selectedMusic.musicId,
                                playlistId = firstPlaylistId
                            )
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
                            isAddToPlaylistDialogShown = event.isShown
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
                        Log.d("UPDATE MUSIC","")
                        musicDao.insertMusic(
                            Music(
                                musicId = state.value.selectedMusic.musicId,
                                name = state.value.name.trim(),
                                album = state.value.album.trim(),
                                artist = state.value.artist.trim(),
                                coverId = coverId,
                                path = state.value.selectedMusic.path,
                                duration = state.value.selectedMusic.duration
                            )
                        )
                    }
                }
                is MusicEvent.SetSortDirection -> {
                    _sortDirection.value = event.type
                }
                is MusicEvent.SetSortType -> {
                    _sortType.value = event.type
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
            musicPlaylistDao: MusicPlaylistDao
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
                is PlaylistEvent.DeletePlaylist -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        playlistDao.deletePlaylist(state.value.selectedPlaylist)
                    }
                }
                is PlaylistEvent.SetSelectedPlaylist -> {
                    _state.update {
                        it.copy(
                            selectedPlaylist = event.playlistWithMusics.playlist
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
                                playlistsWithoutMusicId = playlists
                            )
                        }
                    }
                }
                PlaylistEvent.UpdatePlaylist -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        playlistDao.insertPlaylist(
                            Playlist(
                                playlistId = state.value.selectedPlaylist.playlistId,
                                name = state.value.name.trim()
                            )
                        )
                    }
                }
                is PlaylistEvent.PlaylistFromId -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        val playlist = playlistDao.getPlaylistFromId(event.playlistId)
                        _state.update {
                            it.copy(
                                selectedPlaylist = playlist,
                                name = playlist.name
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
                            cover = event.cover
                        )
                    }
                }
                is PlaylistEvent.SetSortDirection -> {
                    _sortDirection.value = event.type
                }
                is PlaylistEvent.SetSortType -> {
                    _sortType.value = event.type
                }
                else -> {}
            }
        }
    }
}