package com.github.soulsearching.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.soulsearching.composables.SoulSearchingBackHandler
import com.github.soulsearching.draggablestates.PlayerDraggableState
import com.github.soulsearching.draggablestates.PlayerMusicListDraggableState
import com.github.soulsearching.types.BottomSheetStates

/**
 * Navigation host used to show the current screen.
 * If a screen isn't found by the navigationController, the first screen is shown.
 * If no screens are given, a default view is shown.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> NavigationHost(
    modifier: Modifier = Modifier.fillMaxSize(),
    navigationController: NavigationController<T>,
    screens: List<Screen<T>>,
    playerDraggableState: PlayerDraggableState,
    playerMusicListDraggableState: PlayerMusicListDraggableState
) {
    Crossfade(targetState = navigationController.currentRoute) { currentRoute ->
        Scaffold(
            modifier = modifier
        ) {

            SoulSearchingBackHandler(
                enabled = playerDraggableState.state.currentValue != BottomSheetStates.EXPANDED && playerMusicListDraggableState.state.currentValue != BottomSheetStates.EXPANDED
            ) {
                navigationController.navigateBack()
            }

            val screen =
                screens.find { it.screenRoute == currentRoute.route }?.screen ?: return@Scaffold
            screen()
        }
    }
}