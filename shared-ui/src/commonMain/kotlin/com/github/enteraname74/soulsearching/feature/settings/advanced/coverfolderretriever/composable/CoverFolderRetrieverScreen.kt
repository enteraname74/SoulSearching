package com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.github.enteraname74.domain.model.CoverFolderRetriever
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuSwitch
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.CoverFolderRetrieverActions
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.CoverFolderRetrieverState
import com.github.enteraname74.soulsearching.feature.settings.advanced.coverfolderretriever.CoverFolderRetrieverUi
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage

@Composable
fun CoverFolderRetrieverScreen(
    navigateBack: () -> Unit,
    ui: CoverFolderRetrieverUi,
    state: CoverFolderRetrieverState,
    actions: CoverFolderRetrieverActions,
) {
    SettingPage(
        navigateBack = navigateBack,
        title = ui.title,
    ) {
        item {
            SoulMenuSwitch(
                title = ui.activateText,
                isChecked = state.coverFolderRetriever.isActivated,
                toggleAction = actions::onToggleActivation,
            )
        }
        item {
            AnimatedVisibility(
                visible = state.coverFolderRetriever.isActivated,
            ) {
                Column {
                    CoverFolderRetrieverRules(
                        actions = actions,
                        coverFolderRetriever = state.coverFolderRetriever,
                        title = ui.dynamicNameTitle,
                    )
                    when (state.coverFolderRetriever.mode) {
                        CoverFolderRetriever.DynamicMode.Folder -> {
                            CoverFolderRetrieverFolderMode(
                                actions = actions,
                                coverFolderRetriever = state.coverFolderRetriever,
                            )
                        }
                        CoverFolderRetriever.DynamicMode.File -> TODO()
                    }
                }
            }
        }
    }
}