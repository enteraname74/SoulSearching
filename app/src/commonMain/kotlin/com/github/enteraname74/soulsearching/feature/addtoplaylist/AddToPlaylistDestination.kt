package com.github.enteraname74.soulsearching.feature.addtoplaylist

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.navigation.NavigationAnimations
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

@Serializable
data class AddToPlaylistDestination(
    val playlistId: Uuid,
) : NavKey {
    companion object {
        fun register(
            entryProviderScope: EntryProviderScope<NavKey>,
            navigator: Navigator,
        ) {
            entryProviderScope.entry<AddToPlaylistDestination>(
                metadata = NavigationAnimations.horizontalMetadata,
            ) { key ->
                val holder: AddToPlaylistViewHolder = koinViewModel {
                    parametersOf(key)
                }
                holder.Screen(
                    navigation = object : AddToPlaylistNavScope {
                        override fun navigateBack() {
                            navigator.pop()
                        }
                    }
                )
            }
        }
    }
}
