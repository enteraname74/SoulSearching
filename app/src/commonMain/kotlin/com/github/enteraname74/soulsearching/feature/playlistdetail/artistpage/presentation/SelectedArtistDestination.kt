package com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.presentation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.domain.util.serializer.UUIDSerializer
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.ext.isPreviousScreenAPlaylistDetails
import com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.presentation.ModifyAlbumDestination
import com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.presentation.ModifyArtistDestination
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.presentation.ModifyMusicDestination
import com.github.enteraname74.soulsearching.feature.playlistdetail.albumpage.presentation.SelectedAlbumDestination
import com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.domain.SelectedArtistNavigationState
import com.github.enteraname74.soulsearching.navigation.Navigator
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.*

@Serializable
data class SelectedArtistDestination(
    @Serializable(UUIDSerializer::class)
    val selectedArtistId: UUID
) : NavKey {
    companion object {
        fun register(
            entryProviderScope: EntryProviderScope<NavKey>,
            navigator: Navigator,
        ) {
            entryProviderScope.entry<SelectedArtistDestination> { key ->
                val colorThemeManager: ColorThemeManager = injectElement()

                SelectedArtistRoute(
                    viewModel = koinViewModel {
                        parametersOf(key)
                    },
                    onNavigationState = {
                        when (it) {
                            SelectedArtistNavigationState.Idle -> {
                                /*no-op*/
                            }

                            is SelectedArtistNavigationState.ToAlbum -> {
                                navigator.navigate(
                                    SelectedAlbumDestination(
                                        selectedAlbumId = it.albumId,
                                    )
                                )
                            }

                            is SelectedArtistNavigationState.ToEdit -> {
                                navigator.navigate(
                                    ModifyArtistDestination(
                                        selectedArtistId = it.artistId,
                                    )
                                )
                            }

                            is SelectedArtistNavigationState.ToModifyAlbum -> {
                                navigator.navigate(
                                    ModifyAlbumDestination(
                                        selectedAlbumId = it.albumId,
                                    )
                                )
                            }

                            is SelectedArtistNavigationState.ToModifyMusic -> {
                                navigator.navigate(
                                    ModifyMusicDestination(
                                        selectedMusicId = it.musicId,
                                    )
                                )
                            }
                            SelectedArtistNavigationState.Back -> {
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
