package com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.presentation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.domain.util.serializer.UUIDSerializer
import com.github.enteraname74.soulsearching.feature.editableelement.modifymusic.domain.state.ModifyMusicNavigationState
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.*

@Serializable
data class ModifyMusicDestination(
    @Serializable(UUIDSerializer::class)
    val selectedMusicId: UUID
) : NavKey {
    companion object {
        fun register(
            entryProviderScope: EntryProviderScope<NavKey>,
            navigator: Navigator,
        ) {
            entryProviderScope.entry<ModifyMusicDestination> { key ->
                ModifyMusicRoute(
                    viewModel = koinViewModel {
                        parametersOf(key)
                    },
                    onNavigationState = {
                        when (it) {
                            ModifyMusicNavigationState.Back -> {
                                navigator.goBack()
                            }

                            ModifyMusicNavigationState.Idle -> {
                                /*no-op*/
                            }
                        }
                    }
                )
            }
        }
    }
}
