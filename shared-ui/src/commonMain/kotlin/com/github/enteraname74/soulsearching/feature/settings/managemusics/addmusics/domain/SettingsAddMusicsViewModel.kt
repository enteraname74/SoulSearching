package com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain

import androidx.compose.ui.graphics.ImageBitmap
import cafe.adriel.voyager.core.model.ScreenModel
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.repository.FolderRepository
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.usecase.folder.GetHiddenFoldersPathUseCase
import com.github.enteraname74.domain.usecase.music.GetAllMusicUseCase
import com.github.enteraname74.soulsearching.domain.model.MusicFetcher
import com.github.enteraname74.soulsearching.domain.model.SelectableMusicItem
import com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.domain.model.AddMusicsStateType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsAddMusicsViewModel(
    private val musicFetcher: MusicFetcher,
    private val getHiddenFoldersPathUseCase: GetHiddenFoldersPathUseCase,
    private val getAllMusicUseCase: GetAllMusicUseCase,
): ScreenModel {
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
        updateProgressBar: (Float, String?) -> Unit,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val hiddenFoldersPaths: List<String> = getHiddenFoldersPathUseCase()
            val allMusicsPaths: List<String> = getAllMusicUseCase().first().map { it.path }

            val newMusics = fetchNewMusics(
                updateProgress = updateProgressBar,
                alreadyPresentMusicsPaths = allMusicsPaths,
                hiddenFoldersPaths = hiddenFoldersPaths
            )
            onAddMusicEvent(AddMusicsEvent.AddFetchedMusics(newMusics))
        }
    }

    /**
     * Fetch new musics with folders settings.
     */
    private suspend fun fetchNewMusics(
        updateProgress: (Float, String?) -> Unit,
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
    suspend fun addMusic(musicToAdd: Music, musicCover: ImageBitmap?) = musicFetcher.addMusic(musicToAdd)
}