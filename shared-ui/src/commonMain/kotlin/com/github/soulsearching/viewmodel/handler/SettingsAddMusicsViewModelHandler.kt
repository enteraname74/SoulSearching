package com.github.soulsearching.viewmodel.handler

import androidx.compose.ui.graphics.ImageBitmap
import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumArtist
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.Folder
import com.github.enteraname74.domain.model.ImageCover
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.MusicAlbum
import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.FolderRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.soulsearching.events.AddMusicsEvent
import com.github.soulsearching.model.MusicFetcher
import com.github.soulsearching.model.SelectableMusicItem
import com.github.soulsearching.states.AddMusicsState
import com.github.soulsearching.types.AddMusicsStateType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Handler used for managing the AddMusicsViewModel.
 */
open class SettingsAddMusicsViewModelHandler(
    private val folderRepository: FolderRepository,
    private val musicRepository: MusicRepository,
    private val musicFetcher: MusicFetcher,
): ViewModelHandler {
    private var _state = MutableStateFlow(AddMusicsState())
    val state = _state.asStateFlow()

    /**
     * Manage add music events.
     */
    fun onAddMusicEvent(event: AddMusicsEvent) {
        when (event) {
            AddMusicsEvent.ResetState -> {
                _state.update {
                    AddMusicsState(
                        state = if (it.state != AddMusicsStateType.SAVING_MUSICS) AddMusicsStateType.FETCHING_MUSICS else it.state
                    )
                }
            }
            is AddMusicsEvent.AddFetchedMusics -> {
                _state.update {
                    it.copy(
                        fetchedMusics = event.musics,
                        state = AddMusicsStateType.WAITING_FOR_USER_ACTION
                    )
                }
            }
            is AddMusicsEvent.SetState -> {
                _state.update {
                    it.copy(
                        state = event.newState
                    )
                }
            }
            is AddMusicsEvent.SetSelectedMusic -> {
                _state.update { currentState ->
                    currentState.copy(fetchedMusics = currentState.fetchedMusics.map {
                        if (it.music.musicId == event.music.musicId) {
                            it.copy(
                                isSelected = event.isSelected
                            )
                        } else {
                            it.copy()
                        }
                    } as ArrayList<SelectableMusicItem>)
                }
            }
        }
    }

    /**
     * Fetch and add new musics.
     */
    fun fetchAndAddNewMusics(
        updateProgressBar: (Float) -> Unit,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val hiddenFoldersPaths = folderRepository.getAllHiddenFoldersPaths()
            val allMusics = musicRepository.getAllMusicsPaths()

            val newMusics = fetchNewMusics(
                updateProgress = updateProgressBar,
                alreadyPresentMusicsPaths = allMusics,
                hiddenFoldersPaths = hiddenFoldersPaths
            )
            onAddMusicEvent(AddMusicsEvent.AddFetchedMusics(newMusics))
        }
    }

    /**
     * Fetch new musics with folders settings.
     */
    private fun fetchNewMusics(
        updateProgress: (Float) -> Unit,
        alreadyPresentMusicsPaths: List<String>,
        hiddenFoldersPaths: List<String>
    ): ArrayList<SelectableMusicItem> {
        return musicFetcher.fetchMusicsFromSelectedFolders(
            updateProgress = updateProgress,
            alreadyPresentMusicsPaths = alreadyPresentMusicsPaths,
            hiddenFoldersPaths = hiddenFoldersPaths
        )
    }

    /**
     * Persist a music and its cover.
     */
    suspend fun addMusic(musicToAdd: Music, musicCover: ImageBitmap?) = musicFetcher.addMusic(musicToAdd, musicCover)
}