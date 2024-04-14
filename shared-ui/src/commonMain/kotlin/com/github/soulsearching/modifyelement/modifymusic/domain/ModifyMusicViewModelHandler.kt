package com.github.soulsearching.modifyelement.modifymusic.domain

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.soulsearching.domain.viewmodel.handler.ViewModelHandler
import com.github.soulsearching.player.domain.model.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Handler for managing the ModifyMusicViewModel.
 */
class ModifyMusicViewModelHandler(
    coroutineScope: CoroutineScope,
    private val musicRepository: MusicRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val imageCoverRepository: ImageCoverRepository,
    private val playbackManager: PlaybackManager
) : ViewModelHandler {
    private val _state = MutableStateFlow(ModifyMusicState())
    val state = _state.stateIn(
        coroutineScope,
        SharingStarted.WhileSubscribed(5000),
        ModifyMusicState()
    )

    /**
     * Define the selected music in the state from a given id.
     * It will also fetch the cover of the music.
     */
    fun getMusicFromId(musicId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            val music = musicRepository.getMusicFromId(musicId)
            val cover = if (music.coverId != null) {
                imageCoverRepository.getCoverOfElement(music.coverId!!)
            } else {
                null
            }
            _state.update {
                it.copy(
                    selectedMusic = music,
                    modifiedMusicInformation = music.copy(),
                    hasCoverBeenChanged = false,
                    cover = cover?.cover
                )
            }
        }
    }

    /**
     * Manage music events.
     */
    fun onEvent(event: ModifyMusicEvent) {
        when(event) {
            is ModifyMusicEvent.SetAlbum -> setAlbum(albumName = event.album)
            is ModifyMusicEvent.SetArtist -> setArtist(artistName = event.artist)
            is ModifyMusicEvent.SetCover -> setCover(cover = event.cover)
            is ModifyMusicEvent.SetName -> setName(name = event.name)
            is ModifyMusicEvent.SetMatchingAlbums -> setMatchingAlbums(search = event.search)
            is ModifyMusicEvent.SetMatchingArtists -> setMatchingArtists(search = event.search)
            ModifyMusicEvent.UpdateMusic -> updateMusic()
        }
    }

    /**
     * Set a list of matching albums names to use when modifying the album field of a music.
     */
    private fun setMatchingAlbums(search: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _state.update {
                it.copy(
                    matchingAlbumsNames = albumRepository.getAlbumsNameFromSearch(search = search)
                )
            }
        }
    }

    /**
     * Set a list of matching artists names to use when modifying the artist field of a music.
     */
    private fun setMatchingArtists(search: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _state.update {
                it.copy(
                    matchingArtistsNames = artistRepository.getArtistsNameFromSearch(search = search)
                )
            }
        }
    }

    /**
     * Set the cover of the modified music.
     */
    private fun setCover(cover: ImageBitmap) {
        _state.update {
            it.copy(
                cover = cover,
                hasCoverBeenChanged = true
            )
        }
    }

    /**
     * Set the name of the modified music.
     */
    private fun setName(name: String) {
        _state.update {
            it.copy(
                modifiedMusicInformation = it.modifiedMusicInformation.copy(
                    name = name
                )
            )
        }
    }
    /**
     * Set the artist of the modified music.
     */
    private fun setArtist(artistName: String) {
        _state.update {
            it.copy(
                modifiedMusicInformation = it.modifiedMusicInformation.copy(
                    artist = artistName
                )
            )
        }
    }

    /**
     * Set the album of the modified music.
     */
    private fun setAlbum(albumName: String) {
        _state.update {
            it.copy(
                modifiedMusicInformation = it.modifiedMusicInformation.copy(
                    album = albumName
                )
            )
        }
    }

    /**
     * Utility function for removing leading and trailing whitespaces in a Music when modifying its information.
     */
    private fun Music.trim(): Music = this.copy(
        name = this.name.trim(),
        album = this.album.trim(),
        artist = this.artist.trim()
    )

    /**
     * Update selected music information.
     */
    private fun updateMusic() {
        CoroutineScope(Dispatchers.IO).launch {
            val coverId = if (_state.value.hasCoverBeenChanged && _state.value.cover != null) {
                imageCoverRepository.save(cover = _state.value.cover!!)
            } else {
                _state.value.selectedMusic.coverId
            }

            val newMusicInformation = _state.value.modifiedMusicInformation.trim().copy(
                coverId = coverId
            )

            musicRepository.update(
                legacyMusic = _state.value.selectedMusic,
                newMusicInformation = newMusicInformation
            )

            playbackManager.updateMusic(newMusicInformation)
            playbackManager.currentMusic?.let {
                if (it.musicId.compareTo(newMusicInformation.musicId) == 0) {
                    playbackManager.updateCover(
                        cover = _state.value.cover
                    )
                }
            }
        }
    }
}