package com.github.enteraname74.soulsearching.composables.bottomsheets.album

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetDestination
import com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.presentation.ModifyAlbumDestination
import com.github.enteraname74.soulsearching.navigation.BottomSheetSceneStrategy
import com.github.enteraname74.soulsearching.navigation.LocalBottomSheetCloseWithAnimAction
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

@Serializable
data class AlbumBottomSheetDestination(
    val albumIds: List<Uuid>,
) : BottomSheetDestination {
    companion object {
        fun register(
            entryProviderScope: EntryProviderScope<NavKey>,
            navigator: Navigator,
        ) {
            entryProviderScope.entry<AlbumBottomSheetDestination>(
                metadata = BottomSheetSceneStrategy.bottomSheet()
            ) { params ->
                val closeWithAnim = LocalBottomSheetCloseWithAnimAction.current

                val navScope = object : AlbumBottomSheetNavScope {
                    override val navigateBack: () -> Unit = { closeWithAnim { } }
                    override val toModifyAlbum: (albumId: Uuid) -> Unit = {
                        closeWithAnim {
                            navigator.push(ModifyAlbumDestination(it))
                        }
                    }
                }

                val viewModel: AlbumBottomSheetViewModel = koinViewModel {
                    parametersOf(navScope, params)
                }
                AlbumBottomSheetScreen(
                    viewModel = viewModel,
                )
            }
        }
    }
}
