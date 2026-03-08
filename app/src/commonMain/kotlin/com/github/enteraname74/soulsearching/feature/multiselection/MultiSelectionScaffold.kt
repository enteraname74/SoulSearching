package com.github.enteraname74.soulsearching.feature.multiselection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_close
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_more_vertical
import com.github.enteraname74.soulsearching.coreui.navigation.SoulBackHandler
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBarDefaults
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarActionSpec
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarColors
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.domain.model.types.BottomSheetStates
import com.github.enteraname74.soulsearching.feature.multiselection.state.MultiSelectionNavigationState
import com.github.enteraname74.soulsearching.feature.multiselection.state.MultiSelectionState
import com.github.enteraname74.soulsearching.feature.player.domain.PlayerUiUtils
import com.github.enteraname74.soulsearching.feature.player.domain.model.PlayerViewManager
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun MultiSelectionScaffold(
    multiSelectionManager: MultiSelectionManager = injectElement(),
    playerViewManager: PlayerViewManager = injectElement(),
    onNavigationState: (MultiSelectionNavigationState) -> Unit,
    content: @Composable () -> Unit,
) {
    val state: MultiSelectionState by multiSelectionManager.state.collectAsStateWithLifecycle()
    val navigationState: MultiSelectionNavigationState by multiSelectionManager
        .navigationState.collectAsStateWithLifecycle()

    val isMultiSelectionActive by remember {
        derivedStateOf {
            state.selectedIds.isNotEmpty()
        }
    }
    LaunchedEffect(navigationState) {
        onNavigationState(navigationState)
        multiSelectionManager.consumeNavigation()
    }

    SoulBackHandler(
        enabled = isMultiSelectionActive,
    ) {
        multiSelectionManager.clearMultiSelection()
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        content()
        AnimatedVisibility(
            visible = isMultiSelectionActive,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            MultiSelectionTopBar(
                total = state.selectedIds.size,
                topBarColors = if (
                    !PlayerUiUtils.canShowSidePanel()
                    && playerViewManager.currentValue == BottomSheetStates.EXPANDED
                ) {
                    SoulTopBarDefaults.primary()
                } else {
                    SoulTopBarDefaults.secondary()
                },
                onCancel = multiSelectionManager::clearMultiSelection,
                onMore = multiSelectionManager::showBottomSheet,
            )
        }
    }
}

@Composable
private fun MultiSelectionTopBar(
    total: Int,
    topBarColors: TopBarColors,
    onCancel: () -> Unit,
    onMore: () -> Unit,
) {
    SoulTopBar(
        colors = topBarColors,
        title = strings.selectedElements(total = total),
        leftAction = object : TopBarActionSpec {
            override val icon: DrawableResource = CoreRes.drawable.ic_close
            override val onClick: () -> Unit = onCancel
        },
        rightAction = object : TopBarActionSpec {
            override val icon: DrawableResource = CoreRes.drawable.ic_more_vertical
            override val onClick: () -> Unit = onMore
        }
    )
}