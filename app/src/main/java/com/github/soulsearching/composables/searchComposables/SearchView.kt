package com.github.soulsearching.composables.searchComposables

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.IntOffset
import com.github.soulsearching.Constants
import com.github.soulsearching.classes.enumsAndTypes.BottomSheetStates
import com.github.soulsearching.ui.theme.DynamicColor
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchView(
    swipeableState: SwipeableState<BottomSheetStates>,
    playerSwipeableState: SwipeableState<BottomSheetStates>,
    maxHeight: Float,
    placeholder: String,
    primaryColor: Color = DynamicColor.primary,
    secondaryColor: Color = DynamicColor.secondary,
    textColor: Color = DynamicColor.onSecondary,
    searchResult: @Composable ((String, FocusManager) -> Unit)

    ) {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    BackHandler(
        swipeableState.currentValue == BottomSheetStates.EXPANDED
                && playerSwipeableState.currentValue != BottomSheetStates.EXPANDED
    ) {
        coroutineScope.launch {
            swipeableState.animateTo(BottomSheetStates.COLLAPSED, tween(Constants.AnimationTime.normal))
        }
    }

    var searchText by rememberSaveable {
        mutableStateOf("")
    }

    if (swipeableState.currentValue == BottomSheetStates.COLLAPSED) {
        searchText = ""
    }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = 0,
                    y = swipeableState.offset.value.roundToInt()
                )
            }
            .swipeable(
                state = swipeableState,
                orientation = Orientation.Vertical,
                anchors = mapOf(
                    maxHeight to BottomSheetStates.COLLAPSED,
                    0f to BottomSheetStates.EXPANDED
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