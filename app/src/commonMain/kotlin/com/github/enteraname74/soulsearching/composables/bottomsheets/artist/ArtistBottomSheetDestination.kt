package com.github.enteraname74.soulsearching.composables.bottomsheets.artist

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.domain.util.serializer.UUIDListSerializer
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetDestination
import com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.presentation.ModifyArtistDestination
import com.github.enteraname74.soulsearching.navigation.BottomSheetSceneStrategy
import com.github.enteraname74.soulsearching.navigation.LocalBottomSheetCloseWithAnimAction
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

@Serializable
data class ArtistBottomSheetDestination(
    @Serializable(UUIDListSerializer::class)
    val artistIds: List<UUID>,
) : BottomSheetDestination {
    companion object {
        fun register(
            entryProviderScope: EntryProviderScope<NavKey>,
            navigator: Navigator,
        ) {
            entryProviderScope.entry<ArtistBottomSheetDestination>(
                metadata = BottomSheetSceneStrategy.bottomSheet()
            ) { params ->
                val closeWithAnim = LocalBottomSheetCloseWithAnimAction.current

                val navScope = object : ArtistBottomSheetNavScope {
                    override val navigateBack: () -> Unit = { closeWithAnim { } }
                    override val toModifyArtist: (artistId: UUID) -> Unit = {
                        closeWithAnim {
                            navigator.push(ModifyArtistDestination(it))
                        }
                    }
                }

                val viewModel: ArtistBottomSheetViewModel = koinViewModel {
                    parametersOf(navScope, params)
                }
                ArtistBottomSheetScreen(
                    viewModel = viewModel,
                )
            }
        }
    }
}
