package com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.presentation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.editableelement.modifyalbum.domain.state.ModifyAlbumNavigationState
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

@Serializable
data class ModifyAlbumDestination(
    val selectedAlbumId: Uuid,
) : NavKey {
    companion object {
        fun register(
            entryProviderScope: EntryProviderScope<NavKey>,
            navigator: Navigator,
        ) {
            entryProviderScope.entry<ModifyAlbumDestination> { key ->
                ModifyAlbumRoute(
                    viewModel = koinViewModel {
                        parametersOf(key)
                    },
                    onNavigationState = {
                        when (it) {
                            ModifyAlbumNavigationState.Back -> {
                                navigator.pop()
                            }
                            ModifyAlbumNavigationState.Idle -> {
                                /*no-op*/
                            }
                        }
                    }
                )
            }
        }
    }
}
