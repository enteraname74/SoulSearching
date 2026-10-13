package com.github.enteraname74.soulsearching.feature.editableelement.modifycollection.presentation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.editableelement.modifycollection.domain.state.ModifyCollectionNavigationState
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

@Serializable
data class ModifyCollectionDestination(
    val selectedCollectionId: Uuid,
) : NavKey {
    companion object {
        fun register(entryProviderScope: EntryProviderScope<NavKey>, navigator: Navigator) {
            entryProviderScope.entry<ModifyCollectionDestination> { key ->
                ModifyCollectionRoute(
                    viewModel = koinViewModel { parametersOf(key) },
                    onNavigationState = {
                        when (it) {
                            ModifyCollectionNavigationState.Back -> navigator.pop()
                            ModifyCollectionNavigationState.Idle -> Unit
                        }
                    },
                )
            }
        }
    }
}
