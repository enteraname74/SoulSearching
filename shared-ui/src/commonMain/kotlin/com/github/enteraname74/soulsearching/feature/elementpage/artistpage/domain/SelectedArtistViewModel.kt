package com.github.enteraname74.soulsearching.feature.elementpage.artistpage.domain

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicPlaylist
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.enteraname74.domain.usecase.album.DeleteAlbumUseCase
import com.github.enteraname74.domain.usecase.album.GetAlbumsOfArtistsUseCase
import com.github.enteraname74.domain.usecase.album.UpsertAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.GetArtistWithMusicsUseCase
import com.github.enteraname74.domain.usecase.artist.UpsertArtistUseCase
import com.github.enteraname74.domain.usecase.music.DeleteMusicUseCase
import com.github.enteraname74.domain.usecase.music.GetMusicUseCase
import com.github.enteraname74.domain.usecase.music.UpsertMusicUseCase
import com.github.enteraname74.domain.usecase.musicplaylist.UpsertMusicIntoPlaylistUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsUseCase
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class SelectedArtistViewModel(
    private val getAllPlaylistWithMusicsUseCase: GetAllPlaylistWithMusicsUseCase,
    private val upsertAlbumUseCase: UpsertAlbumUseCase,
    private val deleteAlbumUseCase: DeleteAlbumUseCase,
    private val getArtistWithMusicsUseCase: GetArtistWithMusicsUseCase,
    private val getAlbumsOfArtistsUseCase: GetAlbumsOfArtistsUseCase,
    private val playbackManager: PlaybackManager,
    private val upsertMusicIntoPlaylistUseCase: UpsertMusicIntoPlaylistUseCase,
    private val getMusicUseCase: GetMusicUseCase,
    private val deleteMusicUseCase: DeleteMusicUseCase,
    private val upsertMusicUseCase: UpsertMusicUseCase,
    private val upsertArtistUseCase: UpsertArtistUseCase,
) : ScreenModel {
    private var _selectedArtistWithMusics: StateFlow<ArtistWithMusics?> = MutableStateFlow(
        ArtistWithMusics()
    )

    private var artistAlbums: StateFlow<List<Album>> = MutableStateFlow(emptyList())

    private val _state = MutableStateFlow(SelectedArtistState())
    var state = combine(
        _state,
        getAllPlaylistWithMusicsUseCase()
    ) { state, playlists ->
        state.copy(
            allPlaylists = playlists
        )
    }.stateIn(
        screenModelScope,
        SharingStarted.WhileSubscribed(5000),
        SelectedArtistState()
    )

    /**
     * Handles the events of the selected artist screen.
     */
    fun onEvent(event: SelectedArtistEvent) {
        when(event) {
            is SelectedArtistEvent.AddMusicToPlaylists -> addMusicToPlaylists(
                musicId = event.musicId,
                selectedPlaylistsIds = event.selectedPlaylistsIds
            )
            is SelectedArtistEvent.AddNbPlayed -> incrementNbPlayed(artistId = event.artistId)
            is SelectedArtistEvent.DeleteMusic -> deleteMusicFromApp(musicId = event.musicId)
            is SelectedArtistEvent.SetAddToPlaylistBottomSheetVisibility -> showOrHideAddToPlaylistBottomSheet(isShown = event.isShown)
            is SelectedArtistEvent.SetDeleteMusicDialogVisibility -> showOrHideDeleteDialog(isShown = event.isShown)
            is SelectedArtistEvent.SetMusicBottomSheetVisibility -> showOrHideMusicBottomSheet(isShown = event.isShown)
            is SelectedArtistEvent.SetSelectedArtist -> setSelectedArtist(artistId = event.artistId)
            is SelectedArtistEvent.ToggleQuickAccessState -> toggleQuickAccessState(music = event.music)
            is SelectedArtistEvent.DeleteAlbum -> deleteAlbum(albumId = event.albumId)
            is SelectedArtistEvent.SetAlbumBottomSheetVisibility -> setAlbumBottomSheetVisibility(isShown = event.isShown)
            is SelectedArtistEvent.SetDeleteAlbumDialogVisibility -> setDeleteAlbumDialogVisibility(isShown = event.isShown)
            is SelectedArtistEvent.ToggleAlbumQuickAccessState -> toggleAlbumQuickAccessState(album = event.album)
        }
    }

    /**
     * Toggle the quick access state of an album.
     */
    private fun toggleAlbumQuickAccessState(album: Album) {
        CoroutineScope(Dispatchers.IO).launch {
            upsertAlbumUseCase(
                album = album.copy(
                    isInQuickAccess = !album.isInQuickAccess,
                )
            )
        }
    }

    /**
     * Show or hide the delete album dialog.
     */
    private fun setDeleteAlbumDialogVisibility(isShown: Boolean) {
        _state.update {
            it.copy(
                isDeleteAlbumDialogShown = isShown
            )
        }
    }

    /**
     * Show or hide the album bottom sheet.
     */
    private fun setAlbumBottomSheetVisibility(isShown: Boolean) {
        _state.update {
            it.copy(
                isAlbumBottomSheetShown = isShown
            )
        }
    }

    /**
     * Delete an album from its id.
     */
    private fun deleteAlbum(albumId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            deleteAlbumUseCase(albumId = albumId)
        }
    }

    /**
     * Set the selected artist.
     */
    private fun setSelectedArtist(artistId: UUID) {
        _selectedArtistWithMusics = getArtistWithMusicsUseCase(artistId = artistId)
            .stateIn(
                screenModelScope, SharingStarted.WhileSubscribed(), ArtistWithMusics()
            )

        artistAlbums = getAlbumsOfArtistsUseCase(
            artistId = artistId
        ).stateIn(
            screenModelScope, SharingStarted.WhileSubscribed(), emptyList()
        )

        state =
            combine(
                _state,
                _selectedArtistWithMusics,
                getAllPlaylistWithMusicsUseCase(),
                artistAlbums
            ) { state, artist, playlists, albums ->
                state.copy(
                    artistWithMusics = artist ?: ArtistWithMusics(),
                    allPlaylists = playlists,
                    artistAlbums = albums
                )
            }.stateIn(
                screenModelScope,
                SharingStarted.WhileSubscribed(5000),
                SelectedArtistState()
            )

        _state.update {
            it.copy(
                artistWithMusics = _selectedArtistWithMusics.value ?: ArtistWithMusics()
            )
        }
    }

    /**
     * Add a music to multiple playlists.
     */
    private fun addMusicToPlaylists(musicId: UUID, selectedPlaylistsIds: List<UUID>) {
        CoroutineScope(Dispatchers.IO).launch {
            for (selectedPlaylistId in selectedPlaylistsIds) {
                upsertMusicIntoPlaylistUseCase(
                    MusicPlaylist(
                        musicId = musicId,
                        playlistId = selectedPlaylistId
                    )
                )
            }
            getMusicUseCase(musicId = musicId).first()?.let { music ->
                playbackManager.updateMusic(music = music)
            }
        }
    }

    /**
     * Remove the selected music, from the MusicState, from the application
     */
    private fun deleteMusicFromApp(musicId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            deleteMusicUseCase(musicId = musicId)
            playbackManager.removeSongFromLists(musicId = musicId)
        }
    }

    /**
     * Toggle the quick access state of the selected music.
     */
    private fun toggleQuickAccessState(music: Music) {
        CoroutineScope(Dispatchers.IO).launch {
            upsertMusicUseCase(
                music = music.copy(
                    isInQuickAccess = !music.isInQuickAccess,
                )
            )
        }
    }

    /**
     * Show or hide the delete dialog.
     */
    private fun showOrHideDeleteDialog(isShown: Boolean) {
        _state.update {
            it.copy(
                isDeleteMusicDialogShown = isShown
            )
        }
    }

    /**
     * Show or hide the add to playlist bottom sheet.
     */
    private fun showOrHideAddToPlaylistBottomSheet(isShown: Boolean) {
        _state.update {
            it.copy(
                isAddToPlaylistBottomSheetShown = isShown
            )
        }
    }

    /**
     * Show or hide the music bottom sheet.
     */
    private fun showOrHideMusicBottomSheet(isShown: Boolean) {
        _state.update {
            it.copy(
                isMusicBottomSheetShown = isShown
            )
        }
    }

    /**
     * Increment by one the number of time an artist was played.
     */
    private fun incrementNbPlayed(artistId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            val artist = _state.value.artistWithMusics.artist
            upsertArtistUseCase(
                artist = artist.copy(
                    nbPlayed = artist.nbPlayed + 1
                )
            )
        }
    }
}