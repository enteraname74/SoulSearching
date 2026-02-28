package com.github.enteraname74.soulsearching.coreui.multiselection

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_close
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_more_vertical
import com.github.enteraname74.soulsearching.coreui.navigation.SoulBackHandler
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBarDefaults
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarActionSpec
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarColors
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun MultiSelectionScaffold(
    multiSelectionManagerImpl: MultiSelectionManagerImpl,
    topBarColors: TopBarColors = SoulTopBarDefaults.secondary(),
    onCancel: () -> Unit,
    onMore: () -> Unit,
    content: @Composable () -> Unit,
) {

    val state: MultiSelectionState by multiSelectionManagerImpl.state.collectAsState()
    val isMultiSelectionActive by remember {
        derivedStateOf {
            state.selectedIds.isNotEmpty()
        }
    }
    SoulBackHandler(
        enabled = isMultiSelectionActive,
    ) {
        onCancel()
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
                topBarColors = topBarColors,
                onCancel = onCancel,
                onMore = onMore,
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
        leftAction = object: TopBarActionSpec {
            override val icon: DrawableResource = CoreRes.drawable.ic_close
            override val onClick: () -> Unit = onCancel
        },
        rightAction = object: TopBarActionSpec {
            override val icon: DrawableResource = CoreRes.drawable.ic_more_vertical
            override val onClick: () -> Unit = onMore
        }
    )
}