package com.github.soulsearching.composables.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.IntOffset
import com.github.soulsearching.Constants
import com.github.soulsearching.classes.draggablestates.PlayerDraggableState
import com.github.soulsearching.classes.draggablestates.SearchDraggableState
import com.github.soulsearching.classes.types.BottomSheetStates
import com.github.soulsearching.ui.theme.DynamicColor
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchView(
    draggableState: SearchDraggableState,
    playerDraggableState: PlayerDraggableState,
    placeholder: String,
    primaryColor: Color = DynamicColor.primary,
    secondaryColor: Color = DynamicColor.secondary,
    textColor: Color = DynamicColor.onSecondary,
    searchResult: @Composable (String, FocusManager) -> Unit

) {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    BackHandler(
        draggableState.state.currentValue == BottomSheetStates.EXPANDED
                && playerDraggableState.state.currentValue != BottomSheetStates.EXPANDED
    ) {
        coroutineScope.launch {
            draggableState.animateTo(BottomSheetStates.COLLAPSED)
        }
    }

    var searchText by rememberSaveable {
        mutableStateOf("")
    }

    if (draggableState.state.currentValue == BottomSheetStates.COLLAPSED) {
        searchText = ""
    }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = 0,
                    y = draggableState.state.offset.roundToInt()
                )
            }
            .anchoredDraggable(
                state = draggableState.state,
                orientation = Orientation.Vertical
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
                .padding(Constants.Spacing.medium)
        ) {

            AppSearchBar(
                searchText = searchText,
                placeholder = placeholder,
                updateTextMethod = {
                    searchText = it
                },
                focusManager = focusManager,
                primaryColor = secondaryColor,
                textColor = textColor
            )

            if (searchText.isNotBlank()) {
                searchResult(searchText, focusManager)
            }
        }
    }
}