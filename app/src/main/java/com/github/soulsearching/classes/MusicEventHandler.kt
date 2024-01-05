package com.github.soulsearching.classes

import com.github.soulsearching.classes.enumsAndTypes.SortDirection
import com.github.soulsearching.classes.enumsAndTypes.SortType
import com.github.soulsearching.classes.utils.ColorPaletteUtils
import com.github.soulsearching.classes.utils.PlayerUtils
import com.github.soulsearching.classes.utils.SharedPrefUtils
import com.github.soulsearching.classes.utils.Utils
import com.github.soulsearching.database.dao.AlbumArtistDao
import com.github.soulsearching.database.dao.AlbumDao
import com.github.soulsearching.database.dao.ArtistDao
import com.github.soulsearching.database.dao.ImageCoverDao
import com.github.soulsearching.database.dao.MusicAlbumDao
import com.github.soulsearching.database.dao.MusicArtistDao
import com.github.soulsearching.database.dao.MusicDao
import com.github.soulsearching.database.dao.MusicPlaylistDao
import com.github.soulsearching.database.dao.PlaylistDao
import com.github.soulsearching.database.model.Artist
import com.github.soulsearching.database.model.ImageCover
import com.github.soulsearching.database.model.MusicPlaylist
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.service.PlayerService
import com.github.soulsearching.states.MusicState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Class used to handle music events.
 */
