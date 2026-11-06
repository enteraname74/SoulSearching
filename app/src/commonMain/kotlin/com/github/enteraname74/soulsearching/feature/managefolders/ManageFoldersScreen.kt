package com.github.enteraname74.soulsearching.feature.managefolders

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.composable.SoulPlayerSpacer
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.used_folders_title
import com.github.enteraname74.soulsearching.coreui.list.LazyColumnCompat
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuSwitch
import com.github.enteraname74.soulsearching.coreui.navigation.SoulBackHandler
import com.github.enteraname74.soulsearching.coreui.screen.SoulScreen
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.topbar.SoulAdaptiveTopBar
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBarAction

@Composable
fun ManageFoldersScreen(
    state: ManageFoldersState,
) {
    SoulBackHandler {
        state.navigateBack?.invoke()
    }
    SoulScreen {
        Column {
            SoulAdaptiveTopBar(
                title = CoreRes.string.used_folders_title.toTextSpec(),
                leftAction = state.navigateBack?.let {
                    SoulTopBarAction.navigation(onClick = it)
                },
                rightAction = SoulTopBarAction.validate(onClick = state.onDone),
            )
            if (state.folders.isNotEmpty()) {
                LazyColumnCompat(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    items(
                        items = state.folders,
                        key = { it.folderPath },
                        contentType = { USED_FOLDERS_CONTENT_TYPE }
                    ) {
                        SoulMenuSwitch(
                            modifier = Modifier
                                .animateItem(),
                            title = it.name.toTextSpec(),
                            subTitle = it.folderPath.toTextSpec(),
                            toggleAction = {
                                state.setStatus(it, !it.isSelected)
                            },
                            isChecked = it.isSelected,
                            padding = PaddingValues(
                                horizontal = UiConstants.Spacing.large,
                                vertical = UiConstants.Spacing.large,
                            ),
                        )
                    }
                    item(
                        key = USED_FOLDERS_SPACER_KEY,
                        contentType = USED_FOLDERS_SPACER_CONTENT_TYPE,
                    ) {
                        SoulPlayerSpacer()
                    }
                }
            }
        }
    }
}

private const val USED_FOLDERS_CONTENT_TYPE: String = "USED_FOLDERS_CONTENT_TYPE"
private const val USED_FOLDERS_SPACER_KEY: String = "USED_FOLDERS_SPACER_KEY"
private const val USED_FOLDERS_SPACER_CONTENT_TYPE: String = "USED_FOLDERS_SPACER_CONTENT_TYPE"
