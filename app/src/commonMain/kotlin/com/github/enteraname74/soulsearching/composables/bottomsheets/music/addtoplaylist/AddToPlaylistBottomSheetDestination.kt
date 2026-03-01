package com.github.enteraname74.soulsearching.composables.bottomsheets.music.addtoplaylist

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.domain.util.serializer.UUIDListSerializer
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetDestination
import com.github.enteraname74.soulsearching.navigation.BottomSheetSceneStrategy
import com.github.enteraname74.soulsearching.navigation.LocalBottomSheetCloseWithAnimAction
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

@Serializable
data class AddToPlaylistBottomSheetDestination(
    @Serializable(UUIDListSerializer::class)
    val selectedMusicIds: List<UUID>,
): BottomSheetDestination {
    companion object {
        fun register(
            entryProviderScope: EntryProviderScope<NavKey>,
        ) {
            entryProviderScope.entry<AddToPlaylistBottomSheetDestination>(
                metadata = BottomSheetSceneStrategy.bottomSheet()
            ) { params ->
                val closeWithAnim = LocalBottomSheetCloseWithAnimAction.current
                val navScope = object : AddToPlaylistBottomSheetNavScope {
                    override val close: () -> Unit = {
                        closeWithAnim { }
                    }
                }

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
