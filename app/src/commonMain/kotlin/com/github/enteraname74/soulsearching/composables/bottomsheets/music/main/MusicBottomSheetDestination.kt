package com.github.enteraname74.soulsearching.composables.bottomsheets.music.main

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.domain.util.serializer.UUIDSerializer
import com.github.enteraname74.soulsearching.composables.bottomsheets.BottomSheetDestination
import com.github.enteraname74.soulsearching.composables.bottomsheets.music.addtoplaylist.AddToPlaylistBottomSheetDestination
import com.github.enteraname74.soulsearching.domain.model.types.MusicBottomSheetMode
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.presentation.ModifyMusicDestination
import com.github.enteraname74.soulsearching.navigation.BottomSheetSceneStrategy
import com.github.enteraname74.soulsearching.navigation.LocalBottomSheetCloseWithAnimAction
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

@Serializable
data class MusicBottomSheetDestination(
    @Serializable(UUIDSerializer::class)
    val musicId: UUID,
    @Serializable(UUIDSerializer::class)
    val playlistId: UUID? = null,
    val mode: MusicBottomSheetMode = MusicBottomSheetMode.NORMAL,
) : BottomSheetDestination {
    companion object {
        fun register(
            entryProviderScope: EntryProviderScope<NavKey>,
            navigator: Navigator,
        ) {
            entryProviderScope.entry<MusicBottomSheetDestination>(
                metadata = BottomSheetSceneStrategy.bottomSheet()
            ) { params ->

                val closeWithAnim = LocalBottomSheetCloseWithAnimAction.current
                val navScope = object : MusicBottomSheetNavScope {
                    override val navigateBack: () -> Unit = { closeWithAnim { } }
                    override val toModifyMusic: (musicId: UUID) -> Unit = { musicId ->
                        closeWithAnim {
                            navigator.push(ModifyMusicDestination(musicId))
                        }
                    }
                    override val toAddToPlaylists: (musicId: UUID) -> Unit = {
                        navigator.push(
                            AddToPlaylistBottomSheetDestination(
                                selectedMusicIds = listOf(it),
                            )
                        )
                    }
                }

                val viewModel: MusicBottomSheetViewModel = koinViewModel {
                    parametersOf(navScope, params)
                }
                MusicBottomSheetScreen(
                    viewModel = viewModel,
                )
            }
        }
    }
}