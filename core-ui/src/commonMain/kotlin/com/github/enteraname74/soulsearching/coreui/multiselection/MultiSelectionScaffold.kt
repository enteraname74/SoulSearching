package com.github.enteraname74.soulsearching.coreui.multiselection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.enteraname74.soulsearching.coreui.navigation.SoulBackHandler
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarActionSpec

@Composable
fun MultiSelectionScaffold(
    multiSelectionManager: MultiSelectionManager,
    onCancel: () -> Unit,
    onMore: () -> Unit,
    content: @Composable () -> Unit,
) {

    val state: MultiSelectionState by multiSelectionManager.state.collectAsState()
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
            enter = slideInVertically(),
            exit = slideOutVertically()
        ) {
            MultiSelectionTopBar(
                total = state.selectedIds.size,
                onCancel = onCancel,
                onMore = onMore,
            )
        }
    }
}

@Composable
private fun MultiSelectionTopBar(
    total: Int,
    onCancel: () -> Unit,
    onMore: () -> Unit,
) {
    SoulTopBar(
        isElevated = true,
        title = strings.selectedElements(total = total),
        leftAction = object: TopBarActionSpec {
            override val icon: ImageVector = Icons.Rounded.Close
            override val onClick: () -> Unit = onCancel
        },
        rightAction = object: TopBarActionSpec {
            override val icon: ImageVector = Icons.Rounded.MoreVert
            override val onClick: () -> Unit = onMore
        }
    )
}