package com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.presentation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.editableelement.modifyartist.domain.state.ModifyArtistNavigationState
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

@Serializable
data class ModifyArtistDestination(
    val selectedArtistId: Uuid,
) : NavKey {
    companion object {
        fun register(
            entryProviderScope: EntryProviderScope<NavKey>,
            navigator: Navigator,
        ) {
            entryProviderScope.entry<ModifyArtistDestination> { key ->
                ModifyArtistRoute(
                    viewModel = koinViewModel {
                        parametersOf(key)
                    },
                    onNavigationState = {
                        when (it) {
                            ModifyArtistNavigationState.Back -> {
                                navigator.pop()
                            }
                            ModifyArtistNavigationState.Idle -> {
                                /*no-op*/
                            }
                        }
                    }
                )
            }
        }
    }
}
