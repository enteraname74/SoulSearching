package com.github.enteraname74.soulsearching.feature.playlistdetail.albumpage.presentation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.domain.util.serializer.UUIDSerializer
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.ext.isPreviousScreenAPlaylistDetails
import com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.presentation.ModifyAlbumDestination
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.presentation.ModifyMusicDestination
import com.github.enteraname74.soulsearching.feature.playlistdetail.albumpage.domain.SelectedAlbumNavigationState
import com.github.enteraname74.soulsearching.feature.playlistdetail.artistpage.presentation.SelectedArtistDestination
import com.github.enteraname74.soulsearching.feature.playlistdetail.composable.PlaylistDetailPage
import com.github.enteraname74.soulsearching.navigation.Navigator
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.*

@Serializable
data class SelectedAlbumDestination(
    @Serializable(UUIDSerializer::class)
    val selectedAlbumId: UUID,
): NavKey, PlaylistDetailPage {
    companion object {
        fun register(
            entryProviderScope: EntryProviderScope<NavKey>,
            navigator: Navigator,
        ) {
            entryProviderScope.entry<SelectedAlbumDestination> { key ->
                val colorThemeManager = injectElement<ColorThemeManager>()

                SelectedAlbumRoute(
                    viewModel = koinViewModel {
                        parametersOf(key)
                    },
                    onNavigationState = {
                        when (it) {
                            SelectedAlbumNavigationState.Back -> {
                                if (!navigator.isPreviousScreenAPlaylistDetails()) {
                                    colorThemeManager.removePlaylistTheme()
                                }
                                navigator.goBack()
                            }
                            SelectedAlbumNavigationState.Idle -> {
                                /*no-op*/
                            }
                            is SelectedAlbumNavigationState.ToArtist -> {
                               navigator.navigate(
                                   SelectedArtistDestination(
                                       selectedArtistId = it.artistId,
                                   )
                               )
                            }
                            is SelectedAlbumNavigationState.ToEdit -> {
                                navigator.navigate(
                                    ModifyAlbumDestination(selectedAlbumId = it.albumId)
                                )
                            }
                            is SelectedAlbumNavigationState.ToModifyMusic -> {
                                navigator.navigate(
                                    ModifyMusicDestination(
                                        selectedMusicId = it.musicId,
                                    )
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}
