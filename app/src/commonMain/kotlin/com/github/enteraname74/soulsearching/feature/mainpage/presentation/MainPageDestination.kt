package com.github.enteraname74.soulsearching.feature.mainpage.presentation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.presentation.ModifyAlbumDestination
import com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.presentation.ModifyArtistDestination
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.presentation.ModifyMusicDestination
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.presentation.ModifyPlaylistDestination
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.MainPageNavigationState
import com.github.enteraname74.soulsearching.feature.playlistdetail.albumpage.presentation.SelectedAlbumDestination
import com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.presentation.SelectedArtistDestination
import com.github.enteraname74.soulsearching.feature.playlistdetail.folderpage.presentation.SelectedFolderDestination
import com.github.enteraname74.soulsearching.feature.playlistdetail.monthpage.presentation.SelectedMonthDestination
import com.github.enteraname74.soulsearching.feature.playlistdetail.playlistpage.presentation.SelectedPlaylistDestination
import com.github.enteraname74.soulsearching.feature.settings.advanced.SettingsAdvancedDestination
import com.github.enteraname74.soulsearching.feature.settings.presentation.SettingsDestination
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object MainPageDestination : NavKey {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        entryProviderScope.entry<MainPageDestination> {
            MainPageRoute(
                onNavigation = {
                    when (it) {
                        MainPageNavigationState.Idle -> {
                            /*no-op*/
                        }

                        is MainPageNavigationState.ToAdvancedSettings -> {
                            navigator.navigate(
                                SettingsAdvancedDestination(
                                    focusedElement = it.focusedElement,
                                )
                            )
                        }

                        is MainPageNavigationState.ToAlbum -> {
                            navigator.navigate(
                                SelectedAlbumDestination(
                                    selectedAlbumId = it.albumId
                                )
                            )
                        }

                        is MainPageNavigationState.ToArtist -> {
                            navigator.navigate(
                                SelectedArtistDestination(
                                    selectedArtistId = it.artistId,
                                )
                            )
                        }

                        is MainPageNavigationState.ToFolder -> {
                            navigator.navigate(
                                SelectedFolderDestination(
                                    selectedFolderPath = it.folderPath,
                                )
                            )
                        }

                        is MainPageNavigationState.ToModifyAlbum -> {
                            navigator.navigate(
                                ModifyAlbumDestination(
                                    selectedAlbumId = it.albumId
                                )
                            )
                        }

                        is MainPageNavigationState.ToModifyArtist -> {
                            navigator.navigate(
                                ModifyArtistDestination(
                                    selectedArtistId = it.artistId,
                                )
                            )
                        }

                        is MainPageNavigationState.ToModifyMusic -> {
                            navigator.navigate(
                                ModifyMusicDestination(
                                    selectedMusicId = it.musicId
                                )
                            )
                        }

                        is MainPageNavigationState.ToModifyPlaylist -> {
                            navigator.navigate(
                                ModifyPlaylistDestination(
                                    selectedPlaylistId = it.playlistId,
                                )
                            )
                        }
                        is MainPageNavigationState.ToMonth -> {
                            navigator.navigate(
                                SelectedMonthDestination(
                                    month = it.month
                                )
                            )
                        }
                        is MainPageNavigationState.ToPlaylist -> {
                            navigator.navigate(
                                SelectedPlaylistDestination(
                                    selectedPlaylistId = it.playlistId,
                                )
                            )
                        }
                        MainPageNavigationState.ToSettings -> {
                            navigator.navigate(SettingsDestination)
                        }
                    }
                }
            )
        }
    }
}