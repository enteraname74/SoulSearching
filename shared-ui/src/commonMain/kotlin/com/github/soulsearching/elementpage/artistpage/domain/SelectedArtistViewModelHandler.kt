package com.github.soulsearching.elementpage.artistpage.domain

import com.github.enteraname74.domain.model.ArtistWithMusics
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.domain.events.ArtistEvent
import com.github.soulsearching.domain.events.MusicEvent
import com.github.soulsearching.domain.events.handlers.MusicEventHandler
import com.github.soulsearching.player.domain.model.PlaybackManager
import com.github.soulsearching.domain.model.settings.SoulSearchingSettings
import com.github.soulsearching.mainpage.domain.state.MusicState
import com.github.soulsearching.domain.viewmodel.handler.ViewModelHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Handler for managing the SelectedArtistViewModel.
 */
class SelectedArtistViewModelHandler(
    private val coroutineScope: CoroutineScope,
    private val artistRepository: ArtistRepository,
    musicRepository: MusicRepository,
    albumRepository: AlbumRepository,
    playlistRepository: PlaylistRepository,
    musicPlaylistRepository: MusicPlaylistRepository,
    albumArtistRepository: AlbumArtistRepository,
    musicAlbumRepository: MusicAlbumRepository,
    musicArtistRepository: MusicArtistRepository,
    imageCoverRepository: ImageCoverRepository,
    settings: SoulSearchingSettings,
    playbackManager: PlaybackManager
) : ViewModelHandler {
    private var _selectedArtistWithMusics: StateFlow<ArtistWithMusics?> = MutableStateFlow(
        ArtistWithMusics()
    )

    private val _selectedArtistState = MutableStateFlow(SelectedArtistState())
    var selectedArtistState: StateFlow<SelectedArtistState> = _selectedArtistState

    private val _musicState = MutableStateFlow(MusicState())
    var musicState: StateFlow<MusicState> = _musicState

    private val musicEventHandler = MusicEventHandler(
        privateState = _musicState,
        publicState = musicState,
        musicRepository = musicRepository,
        playlistRepository = playlistRepository,
        albumRepository = albumRepository,
        artistRepository = artistRepository,
        musicPlaylistRepository = musicPlaylistRepository,
        musicAlbumRepository = musicAlbumRepository,
        musicArtistRepository = musicArtistRepository,
        albumArtistRepository = albumArtistRepository,
        imageCoverRepository = imageCoverRepository,
        settings = settings,
        playbackManager = playbackManager
    )

    /**
     * Set the selected artist.
     */
    fun setSelectedArtist(artistId: UUID) {
        _selectedArtistWithMusics = artistRepository
            .getArtistWithMusicsAsFlow(artistId = artistId)
            .stateIn(
                coroutineScope, SharingStarted.WhileSubscribed(), ArtistWithMusics()
            )

        selectedArtistState =
            combine(_selectedArtistState, _selectedArtistWithMusics) { state, artist ->
                state.copy(
                    artistWithMusics = artist ?: ArtistWithMusics()
                )
            }.stateIn(
                coroutineScope,
                SharingStarted.WhileSubscribed(5000),
                SelectedArtistState()
            )

        musicState = combine(_musicState, _selectedArtistWithMusics) { state, artist ->
            state.copy(
                musics = if (artist?.musics?.isNotEmpty() == true) {
                    artist.musics.filter { !it.isHidden } as ArrayList<Music>
                } else {
                    ArrayList()
                }
            )
        }.stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(5000),
            MusicState()
        )

        _selectedArtistState.update {
            it.copy(
                artistWithMusics = _selectedArtistWithMusics.value ?: ArtistWithMusics()
            )
        }

        _musicState.update {
            it.copy(
                musics = if (_selectedArtistWithMusics.value?.musics?.isNotEmpty() == true) {
                    _selectedArtistWithMusics.value?.musics as ArrayList<Music>
                } else {
                    ArrayList()
                }
            )
        }
    }

    /**
     * Check if an artist exists.
     */
    suspend fun doesArtistExists(artistId: UUID): Boolean {
        return artistRepository.getArtistFromId(
            artistId
        ) != null
    }

    /**
     * Manage music events.
     */
    fun onMusicEvent(event: MusicEvent) {
        musicEventHandler.handleEvent(event)
    }

    /**
     * Manage artist events.
     */
    fun onArtistEvent(event: ArtistEvent) {
        when (event) {
            is ArtistEvent.AddNbPlayed -> {
                CoroutineScope(Dispatchers.IO).launch {
                    artistRepository.updateNbPlayed(
                        newNbPlayed = artistRepository.getNbPlayedOfArtist(event.artistId) + 1,
                        artistId = event.artistId
                    )
                }
            }

            else -> {}
        }
    }
}