package com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.presentation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.domain.util.serializer.UUIDSerializer
import com.github.enteraname74.soulsearching.feature.editableelement.modifyplaylist.domain.state.ModifyPlaylistNavigationState
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

@Serializable
data class ModifyPlaylistDestination(
    @Serializable(UUIDSerializer::class)
    val selectedPlaylistId: UUID,
) : NavKey {
    companion object {
        fun register(
            entryProviderScope: EntryProviderScope<NavKey>,
            navigator: Navigator,
        ) {
            entryProviderScope.entry<ModifyPlaylistDestination> { key ->
                ModifyPlaylistRoute(
                    viewModel = koinViewModel {
                        parametersOf(key)
                    },
                    onNavigationState = {
                        when (it) {
                            ModifyPlaylistNavigationState.Back -> {
                                navigator.goBack()
                            }

                            ModifyPlaylistNavigationState.Idle -> {
                                /*no-op*/
                            }
                        }
                    }
                )
            }
        }
    }
}
