package com.github.enteraname74.soulsearching.feature.mainpage.presentation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.composables.bottomsheets.album.AlbumBottomSheetDestination
import com.github.enteraname74.soulsearching.composables.bottomsheets.artist.ArtistBottomSheetDestination
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.main.MusicBottomSheetDestination
import com.github.enteraname74.soulsearching.composables.bottomsheets.playlist.PlaylistBottomSheetDestination
import com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.presentation.ModifyAlbumDestination
import com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.presentation.ModifyArtistDestination
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.presentation.ModifyMusicDestination
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.presentation.ModifyPlaylistDestination
import com.github.enteraname74.soulsearching.feature.mainpage.domain.state.MainPageNavigationState
import com.github.enteraname74.soulsearching.feature.musiclistdetail.MusicListDetailDestination
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
                            navigator.push(
                                SettingsAdvancedDestination(
                                    focusedElement = it.focusedElement,
                                )
                            )
                        }

                        is MainPageNavigationState.ToModifyAlbum -> {
                            navigator.push(
                                ModifyAlbumDestination(
                                    selectedAlbumId = it.albumId
                                )
                            )
                        }

                        is MainPageNavigationState.ToModifyArtist -> {
                            navigator.push(
                                ModifyArtistDestination(
                                    selectedArtistId = it.artistId,
                                )
                            )
                        }

                        is MainPageNavigationState.ToModifyMusic -> {
                            navigator.push(
                                ModifyMusicDestination(
                                    selectedMusicId = it.musicId
                                )
                            )
                        }

                        is MainPageNavigationState.ToModifyPlaylist -> {
                            navigator.push(
                                ModifyPlaylistDestination(
                                    selectedPlaylistId = it.playlistId,
                                )
                            )
                        }
                        is MainPageNavigationState.ToMusicListDetail -> {
                            navigator.push(
                                MusicListDetailDestination(it.detailId)
                            )
                        }
                        MainPageNavigationState.ToSettings -> {
                            navigator.push(SettingsDestination)
                        }

                        is MainPageNavigationState.ToMusicBottomSheet -> {
                            navigator.push(
                                MusicBottomSheetDestination(it.musicIds)
                            )
                        }

                        is MainPageNavigationState.ToPlaylistBottomSheet -> {
                            navigator.push(
                                PlaylistBottomSheetDestination(it.playlistIds)
                            )
                        }

                        is MainPageNavigationState.ToAlbumBottomSheet -> {
                            navigator.push(
                                AlbumBottomSheetDestination(it.albumIds)
                            )
                        }
                        is MainPageNavigationState.ToArtistBottomSheet -> {
                            navigator.push(
                                ArtistBottomSheetDestination(it.artistIds)
                            )
                        }
                    }
                }
            )
        }
    }
}
