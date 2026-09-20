package com.github.enteraname74.soulsearching.feature.musiclistdetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.enteraname74.soulsearching.coreui.screen.SoulErrorScreen
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarNavigationAction
import com.github.enteraname74.soulsearching.feature.multiselection.state.MultiSelectionState
import com.github.enteraname74.soulsearching.feature.search.PlaylistSearchViewManager
import kotlinx.coroutines.flow.StateFlow

@Composable
internal fun MusicListDetailScreen(
    state: MusicListDetailState,
    multiSelectionStateFlow: StateFlow<MultiSelectionState>,
    playlistSearchViewManager: PlaylistSearchViewManager,
) {
    val multiSelectionState by multiSelectionStateFlow.collectAsStateWithLifecycle()

    when (state) {
        is MusicListDetailState.Data -> {
            MusicListDetailDataScreen(
                data = state,
                playlistSearchViewManager = playlistSearchViewManager,
                multiSelectionState = multiSelectionState,
            )
        }
        is MusicListDetailState.Error -> SoulErrorScreen(
            leftAction = TopBarNavigationAction(state.navigateBack),
            text = state.error,
        )
        is MusicListDetailState.Loading ->
            SoulLoadingScreen(
                navigateBack = state.navigateBack,
            )
    }
}