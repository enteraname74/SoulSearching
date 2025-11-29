package com.github.enteraname74.soulsearching.feature.playlistdetail.monthpage.presentation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.ext.isPreviousScreenAPlaylistDetails
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.presentation.ModifyMusicDestination
import com.github.enteraname74.soulsearching.feature.playlistdetail.monthpage.domain.SelectedMonthNavigationState
import com.github.enteraname74.soulsearching.navigation.Navigator
import com.github.enteraname74.soulsearching.theme.ColorThemeManager
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data class SelectedMonthDestination(
    val month: String,
) : NavKey {
    companion object {
        fun register(
            entryProviderScope: EntryProviderScope<NavKey>,
            navigator: Navigator,
        ) {
            entryProviderScope.entry<SelectedMonthDestination> { key ->
                val colorThemeManager: ColorThemeManager = injectElement()

                SelectedMonthRoute(
                    viewModel = koinViewModel {
                        parametersOf(key)
                    },
                    onNavigationState = {
                        when (it) {
                            SelectedMonthNavigationState.Idle -> {
                                /*no-op*/
                            }
                            is SelectedMonthNavigationState.ToModifyMusic -> {
                                navigator.navigate(
                                    ModifyMusicDestination(
                                        selectedMusicId = it.musicId,
                                    )
                                )
                            }

                            SelectedMonthNavigationState.Back -> {
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
