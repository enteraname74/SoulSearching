package com.github.soulsearching.classes

import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.classes.settings.SoulSearchingSettings
import com.github.soulsearching.classes.types.SortDirection
import com.github.soulsearching.classes.types.SortType
import com.github.soulsearching.classes.utils.AndroidUtils
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.service.PlayerService
import com.github.soulsearching.states.MusicState
import com.github.soulsearching.utils.PlayerUtils
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
    private val musicRepository: MusicRepository,
    private val playlistRepository: PlaylistRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val musicPlaylistRepository: MusicPlaylistRepository,
    private val musicAlbumRepository: MusicAlbumRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val albumArtistRepository: AlbumArtistRepository,
    private val imageCoverRepository: ImageCoverRepository,
    private val sortType: MutableStateFlow<Int> = MutableStateFlow(SortType.NAME),
    private val sortDirection: MutableStateFlow<Int> = MutableStateFlow(SortDirection.ASC),
    private val settings: SoulSearchingSettings
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
            AndroidUtils.removeMusicFromApp(
                musicRepository = musicRepository,
                albumRepository = albumRepository,
                artistRepository = artistRepository,
                albumArtistRepository = albumArtistRepository,
                musicAlbumRepository = musicAlbumRepository,
                musicArtistRepository = musicArtistRepository,
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
                imageCoverRepository.insertImageCover(
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
                val legacyArtist = artistRepository.getArtistFromInfo(
                    artistName = publicState.value.selectedMusic.artist
                )
                var newArtist = artistRepository.getArtistFromInfo(
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
                    artistRepository.insertArtist(
                        newArtist
                    )
                }

                // On met les infos de la musique à jour :
                musicArtistRepository.updateArtistOfMusic(
                    musicId = publicState.value.selectedMusic.musicId,
                    newArtistId = newArtist.artistId
                )

                AndroidUtils.modifyMusicAlbum(
                    artist = newArtist,
                    musicAlbumRepository = musicAlbumRepository,
                    albumRepository = albumRepository,
                    albumArtistRepository = albumArtistRepository,
                    legacyMusic = publicState.value.selectedMusic,
                    currentAlbum = publicState.value.album.trim(),
                )

                AndroidUtils.checkAndDeleteArtist(
                    artistToCheck = legacyArtist!!,
                    artistRepository = artistRepository,
                    musicArtistRepository = musicArtistRepository
                )
            } else if (publicState.value.selectedMusic.album != publicState.value.album) {
                val artist = AndroidUtils.getCorrespondingArtist(
                    musicId = publicState.value.selectedMusic.musicId,
                    artistRepository = artistRepository,
                    musicArtistRepository = musicArtistRepository
                )

                AndroidUtils.modifyMusicAlbum(
                    musicAlbumRepository = musicAlbumRepository,
                    albumRepository = albumRepository,
                    albumArtistRepository = albumArtistRepository,
                    legacyMusic = publicState.value.selectedMusic,
                    currentAlbum = publicState.value.album.trim(),
                    artist = artist!!
                )
            }
            if (publicState.value.hasCoverBeenChanged) {
                // On mets à jour la cover pour l'album et l'artiste :
                val artist = artistRepository.getArtistFromInfo(
                    publicState.value.artist.trim()
                )
                val album = albumRepository.getCorrespondingAlbum(
                    albumName = publicState.value.album.trim(),
                    artistId = artist!!.artistId
                )
                // Si l'artiste n'a pas d'image, on lui donne la nouvelle cover
                if (artist.coverId == null) {
                    artistRepository.updateArtistCover(coverId!!, artist.artistId)
                }
                // Faison de même pour l'album :
                if (album!!.coverId == null) {
                    albumRepository.updateAlbumCover(coverId!!, album.albumId)
                }
            }
            val newMusic = publicState.value.selectedMusic.copy(
                name = publicState.value.name.trim(),
                album = publicState.value.album.trim(),
                artist = publicState.value.artist.trim(),
                coverId = coverId
            )
            musicRepository.insertMusic(newMusic)

            CoroutineScope(Dispatchers.IO).launch {
                PlayerUtils.playerViewModel.updateMusic(newMusic)

                PlayerUtils.playerViewModel.currentMusic?.let {
                    if (it.musicId.compareTo(newMusic.musicId) == 0) {
                        PlayerUtils.playerViewModel.currentMusicCover = publicState.value.cover
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
        settings.setInt(
            key = SoulSearchingSettings.SORT_MUSICS_TYPE_KEY,
            value = event.type
        )
    }

    /**
     * Set the sort direction.
     */
    private fun setSortDirection(event: MusicEvent.SetSortDirection) {
        sortDirection.value = event.type

        settings.setInt(
            key = SoulSearchingSettings.SORT_MUSICS_DIRECTION_KEY,
            value = event.type
        )
    }

    /**
     * Toggle the favorite status of a given music.
     */
    private fun toggleFavoriteStatus(event: MusicEvent.SetFavorite) {
        CoroutineScope(Dispatchers.IO).launch {
            val isInFavorite = musicRepository.getMusicFromFavoritePlaylist(event.musicId) != null
            val playlistId = playlistRepository.getFavoritePlaylist().playlistId
            if (isInFavorite) {
                musicPlaylistRepository.deleteMusicFromPlaylist(
                    musicId = event.musicId,
                    playlistId = playlistId
                )
            } else {
                musicPlaylistRepository.insertMusicIntoPlaylist(
                    MusicPlaylist(
                        musicId = event.musicId,
                        playlistId = playlistId
                    )
                )
            }
        }
    }

    /**
     * Update the quick access state of the selected music.
     */
    private fun updateQuickAccessState() {
        CoroutineScope(Dispatchers.IO).launch {
            musicRepository.updateQuickAccessState(
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
            val currentNbPlayed = musicRepository.getNbPlayedOfMusic(event.musicId)
            musicRepository.updateNbPlayed(
                newNbPlayed = currentNbPlayed + 1,
                musicId = event.musicId
            )
        }
    }
}