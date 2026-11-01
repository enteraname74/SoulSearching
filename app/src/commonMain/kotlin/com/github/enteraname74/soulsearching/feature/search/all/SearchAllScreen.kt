package com.github.enteraname74.soulsearching.feature.search.all

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.composable.SoulChip
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.albums
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.all
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.artists
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.collections
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.musics
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.playlists
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.search_all
import com.github.enteraname74.soulsearching.coreui.list.LazyRowCompat
import com.github.enteraname74.soulsearching.coreui.navigation.SoulBackHandler
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.LaunchInit
import com.github.enteraname74.soulsearching.coreui.utils.isLargeWindow
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.search.SearchAll
import com.github.enteraname74.soulsearching.feature.search.SearchAllViewManager
import com.github.enteraname74.soulsearching.feature.search.composable.SoulSearchBar
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
internal fun SearchAllScreen(
    state: SearchAllState,
) {
    val isLargeWindow = isLargeWindow()
    var searchText by rememberSaveable {
        mutableStateOf("")
    }
    LaunchInit {
        searchText = state.search
    }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val lazyListState = rememberLazyListState()

    val searchViewManager: SearchAllViewManager = koinInject()
    val playerViewManager: PlayerViewManager = koinInject()

    val containerColor = if (isLargeWindow) {
        SoulSearchingColorTheme.colorScheme.surface
    } else {
        SoulSearchingColorTheme.colorScheme.background
    }

    SoulBackHandler(
        searchViewManager.currentValue == BottomSheetStates.EXPANDED
            && playerViewManager.currentValue != BottomSheetStates.EXPANDED
    ) {
        focusManager.clearFocus()
        state.navigateBack()
    }

    Column(
        modifier = Modifier
            .background(
                color = containerColor
            )
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
            .statusBarsPadding()
    ) {
        SoulSearchBar(
            modifier = Modifier
                .padding(
                    top = UiConstants.Spacing.medium,
                    start = UiConstants.Spacing.medium,
                    end = UiConstants.Spacing.medium,
                ),
            searchText = searchText,
            placeholder = CoreRes.string.search_all.toTextSpec(),
            updateTextMethod = {
                searchText = it
                state.onSearch(it)
                coroutineScope.launch { lazyListState.scrollToItem(0) }
            },
            focusManager = focusManager,
            focusRequester = state.focusRequester,
            onClose = {
                focusManager.clearFocus()
                state.navigateBack()
            }.takeIf { !isLargeWindow }
        )
        if (isLargeWindow) {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(),
                thickness = 1.dp,
                color = SoulSearchingColorTheme.colorScheme.outline,
            )
        }
        SearchableElements(
            state = state,
        )
        SearchAll(
            lazyListState = lazyListState,
            searchAllState = state,
            focusManager = focusManager,
        )
    }
}

@Composable
private fun SearchableElements(
    state: SearchAllState,
) {
    Row(
        modifier = Modifier
            .padding(
                PaddingValues(
                    horizontal = if (isLargeWindow()) {
                        UiConstants.Spacing.large
                    } else {
                        UiConstants.Spacing.medium
                    },
                    vertical = UiConstants.Spacing.mediumPlus
                )
            ),
        horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SoulChip(
            title = CoreRes.string.all.toTextSpec(),
            isSelected = SearchAllState.SearchableElement.entries.all { it in state.selectedSearchableElements },
            onClick = {
                state.onSearchInAllElements()
            },
            baseColors = SoulButtonDefaults.surfaceHigh(),
        )
        LazyRowCompat(
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
        ) {
            items(
                items = SearchAllState.SearchableElement.entries
            ) { element ->
                SoulChip(
                    title = element.title(),
                    isSelected = element in state.selectedSearchableElements,
                    onClick = {
                        state.onToggleSearchableElement(element)
                    },
                    baseColors = SoulButtonDefaults.surfaceHigh(),
                )
            }
        }
    }
}

private fun SearchAllState.SearchableElement.title(): SoulTextSpec =
    when (this) {
        SearchAllState.SearchableElement.Collections -> CoreRes.string.collections.toTextSpec()
        SearchAllState.SearchableElement.Playlists -> CoreRes.string.playlists.toTextSpec()
        SearchAllState.SearchableElement.Artists -> CoreRes.string.artists.toTextSpec()
        SearchAllState.SearchableElement.Albums -> CoreRes.string.albums.toTextSpec()
        SearchAllState.SearchableElement.Musics -> CoreRes.string.musics.toTextSpec()
    }