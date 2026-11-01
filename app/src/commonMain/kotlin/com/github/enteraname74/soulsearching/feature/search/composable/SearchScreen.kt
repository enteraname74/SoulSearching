package com.github.enteraname74.soulsearching.feature.search.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.navigation.SoulBackHandler
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.utils.LaunchInit
import com.github.enteraname74.soulsearching.coreui.utils.isLargeWindow
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.swipeableview.SwipeableViewManager
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun SearchScreen(
    searchBarPlaceholder: SoulTextSpec,
    initialSearch: String,
    onSearch: (String) -> Unit,
    focusRequester: FocusRequester,
    searchViewManager: SwipeableViewManager,
    lazyListState: LazyListState,
    navigateBack: () -> Unit,
    content: @Composable () -> Unit,
) {
    val isLargeWindow = isLargeWindow()
    var searchText by rememberSaveable {
        mutableStateOf("")
    }
    LaunchInit {
        searchText = initialSearch
    }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

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
        navigateBack()
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
            placeholder = searchBarPlaceholder,
            updateTextMethod = {
                searchText = it
                onSearch(it)
                coroutineScope.launch { lazyListState.scrollToItem(0) }
            },
            focusManager = focusManager,
            focusRequester = focusRequester,
            onClose = {
                focusManager.clearFocus()
                navigateBack()
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
        content()
    }
}