package com.github.enteraname74.soulsearching.feature.search.musics

import androidx.compose.ui.focus.FocusRequester
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.main.MusicBottomSheetDestination
import com.github.enteraname74.soulsearching.di.DiConstants
import com.github.enteraname74.soulsearching.domain.model.MusicListDetailId
import com.github.enteraname74.soulsearching.feature.search.composable.SearchAdaptiveView
import com.github.enteraname74.soulsearching.navigation.Navigator
import com.github.enteraname74.soulsearching.navigation.OverlaySceneStrategy
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import kotlin.uuid.Uuid

@Serializable
data class SearchMusicsDestination(
    val musicListDetailId: MusicListDetailId,
) : NavKey {
    companion object {
        fun register(
            entryProviderScope: EntryProviderScope<NavKey>,
            navigator: Navigator,
        ) {
            entryProviderScope.entry<SearchMusicsDestination>(
                metadata = OverlaySceneStrategy.overlay(),
            ) { params ->
                val holder: SearchMusicsViewHolder = koinViewModel {
                    parametersOf(params)
                }
                val focusRequester: FocusRequester = koinInject(named(DiConstants.SearchMusicsFocusRequester))
                val swipeableViewManager: PlaylistSearchViewManager = koinInject()

                SearchAdaptiveView(
                    swipeableViewManager = swipeableViewManager,
                    focusRequester = focusRequester,
                    dismiss = navigator::pop,
                ) { onBack ->
                    holder.Screen(
                        navigation = object : SearchMusicsNavScope {
                            override fun navigateBack() {
                                onBack()
                            }

                            override fun showMusicBottomSheet(musicId: Uuid) {
                                navigator.push(MusicBottomSheetDestination(listOf(musicId)))
                            }
                        }
                    )
                }
            }
        }
    }
}
