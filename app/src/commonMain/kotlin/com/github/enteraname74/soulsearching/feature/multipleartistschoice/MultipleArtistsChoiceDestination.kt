package com.github.enteraname74.soulsearching.feature.multipleartistschoice

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.navigation.NavigationAnimations
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data class MultipleArtistsChoiceDestination(
    val mode: MultipleArtistsChoiceMode,
) : NavKey {
    companion object {
        fun register(
            entryProviderScope: EntryProviderScope<NavKey>,
            navScope: MultipleArtistsChoiceNavScope,
        ) {
            entryProviderScope.entry<MultipleArtistsChoiceDestination>(
                metadata = NavigationAnimations.horizontalMetadata,
            ) { key ->
                val holder: MultipleArtistsChoiceViewHolder = koinViewModel {
                    parametersOf(key)
                }
                holder.Screen(
                    navigation = navScope,
                )
            }
        }
    }
}
