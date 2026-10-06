package com.github.enteraname74.soulsearching.composables.bottomsheets.collection.add

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

@Serializable
data class AddToCollectionBottomSheetDestination(
    val artistIds: List<Uuid> = emptyList(),
    val albumIds: List<Uuid> = emptyList(),
) : NavKey {
    companion object {
        fun register(
            entryProviderScope: EntryProviderScope<NavKey>,
            navScope: AddToCollectionBottomSheetNavScope,
        ) {
            entryProviderScope.entry<AddToCollectionBottomSheetDestination> { params ->
                val viewModel: AddToCollectionBottomSheetViewModel = koinViewModel {
                    parametersOf(navScope, params)
                }
                AddToCollectionBottomSheetScreen(viewModel)
            }
        }
    }
}
