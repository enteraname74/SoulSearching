package com.github.enteraname74.soulsearching.feature.search.musics

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalFocusManager
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.search_for_musics
import com.github.enteraname74.soulsearching.coreui.navigation.SoulBackHandler
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.search.composable.SearchScreen
import com.github.enteraname74.soulsearching.feature.search.musics.composable.SearchMusics
import org.koin.compose.koinInject

@Composable
internal fun SearchMusicsScreen(
    state: SearchMusicsState,
) {
    val focusManager = LocalFocusManager.current
    val lazyListState = rememberLazyListState()

    val searchViewManager: PlaylistSearchViewManager = koinInject()
    val playerViewManager: PlayerViewManager = koinInject()


    SoulBackHandler(
        searchViewManager.currentValue == BottomSheetStates.EXPANDED
            && playerViewManager.currentValue != BottomSheetStates.EXPANDED
    ) {
        focusManager.clearFocus()
        state.navigateBack()
    }
    SearchScreen(
        lazyListState = lazyListState,
        searchBarPlaceholder = CoreRes.string.search_for_musics.toTextSpec(),
        initialSearch = state.search,
        onSearch = state.onSearch,
        focusRequester = state.focusRequester,
        searchViewManager = searchViewManager,
        navigateBack = state.navigateBack,
    ) {
        SearchMusics(
            lazyListState = lazyListState,
            state = state,
            focusManager = focusManager,
        )
    }
}