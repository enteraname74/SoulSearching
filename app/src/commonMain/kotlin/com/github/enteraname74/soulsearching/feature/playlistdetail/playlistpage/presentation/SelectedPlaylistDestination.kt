package com.github.enteraname74.soulsearching.feature.playlistdetail.playlistpage.presentation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.domain.util.serializer.UUIDSerializer
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.ext.isPreviousScreenAPlaylistDetails
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.presentation.ModifyMusicDestination
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.presentation.ModifyPlaylistDestination
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.PlaylistDetailPage
import com.github.enteraname74.soulsearching.feature.playlistdetail.playlistpage.domain.SelectedPlaylistNavigationState
import com.github.enteraname74.soulsearching.navigation.Navigator
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

@Serializable
data class SelectedPlaylistDestination(
    @Serializable(UUIDSerializer::class)
    val selectedPlaylistId: UUID
) : PlaylistDetailPage {
    companion object {
        fun register(
            entryProviderScope: EntryProviderScope<NavKey>,
            navigator: Navigator,
        ) {
            entryProviderScope.entry<SelectedPlaylistDestination> { key ->
                val colorThemeManager: ColorThemeManager = injectElement()

                SelectedPlaylistRoute(
                    viewModel = koinViewModel {
                        parametersOf(key)
                    },
                    onNavigationState = {
                        when (it) {
                            SelectedPlaylistNavigationState.Idle -> {
                                /*no-op*/
                            }

                            is SelectedPlaylistNavigationState.ToEdit -> {
                                navigator.navigate(
                                    ModifyPlaylistDestination(
                                        selectedPlaylistId = it.playlistId,
                                    )
                                )
                            }

                            is SelectedPlaylistNavigationState.ToModifyMusic -> {
                                navigator.navigate(
                                    ModifyMusicDestination(
                                        selectedMusicId = it.musicId,
                                    )
                                )
                            }

                            SelectedPlaylistNavigationState.Back -> {
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
