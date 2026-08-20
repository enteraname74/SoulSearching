package com.github.enteraname74.soulsearching.feature.musiclistdetail

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.domain.model.MusicListDetailId
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.ext.isPreviousScreenAPlaylistDetails
import com.github.enteraname74.soulsearching.feature.musiclistdetail.composable.PlaylistDetailPage
import com.github.enteraname74.soulsearching.navigation.Navigator
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data class MusicListDetailDestination(
    val detailId: MusicListDetailId,
) : PlaylistDetailPage {
    companion object {
        fun register(
            entryProviderScope: EntryProviderScope<NavKey>,
            navigator: Navigator,
        ) {
            entryProviderScope.entry<MusicListDetailDestination> { key ->
                val colorThemeManager: ColorThemeManager = injectElement()
                val holder: MusicListDetailViewHolder = koinViewModel {
                    parametersOf(key.detailId)
                }
                holder.Screen(
                    navigation = object : MusicListDetailNavScope {
                        override fun navigateBack() {
                            if (!navigator.isPreviousScreenAPlaylistDetails()) {
                                colorThemeManager.removePlaylistTheme()
                            }
                            navigator.pop()
                        }

                        override fun toDestination(destination: NavKey) {
                            navigator.push(destination)
                        }
                    }
                )
            }
        }
    }
}
