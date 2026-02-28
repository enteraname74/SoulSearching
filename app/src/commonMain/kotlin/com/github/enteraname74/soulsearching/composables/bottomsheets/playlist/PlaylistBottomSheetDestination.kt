package com.github.enteraname74.soulsearching.composables.bottomsheets.playlist

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.domain.util.serializer.UUIDListSerializer
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetDestination
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.presentation.ModifyPlaylistDestination
import com.github.enteraname74.soulsearching.navigation.BottomSheetSceneStrategy
import com.github.enteraname74.soulsearching.navigation.LocalBottomSheetCloseWithAnimAction
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

@Serializable
data class PlaylistBottomSheetDestination(
    @Serializable(UUIDListSerializer::class)
    val playlistIds: List<UUID>,
) : BottomSheetDestination {
    companion object {
        fun register(
            entryProviderScope: EntryProviderScope<NavKey>,
            navigator: Navigator,
        ) {
            entryProviderScope.entry<PlaylistBottomSheetDestination>(
                metadata = BottomSheetSceneStrategy.bottomSheet()
            ) { params ->
                val closeWithAnim = LocalBottomSheetCloseWithAnimAction.current

                val navScope = object : PlaylistBottomSheetNavScope {
                    override val navigateBack: () -> Unit = { closeWithAnim { } }
                    override val toModifyPlaylist: (playlistId: UUID) -> Unit = {
                        closeWithAnim {
                            navigator.push(ModifyPlaylistDestination(it))
                        }
                    }
                }

                val viewModel: PlaylistBottomSheetViewModel = koinViewModel {
                    parametersOf(navScope, params)
                }
                PlaylistBottomSheetScreen(
                    viewModel = viewModel,
                )
            }
        }
    }
}
