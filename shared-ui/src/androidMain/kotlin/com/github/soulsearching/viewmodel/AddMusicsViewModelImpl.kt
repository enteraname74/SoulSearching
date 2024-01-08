package com.github.soulsearching.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.github.enteraname74.domain.repository.AlbumArtistRepository
import com.github.enteraname74.domain.repository.AlbumRepository
import com.github.enteraname74.domain.repository.ArtistRepository
import com.github.enteraname74.domain.repository.FolderRepository
import com.github.enteraname74.domain.repository.ImageCoverRepository
import com.github.enteraname74.domain.repository.MusicAlbumRepository
import com.github.enteraname74.domain.repository.MusicArtistRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlaylistRepository
import com.github.soulsearching.classes.SelectableMusicItem
import com.github.soulsearching.classes.types.AddMusicsStateType
import com.github.soulsearching.classes.utils.MusicFetcher
import com.github.soulsearching.events.AddMusicsEvent
import com.github.soulsearching.states.AddMusicsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * View model for adding new musics from the settings..
 */
class AddMusicsViewModelImpl(
    private val folderRepository: FolderRepository,
    private val musicRepository: MusicRepository,
    private val playlistRepository: PlaylistRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val musicAlbumRepository: MusicAlbumRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val albumArtistRepository: AlbumArtistRepository,
    private val imageCoverRepository: ImageCoverRepository,
) : ViewModel() {
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
        context: Context,
        updateProgressBar: (Float) -> Unit,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val hiddenFoldersPaths = folderRepository.getAllHiddenFoldersPaths()
            val allMusics = musicRepository.getAllMusicsPaths()

            val newMusics = fetchNewMusics(
                context = context,
                updateProgress = updateProgressBar,
                alreadyPresentMusicsPaths = allMusics,
                hiddenFoldersPaths = hiddenFoldersPaths
            )
            onAddMusicEvent(AddMusicsEvent.AddFetchedMusics(newMusics))
        }
    }

    /**
     * Fetch new musics.
     */
    private fun fetchNewMusics(
        context: Context,
        updateProgress: (Float) -> Unit,
        alreadyPresentMusicsPaths: List<String>,
        hiddenFoldersPaths: List<String>
    ) : ArrayList<SelectableMusicItem> {
        val musicFetcher = MusicFetcher(
            context = context,
            musicRepository = musicRepository,
            playlistRepository = playlistRepository,
            albumRepository = albumRepository,
            artistRepository = artistRepository,
            musicAlbumRepository = musicAlbumRepository,
            musicArtistRepository = musicArtistRepository,
            albumArtistRepository = albumArtistRepository,
            imageCoverRepository = imageCoverRepository,
            folderRepository = folderRepository
        )

        return musicFetcher.fetchNewMusics(
            updateProgress = updateProgress,
            alreadyPresentMusicsPaths = alreadyPresentMusicsPaths,
            hiddenFoldersPaths = hiddenFoldersPaths
        )
    }
}