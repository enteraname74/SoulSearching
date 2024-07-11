package com.github.enteraname74.soulsearching.feature.modifyelement.modifymusic.domain

import androidx.compose.ui.graphics.ImageBitmap
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.usecase.album.GetAlbumsNameFromSearchString
import com.github.enteraname74.domain.usecase.artist.GetArtistsNameFromSearchStringUseCase
import com.github.enteraname74.domain.usecase.imagecover.GetCoverOfElementUseCase
import com.github.enteraname74.domain.usecase.imagecover.UpsertImageCoverUseCase
import com.github.enteraname74.domain.usecase.music.GetMusicUseCase
import com.github.enteraname74.domain.usecase.music.UpdateMusicUseCase
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class ModifyMusicViewModel(
    private val playbackManager: PlaybackManager,
    private val getMusicUseCase: GetMusicUseCase,
    private val getCoverOfElementUseCase: GetCoverOfElementUseCase,
    private val getAlbumsNameFromSearchStringUseCase: GetAlbumsNameFromSearchString,
    private val getArtistsNameFromSearchStringUseCase: GetArtistsNameFromSearchStringUseCase,
    private val upsertImageCoverUseCase: UpsertImageCoverUseCase,
    private val updateMusicUseCase: UpdateMusicUseCase,
) : ScreenModel {
    private val _state = MutableStateFlow(ModifyMusicState())
    val state = _state.stateIn(
        screenModelScope,
        SharingStarted.WhileSubscribed(5000),
        ModifyMusicState()
    )

    /**
     * Define the selected music in the state from a given id.
     * It will also fetch the cover of the music.
     */
    fun getMusicFromId(musicId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            val music: Music = getMusicUseCase(musicId).first() ?: return@launch
            val cover: ImageBitmap? = music.coverId?.let { coverId ->
                getCoverOfElementUseCase(
                    coverId = coverId,
                )?.cover
            }
            _state.update {
                it.copy(
                    selectedMusic = music,
                    modifiedMusicInformation = music.copy(),
                    hasCoverBeenChanged = false,
                    cover = cover,
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
                    matchingAlbumsNames = getAlbumsNameFromSearchStringUseCase(
                        searchString = search
                    )
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
                    matchingArtistsNames = getArtistsNameFromSearchStringUseCase(
                        searchString = search
                    )
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
                val imageCover = ImageCover(
                    cover = _state.value.cover!!
                )
                upsertImageCoverUseCase(imageCover = imageCover)
                imageCover.coverId
            } else {
                _state.value.selectedMusic.coverId
            }

            val newMusicInformation = _state.value.modifiedMusicInformation.trim().copy(
                coverId = coverId
            )

            updateMusicUseCase(
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