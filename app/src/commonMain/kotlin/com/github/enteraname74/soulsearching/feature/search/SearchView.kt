package com.github.enteraname74.soulsearching.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.IntOffset
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.navigation.SoulBackHandler
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.ext.swipeableView
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import com.github.enteraname74.soulsearching.feature.search.composable.SoulSearchBar
import com.github.enteraname74.soulsearching.feature.swipeableview.SwipeableViewManager
import com.github.enteraname74.soulsearching.feature.swipeableview.SwipeableViewManagerHandler
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchView(
    maxHeight: Float,
    searchViewManager: SwipeableViewManager,
    placeholder: String,
    primaryColor: Color = SoulSearchingColorTheme.colorScheme.primary,
    focusRequester: FocusRequester,
    onSearch: (String) -> Unit,
    playerViewManager: PlayerViewManager = injectElement(),
    searchResult: @Composable (FocusManager, LazyListState) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val lazyListState = rememberLazyListState()

    SwipeableViewManagerHandler(
        swipeableViewManager = searchViewManager,
    )

    SoulBackHandler(
        searchViewManager.currentValue == BottomSheetStates.EXPANDED
            && playerViewManager.currentValue != BottomSheetStates.EXPANDED
    ) {
        focusRequester.freeFocus()
        focusManager.clearFocus()
        searchViewManager.animateTo(BottomSheetStates.COLLAPSED)
    }

    var searchText by rememberSaveable {
        mutableStateOf("")
    }

    if (searchViewManager.currentValue == BottomSheetStates.COLLAPSED) {
        searchText = ""
        SideEffect {
            focusRequester.freeFocus()
            focusManager.clearFocus()
        }
    }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = 0,
                    y = searchViewManager.offset.roundToInt()
                )
            }
            .swipeableView(
                swipeableViewManager = searchViewManager,
                anchors = mapOf(
                    maxHeight to BottomSheetStates.COLLAPSED,
                    0f to BottomSheetStates.EXPANDED,
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(primaryColor)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
                .statusBarsPadding()
                .padding(UiConstants.Spacing.medium)
        ) {

            SoulSearchBar(
                searchText = searchText,
                placeholder = placeholder,
                updateTextMethod = {
                    searchText = it
                    onSearch(it)
                    coroutineScope.launch { lazyListState.scrollToItem(0) }
                },
                focusManager = focusManager,
                focusRequester = focusRequester,
                onClose = {
                    focusRequester.freeFocus()
                    focusManager.clearFocus()
                    searchViewManager.animateTo(BottomSheetStates.COLLAPSED)
                }
            )

            if (searchText.isNotBlank()) {
                searchResult(focusManager, lazyListState)
            }
        }
    }
}
