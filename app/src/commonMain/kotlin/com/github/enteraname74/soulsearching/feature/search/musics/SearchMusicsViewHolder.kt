package com.github.enteraname74.soulsearching.feature.search.musics

import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.viewModelScope
import com.github.enteraname74.soulsearching.domain.model.Music
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.viewholder.SoulViewModelHolderV2
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class SearchMusicsViewHolder(
    private val commonMusicUseCase: CommonMusicUseCase,
    private val params: SearchMusicsDestination,
    private val focusRequester: FocusRequester,
    private val playerViewManager: PlayerViewManager,
    private val playbackManager: PlaybackManager,
) : SoulViewModelHolderV2<SearchMusicsNavScope, SearchMusicsState>() {
    private val search: MutableStateFlow<String> = MutableStateFlow("")

    init {
        viewModelScope.launch {
            search.flatMapLatest { search ->
                updateState { copy(search = search) }

                if (search.isNotEmpty()) {
                    delay(300.milliseconds)
                    commonMusicUseCase.searchFromMusicListDetailId(
                        musicListDetailId = params.musicListDetailId,
                        search = search,
                    ).mapLatest { musics ->
                        currentState.copy(
                            musics = musics
                        )
                    }
                } else {
                    flowOf(
                        currentState.copy(
                            musics = emptyList(),
                        )
                    )
                }
            }.collectLatest { state ->
                updateState { state }
            }
        }
    }

    override fun getInitialState(): SearchMusicsState =
        SearchMusicsState(
            search = search.value,
            musics = emptyList(),
            onSearch = {
                search.value = it
            },
            showMusicBottomSheet = {
                navigate {
                    showMusicBottomSheet(it)
                }
            },
            onMusicClick = ::playSearchList,
            focusRequester = focusRequester,
            navigateBack = {
                navigate { navigateBack() }
            }
        )

    private fun playSearchList(
        music: Music,
    ) {
        viewModelScope.launch {
            playbackManager.setCurrentPlaylistAndMusic(
                music = music,
                musicList = currentState.musics,
                playlistId = null,
                isMainPlaylist = false,
                isForcingNewPlaylist = true
            )
            playerViewManager.animateTo(BottomSheetStates.EXPANDED)
        }
    }

    @Composable
    override fun Content(state: SearchMusicsState) {
        SearchMusicsScreen(state)
    }
}