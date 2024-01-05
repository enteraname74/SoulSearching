package com.github.soulsearching.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.github.soulsearching.classes.utils.MusicUtils
import com.github.soulsearching.classes.SelectableMusicItem
import com.github.soulsearching.classes.enumsAndTypes.AddMusicsStateType
import com.github.soulsearching.database.dao.FolderDao
import com.github.soulsearching.database.dao.MusicDao
import com.github.soulsearching.events.AddMusicsEvent
import com.github.soulsearching.states.AddMusicsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * View model for adding new musics from the settings..
 */
@HiltViewModel
class AddMusicsViewModel @Inject constructor(
    private val folderDao: FolderDao, private val musicDao: MusicDao
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
     * Retrieve new musics.
     */
    fun fetchNewMusics(
        context: Context,
        updateProgressBar: (Float) -> Unit,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val hiddenFoldersPaths = folderDao.getAllHiddenFoldersPaths()
            val allMusics = musicDao.getAllMusicsPaths()

            val newMusics = MusicUtils.fetchNewMusics(
                context, updateProgressBar, allMusics, hiddenFoldersPaths
            )
            onAddMusicEvent(AddMusicsEvent.AddFetchedMusics(newMusics))
        }
    }
}