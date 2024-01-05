package com.github.soulsearching.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.soulsearching.classes.utils.EventUtils
import com.github.soulsearching.database.dao.*
import com.github.soulsearching.events.MusicEvent
import com.github.soulsearching.states.MusicState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * View model for the modify music screen.
 */
@HiltViewModel
class ModifyMusicViewModel @Inject constructor(
    private val musicDao: MusicDao,
    private val playlistDao: PlaylistDao,
    private val artistDao: ArtistDao,
    private val albumDao: AlbumDao,
    private val musicPlaylistDao: MusicPlaylistDao,
    private val musicAlbumDao: MusicAlbumDao,
    private val albumArtistDao: AlbumArtistDao,
    private val musicArtistDao: MusicArtistDao,
    private val imageCoverDao: ImageCoverDao
) : ViewModel() {

    private val _state = MutableStateFlow(MusicState())
    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        MusicState()
    )

    /**
     * Define the selected music in the state from a given id.
     * It will also fetch the cover of the music.
     */
    fun getMusicFromId(musicId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            val music = musicDao.getMusicFromId(musicId)
            val cover = if (music.coverId != null) {
                imageCoverDao.getCoverOfElement(music.coverId!!)
            } else {
                null
            }
            _state.update {
                it.copy(
                    selectedMusic = music,
                    name = music.name,
                    album = music.album,
                    artist = music.artist,
                    hasCoverBeenChanged = false,
                    cover = cover?.cover
                )
            }
        }
    }

    /**
     * Manage music events.
     */
    fun onMusicEvent(event: MusicEvent) {
        EventUtils.onMusicEvent(
            event = event,
            _state = _state,
            state = state,
            musicDao = musicDao,
            playlistDao = playlistDao,
            albumDao = albumDao,
            artistDao = artistDao,
            musicPlaylistDao = musicPlaylistDao,
            musicAlbumDao = musicAlbumDao,
            musicArtistDao = musicArtistDao,
            albumArtistDao = albumArtistDao,
            imageCoverDao = imageCoverDao
        )
    }
}