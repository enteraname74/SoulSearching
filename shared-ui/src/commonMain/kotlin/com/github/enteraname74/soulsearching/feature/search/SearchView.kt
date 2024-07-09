package com.github.enteraname74.soulsearching.feature.search

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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.IntOffset
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.soulsearching.composables.SoulSearchingBackHandler
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.search.composable.AppSearchBar
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchView(
    maxHeight: Float,
    draggableState: SwipeableState<BottomSheetStates>,
    playerDraggableState: SwipeableState<BottomSheetStates>,
    placeholder: String,
    primaryColor: Color = SoulSearchingColorTheme.colorScheme.primary,
    secondaryColor: Color = SoulSearchingColorTheme.colorScheme.secondary,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onSecondary,
    focusRequester: FocusRequester,
    searchResult: @Composable (String, FocusManager) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    SoulSearchingBackHandler(
        draggableState.currentValue == BottomSheetStates.EXPANDED
                && playerDraggableState.currentValue != BottomSheetStates.EXPANDED
    ) {
        focusRequester.freeFocus()
        focusManager.clearFocus()
        coroutineScope.launch {
            draggableState.animateTo(
                BottomSheetStates.COLLAPSED,
                tween(UiConstants.AnimationDuration.normal)
            )
        }
    }

    var searchText by rememberSaveable {
        mutableStateOf("")
    }

    if (draggableState.currentValue == BottomSheetStates.COLLAPSED) {
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
                    y = draggableState.offset.value.roundToInt()
                )
            }
            .swipeable(
                state = draggableState,
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
                .padding(UiConstants.Spacing.medium)
        ) {

            AppSearchBar(
                searchText = searchText,
                placeholder = placeholder,
                updateTextMethod = {
                    searchText = it
                },
                focusManager = focusManager,
                primaryColor = secondaryColor,
                textColor = textColor,
                focusRequester = focusRequester
            )

            if (searchText.isNotBlank()) {
                searchResult(searchText, focusManager)
            }
        }
    }
}