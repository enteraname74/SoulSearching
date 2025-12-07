package com.github.enteraname74.soulsearching.feature.playlistdetail.folderpage.presentation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.ext.isPreviousScreenAPlaylistDetails
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.presentation.ModifyMusicDestination
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.PlaylistDetailPage
import com.github.enteraname74.soulsearching.feature.playlistdetail.folderpage.domain.SelectedFolderNavigationState
import com.github.enteraname74.soulsearching.navigation.Navigator
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data class SelectedFolderDestination(
    val selectedFolderPath: String,
) : NavKey, PlaylistDetailPage {
    companion object {
        fun register(
            entryProviderScope: EntryProviderScope<NavKey>,
            navigator: Navigator,
        ) {
            entryProviderScope.entry<SelectedFolderDestination> { key ->
                val colorThemeManager: ColorThemeManager = injectElement()

                SelectedFolderRoute(
                    viewModel = koinViewModel {
                        parametersOf(key)
                    },
                    onNavigationState = {
                        when (it) {
                            SelectedFolderNavigationState.Idle -> {
                                /*no-op*/
                            }

                            is SelectedFolderNavigationState.ToModifyMusic -> {
                                navigator.navigate(
                                    ModifyMusicDestination(
                                        selectedMusicId = it.musicId,
                                    )
                                )
                            }

                            SelectedFolderNavigationState.Back -> {
                                if (!navigator.isPreviousScreenAPlaylistDetails()) {
                                    colorThemeManager.removePlaylistTheme()
                                }
                                navigator.goBack()
                            }
                        }
                    }
                )
            }
        }
    }
}
