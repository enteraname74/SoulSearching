package com.github.enteraname74.soulsearching.composables.bottomsheets.music.addtoplaylist

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetDestination
import com.github.enteraname74.soulsearching.navigation.BottomSheetSceneStrategy
import com.github.enteraname74.soulsearching.navigation.LocalBottomSheetCloseWithAnimAction
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

@Serializable
data class AddToPlaylistBottomSheetDestination(
    val selectedMusicIds: List<Uuid>,
): NavKey {
    companion object {
        fun register(
            entryProviderScope: EntryProviderScope<NavKey>,
            navScope: AddToPlaylistBottomSheetNavScope,
        ) {
            entryProviderScope.entry<AddToPlaylistBottomSheetDestination> { params ->
                val viewModel: AddToPlaylistBottomSheetViewModel = koinViewModel {
                    parametersOf(navScope, params)
                }
                AddToPlaylistBottomSheetScreen(
                    viewModel = viewModel,
                )
            }
        }
    }
}
