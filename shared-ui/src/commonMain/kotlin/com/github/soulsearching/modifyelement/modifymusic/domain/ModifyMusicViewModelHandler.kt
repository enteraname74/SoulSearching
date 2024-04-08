package com.github.soulsearching.modifyelement.modifymusic.domain

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.service.ImageCoverService
import com.github.enteraname74.domain.service.MusicService
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
    private val imageCoverRepository: ImageCoverRepository,
    private val musicService: MusicService,
    private val imageCoverService: ImageCoverService,
    settings: SoulSearchingSettings,
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
                    isSelectedMusicFetched = true,
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
            ModifyMusicEvent.UpdateMusic -> updateMusic()
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
     * Update selected music information.
     */
    private fun updateMusic() {
        CoroutineScope(Dispatchers.IO).launch {
            val coverId = if (_state.value.hasCoverBeenChanged && _state.value.cover != null) {
                imageCoverService.save(cover = _state.value.cover!!)
            } else {
                _state.value.selectedMusic.coverId
            }

            val newMusicInformation = _state.value.modifiedMusicInformation.copy(
                coverId = coverId
            )

            musicService.update(
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