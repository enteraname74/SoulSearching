package com.github.soulsearching.classes

import com.github.soulsearching.classes.enumsAndTypes.SortDirection
import com.github.soulsearching.classes.enumsAndTypes.SortType
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
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.states.MusicState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
            MusicEvent.DeleteMusic -> deleteMusicFromApp()
            else -> {}
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

}