package com.github.soulsearching.classes

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
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

class Utils {
    companion object {
        private var isAddingMusic = false

        fun getBitmapFromUri(uri: Uri, contentResolver: ContentResolver): Bitmap {
            return if (Build.VERSION.SDK_INT >= 29) {
                contentResolver.loadThumbnail(
                    uri,
                    Size(400, 400),
                    null
                )
            } else {
                Bitmap.createScaledBitmap(
                    MediaStore.Images.Media.getBitmap(
                        contentResolver,
                        uri
                    ), 400, 400, false
                )
            }
        }

        private suspend fun removeMusicFromApp(
            musicDao: MusicDao,
            albumDao: AlbumDao,
            artistDao: ArtistDao,
            albumArtistDao: AlbumArtistDao,
            musicAlbumDao: MusicAlbumDao,
            musicArtistDao: MusicArtistDao,
            musicToRemove: Music
        ) {
            val artist = artistDao.getArtistFromInfo(
                artistName = musicToRemove.artist
            )
            val album = getCorrespondingAlbum(
                musicId = musicToRemove.musicId,
                albumDao = albumDao,
                musicAlbumDao = musicAlbumDao
            )

            musicDao.deleteMusic(music = musicToRemove)

            checkAndDeleteAlbum(
                albumToCheck = album!!,
                albumDao = albumDao,
                musicAlbumDao = musicAlbumDao,
                albumArtistDao = albumArtistDao
            )
            checkAndDeleteArtist(
                artistToCheck = artist!!,
                musicArtistDao = musicArtistDao,
                artistDao = artistDao
            )
        }

        private suspend fun removeAlbumFromApp(
            albumToRemove: Album,
            albumDao: AlbumDao,
            albumArtistDao: AlbumArtistDao
        ) {
            albumDao.deleteAlbum(album = albumToRemove)
            albumArtistDao.deleteAlbumFromArtist(albumId = albumToRemove.albumId)
        }

        private suspend fun removeArtistFromApp(
            artistToRemove: Artist,
            artistDao: ArtistDao
        ) {
            artistDao.deleteArtist(artist = artistToRemove)
        }

        private suspend fun checkAndDeleteAlbum(
            albumToCheck: Album,
            albumDao: AlbumDao,
            musicAlbumDao: MusicAlbumDao,
            albumArtistDao: AlbumArtistDao
        ) {
            if (musicAlbumDao.getNumberOfMusicsFromAlbum(
                    albumId = albumToCheck.albumId
                ) == 0
            ) {
                removeAlbumFromApp(
                    albumToRemove = albumToCheck,
                    albumDao = albumDao,
                    albumArtistDao = albumArtistDao
                )
            }
        }

        suspend fun checkAndDeleteArtist(
            artistToCheck: Artist,
            musicArtistDao: MusicArtistDao,
            artistDao: ArtistDao
        ) {
            if (musicArtistDao.getNumberOfMusicsFromArtist(
                    artistId = artistToCheck.artistId
                ) == 0
            ) {
                removeArtistFromApp(
                    artistToRemove = artistToCheck,
                    artistDao = artistDao
                )
            }
        }

        suspend fun modifyMusicAlbum(
            artist: Artist,
            musicAlbumDao: MusicAlbumDao,
            albumDao: AlbumDao,
            albumArtistDao: AlbumArtistDao,
            legacyMusic: Music,
            currentAlbum: String,
            currentCover: Bitmap?,
        ) {
            // On récupère l'ancien album :
            val legacyAlbum = getCorrespondingAlbum(
                musicId = legacyMusic.musicId,
                albumDao = albumDao,
                musicAlbumDao = musicAlbumDao
            )

            var newAlbum = albumDao.getCorrespondingAlbum(
                albumName = currentAlbum,
                artistId = artist.artistId
            )

            if (newAlbum == null) {
                // C'est un nouvel album, il faut le créer :
                println("Nouvel album !")
                val album = Album(
                    albumName = currentAlbum,
                    albumCover = currentCover
                )
                newAlbum = album

                albumDao.insertAlbum(
                    album = album
                )
                // On lie l'album crée à son artiste :
                albumArtistDao.insertAlbumIntoArtist(
                    AlbumArtist(
                        albumId = newAlbum.albumId,
                        artistId = artist.artistId
                    )
                )
            }
            // On met les infos de la musique à jour :
            musicAlbumDao.updateAlbumOfMusic(
                musicId = legacyMusic.musicId,
                newAlbumId = newAlbum.albumId
            )

            checkAndDeleteAlbum(
                albumToCheck = legacyAlbum!!,
                musicAlbumDao = musicAlbumDao,
                albumDao = albumDao,
                albumArtistDao = albumArtistDao
            )
        }

        private fun getCorrespondingAlbum(
            musicId: UUID,
            albumDao: AlbumDao,
            musicAlbumDao: MusicAlbumDao
        ): Album? {
            val albumId: UUID? = musicAlbumDao.getAlbumIdFromMusicId(
                musicId = musicId
            )
            return if (albumId == null) {
                null
            } else {
                albumDao.getAlbumFromId(
                    albumId = albumId
                )
            }
        }

        fun getCorrespondingArtist(
            musicId: UUID,
            artistDao: ArtistDao,
            musicArtistDao: MusicArtistDao
        ): Artist? {
            val artistId: UUID? = musicArtistDao.getArtistIdFromMusicId(
                musicId = musicId
            )
            return if (artistId == null) {
                null
            } else {
                artistDao.getArtistFromId(
                    artistId = artistId
                )
            }
        }

        fun onMusicEvent(
            event : MusicEvent,
            _state : MutableStateFlow<MusicState>,
            state : StateFlow<MusicState>,
            musicDao : MusicDao,
            playlistDao: PlaylistDao,
            albumDao : AlbumDao,
            artistDao: ArtistDao,
            musicPlaylistDao : MusicPlaylistDao,
            musicAlbumDao: MusicAlbumDao,
            musicArtistDao: MusicArtistDao,
            albumArtistDao: AlbumArtistDao,
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
                        removeMusicFromApp(
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
                MusicEvent.AddMusic -> {
                    if (!isAddingMusic) {
                        isAddingMusic = true
                        val music = Music(
                            musicId = UUID.randomUUID(),
                            name = "Nom Musique",
                            album = "Nom Album",
                            artist = "Nom Artiste",
                            duration = 1000L,
                            path = ""
                        )

                        CoroutineScope(Dispatchers.IO).launch {
                            val correspondingArtist = artistDao.getArtistFromInfo(
                                artistName = music.artist
                            )
                            val allAlbums = albumDao.getAllAlbumsWithArtistSimple()
                            val correspondingAlbum = allAlbums.find {
                                (it.album.albumName == music.album)
                                        && (it.artist!!.artistName == music.artist)
                            }
                            var albumId = UUID.randomUUID()
                            var artistId = correspondingArtist?.artistId ?: UUID.randomUUID()
                            if (correspondingAlbum == null) {
                                albumDao.insertAlbum(
                                    Album(
                                        albumId = albumId,
                                        albumName = music.album,
                                        albumCover = music.albumCover,
                                    )
                                )
                                artistDao.insertArtist(
                                    Artist(
                                        artistId = artistId,
                                        artistName = music.artist,
                                        artistCover = music.albumCover
                                    )
                                )
                                albumArtistDao.insertAlbumIntoArtist(
                                    AlbumArtist(
                                        albumId = albumId,
                                        artistId = artistId
                                    )
                                )

                            } else {
                                albumId = correspondingAlbum.album.albumId
                                artistId = correspondingAlbum.artist!!.artistId
                                // Si la musique n'a pas de couverture, on lui donne celle de son album :
                                if (music.albumCover == null) {
                                    music.albumCover = correspondingAlbum.album.albumCover
                                }
                            }
                            musicDao.insertMusic(music)
                            musicAlbumDao.insertMusicIntoAlbum(
                                MusicAlbum(
                                    musicId = music.musicId,
                                    albumId = albumId
                                )
                            )
                            musicArtistDao.insertMusicIntoArtist(
                                MusicArtist(
                                    musicId = music.musicId,
                                    artistId = artistId
                                )
                            )
                            isAddingMusic = false
                        }
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
                else -> {}
            }
        }

        fun onPlaylistEvent(
            event : PlaylistEvent,
            _state : MutableStateFlow<PlaylistState>,
            state : StateFlow<PlaylistState>,
            playlistDao: PlaylistDao,
            musicPlaylistDao: MusicPlaylistDao
        ) {
            when(event){
                is PlaylistEvent.AddPlaylist -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        playlistDao.insertPlaylist(
                            Playlist(
                                playlistId = UUID.randomUUID(),
                                name = "test"
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
                        _state.update {it.copy(
                            multiplePlaylistSelected = ArrayList()
                        )
                        }
                    }
                }
                is PlaylistEvent.DeletePlaylist -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        playlistDao.deletePlaylist(event.playlist)
                    }
                }
                is PlaylistEvent.SetSelectedPlaylist -> {
                    _state.update { it.copy(
                        selectedPlaylist = event.playlist
                    ) }
                }
                is PlaylistEvent.TogglePlaylistSelectedState -> {
                    val newList = ArrayList(state.value.multiplePlaylistSelected)
                    if (event.playlistId in newList) newList.remove(event.playlistId)
                    else newList.add(event.playlistId)

                    _state.update {it.copy(
                        multiplePlaylistSelected = newList
                    )
                    }
                }
                is PlaylistEvent.PlaylistsSelection -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        val playlists = playlistDao.getAllPlaylistsWithMusicsSimple().filter { playlistWithMusics ->
                            playlistWithMusics.musics.find { it.musicId == event.musicId } == null
                        }
                        _state.update { it.copy(
                            multiplePlaylistSelected = ArrayList(),
                            playlistsWithoutMusicId = playlists
                        ) }
                    }
                }
                PlaylistEvent.UpdatePlaylist -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        playlistDao.insertPlaylist(
                            Playlist(
                                playlistId = state.value.selectedPlaylist.playlistId,
                                name = state.value.name,
                                playlistCover = state.value.cover
                            )
                        )
                    }
                }
                is PlaylistEvent.PlaylistFromId -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        val playlist = playlistDao.getPlaylistFromId(event.playlistId)
                        _state.update { it.copy(
                            selectedPlaylist = playlist,
                            name = playlist.name,
                            cover = playlist.playlistCover
                        ) }
                    }
                }
                is PlaylistEvent.SetName -> {
                    _state.update { it.copy(
                        name = event.name
                    ) }
                }
                is PlaylistEvent.SetCover -> {
                    _state.update { it.copy(
                        cover = event.cover
                    ) }
                }
                else -> {}
            }
        }
    }
}