class MusicEventHandler(
    private val privateState: MutableStateFlow<MusicState>,
    private val publicState: StateFlow<MusicState>,
    private val musicDao: MusicDao,
    private val playlistDao: PlaylistDao,
    private val albumDao: AlbumDao,
    private val artistDao: ArtistDao,
    private val musicPlaylistDao: MusicPlaylistDao,
    private val musicAlbumDao: MusicAlbumDao,
    private val musicArtistDao: MusicArtistDao,
    private val albumArtistDao: AlbumArtistDao,
    private val imageCoverDao: ImageCoverDao,
    private val sortType: MutableStateFlow<Int> = MutableStateFlow(SortType.NAME),
    private val sortDirection: MutableStateFlow<Int> = MutableStateFlow(SortDirection.ASC),
) {
    /**
     * handle a music event.
     */
    fun handleEvent(event: MusicEvent) {
        when(event) {
            is MusicEvent.DeleteDialog -> showOrHideDeleteDialog(event)
            is MusicEvent.RemoveFromPlaylistDialog -> showOrHideRemoveFromPlaylistDialog(event)
            is MusicEvent.BottomSheet -> showOrHideMusicBottomSheet(event)
            is MusicEvent.AddToPlaylistBottomSheet -> showOrHideAddToPlaylistBottomSheet(event)
            is MusicEvent.SetSelectedMusic -> setSelectedMusic(event)
            is MusicEvent.SetCover -> setSelectedMusicCover(event)
            is MusicEvent.SetName -> setSelectedMusicName(event)
            is MusicEvent.SetArtist -> setSelectedMusicArtist(event)
            is MusicEvent.SetAlbum -> setSelectedMusicAlbum(event)
            is MusicEvent.UpdateMusic -> updateMusic()
            is MusicEvent.DeleteMusic -> deleteMusicFromApp()
            is MusicEvent.SetSortType -> setSortType(event)
            is MusicEvent.SetSortDirection -> setSortDirection(event)
            is MusicEvent.SetFavorite -> toggleFavoriteStatus(event)
            is MusicEvent.UpdateQuickAccessState -> updateQuickAccessState()
            is MusicEvent.AddNbPlayed -> incrementNbPlayedOfMusic(event)
        }
    }

    /**
     * Show or hide the delete dialog.
     */
    private fun showOrHideDeleteDialog(event: MusicEvent.DeleteDialog) {
        privateState.update {
            it.copy(
                isDeleteDialogShown = event.isShown
            )
        }
    }

    /**
     * Show or hide the remove from playlist dialog.
     */
    private fun showOrHideRemoveFromPlaylistDialog(event: MusicEvent.RemoveFromPlaylistDialog) {
        privateState.update {
            it.copy(
                isRemoveFromPlaylistDialogShown = event.isShown
            )
        }
    }

    /**
     * Remove the selected music, from the MusicState, from the application
     */
    private fun deleteMusicFromApp() {
        CoroutineScope(Dispatchers.IO).launch {
            Utils.removeMusicFromApp(
                musicDao = musicDao,
                albumDao = albumDao,
                artistDao = artistDao,
                albumArtistDao = albumArtistDao,
                musicAlbumDao = musicAlbumDao,
                musicArtistDao = musicArtistDao,
                musicToRemove = publicState.value.selectedMusic
            )
        }
    }

    /**
     * Define the selected music of the state.
     */
    private fun setSelectedMusic(event: MusicEvent.SetSelectedMusic) {
        privateState.update {
            it.copy(
                selectedMusic = event.music
            )
        }
    }

    /**
     * Show or hide the music bottom sheet.
     */
    private fun showOrHideMusicBottomSheet(event: MusicEvent.BottomSheet) {
        privateState.update {
            it.copy(
                isBottomSheetShown = event.isShown
            )
        }
    }

    /**
     * Show or hide the add to playlist bottom sheet.
     */
    private fun showOrHideAddToPlaylistBottomSheet(event: MusicEvent.AddToPlaylistBottomSheet) {
        privateState.update {
            it.copy(
                isAddToPlaylistBottomSheetShown = event.isShown
            )
        }
    }

    /**
     * Set the cover of the selected music.
     * Primarily used when changing the cover of the selected music.
     */
    private fun setSelectedMusicCover(event: MusicEvent.SetCover) {
        privateState.update {
            it.copy(
                cover = event.cover,
                hasCoverBeenChanged = true
            )
        }
    }

    /**
     * Set the name of the selected music.
     * Primarily used when changing the name of the selected music.
     */
    private fun setSelectedMusicName(event: MusicEvent.SetName) {
        privateState.update {
            it.copy(
                name = event.name
            )
        }
    }
    /**
     * Set the artist of the selected music.
     * Primarily used when changing the artist of the selected music.
     */
    private fun setSelectedMusicArtist(event: MusicEvent.SetArtist) {
        privateState.update {
            it.copy(
                artist = event.artist
            )
        }
    }

    /**
     * Set the album of the selected music.
     * Primarily used when changing the album of the selected music.
     */
    private fun setSelectedMusicAlbum(event: MusicEvent.SetAlbum) {
        privateState.update {
            it.copy(
                album = event.album
            )
        }
    }

    /**
     * Update selected music information.
     */
    private fun updateMusic() {
        CoroutineScope(Dispatchers.IO).launch {
            val coverId = if (publicState.value.hasCoverBeenChanged) {
                val id = UUID.randomUUID()
                imageCoverDao.insertImageCover(
                    ImageCover(
                        coverId = id,
                        cover = publicState.value.cover
                    )
                )
                id
            } else {
                publicState.value.selectedMusic.coverId
            }
            if (publicState.value.selectedMusic.artist != publicState.value.artist.trim()) {
                val legacyArtist = artistDao.getArtistFromInfo(
                    artistName = publicState.value.selectedMusic.artist
                )
                var newArtist = artistDao.getArtistFromInfo(
                    artistName = publicState.value.artist.trim()
                )
                if (newArtist == null) {
                    // C'est un nouvel artist, il faut le créer :
                    val newArtistId = UUID.randomUUID()
                    newArtist = Artist(
                        artistId = newArtistId,
                        artistName = publicState.value.artist.trim(),
                        coverId = coverId
                    )
                    artistDao.insertArtist(
                        newArtist
                    )
                }

                // On met les infos de la musique à jour :
                musicArtistDao.updateArtistOfMusic(
                    musicId = publicState.value.selectedMusic.musicId,
                    newArtistId = newArtist.artistId
                )

                Utils.modifyMusicAlbum(
                    artist = newArtist,
                    musicAlbumDao = musicAlbumDao,
                    albumDao = albumDao,
                    albumArtistDao = albumArtistDao,
                    legacyMusic = publicState.value.selectedMusic,
                    currentAlbum = publicState.value.album.trim(),
                )

                Utils.checkAndDeleteArtist(
                    artistToCheck = legacyArtist!!,
                    artistDao = artistDao,
                    musicArtistDao = musicArtistDao
                )
            } else if (publicState.value.selectedMusic.album != publicState.value.album) {
                val artist = Utils.getCorrespondingArtist(
                    musicId = publicState.value.selectedMusic.musicId,
                    artistDao = artistDao,
                    musicArtistDao = musicArtistDao
                )

                Utils.modifyMusicAlbum(
                    musicAlbumDao = musicAlbumDao,
                    albumDao = albumDao,
                    albumArtistDao = albumArtistDao,
                    legacyMusic = publicState.value.selectedMusic,
                    currentAlbum = publicState.value.album.trim(),
                    artist = artist!!
                )
            }
            if (publicState.value.hasCoverBeenChanged) {
                // On mets à jour la cover pour l'album et l'artiste :
                val artist = artistDao.getArtistFromInfo(
                    publicState.value.artist.trim()
                )
                val album = albumDao.getCorrespondingAlbum(
                    albumName = publicState.value.album.trim(),
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
            val newMusic = publicState.value.selectedMusic.copy(
                name = publicState.value.name.trim(),
                album = publicState.value.album.trim(),
                artist = publicState.value.artist.trim(),
                coverId = coverId
            )
            musicDao.insertMusic(newMusic)

            CoroutineScope(Dispatchers.IO).launch {
                PlayerUtils.playerViewModel.updateMusic(newMusic)

                PlayerUtils.playerViewModel.currentMusic?.let {
                    if (it.musicId.compareTo(newMusic.musicId) == 0) {
                        PlayerUtils.playerViewModel.currentMusicCover = publicState.value.cover
                        PlayerUtils.playerViewModel.currentColorPalette =
                            ColorPaletteUtils.getPaletteFromAlbumArt(
                                PlayerUtils.playerViewModel.currentMusicCover
                            )
                        PlayerService.updateNotification()
                    }
                }
            }
        }
    }

    /**
     * Set the sort type.
     */
    private fun setSortType(event: MusicEvent.SetSortType) {
        sortType.value = event.type
        privateState.update {
            it.copy(
                sortType = event.type
            )
        }
        SharedPrefUtils.updateIntValue(
            keyToUpdate = SharedPrefUtils.SORT_MUSICS_TYPE_KEY,
            newValue = event.type
        )
    }

    /**
     * Set the sort direction.
     */
    private fun setSortDirection(event: MusicEvent.SetSortDirection) {
        sortDirection.value = event.type
        privateState.update {
            it.copy(
                sortDirection = event.type
            )
        }

        SharedPrefUtils.updateIntValue(
            keyToUpdate = SharedPrefUtils.SORT_MUSICS_DIRECTION_KEY,
            newValue = event.type
        )
    }

    /**
     * Toggle the favorite status of a given music.
     */
    private fun toggleFavoriteStatus(event: MusicEvent.SetFavorite) {
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

    /**
     * Update the quick acces state of the selected music.
     */
    private fun updateQuickAccessState() {
        CoroutineScope(Dispatchers.IO).launch {
            musicDao.updateQuickAccessState(
                musicId = publicState.value.selectedMusic.musicId,
                newQuickAccessState = !publicState.value.selectedMusic.isInQuickAccess
            )
        }
    }

    /**
     * Increment the number of time a music has been played.
     */
    private fun incrementNbPlayedOfMusic(event: MusicEvent.AddNbPlayed) {
        CoroutineScope(Dispatchers.IO).launch {
            val currentNbPlayed = musicDao.getNbPlayedOfMusic(event.musicId)
            musicDao.updateNbPlayed(
                newNbPlayed = currentNbPlayed + 1,
                musicId = event.musicId
            )
        }
    }
}