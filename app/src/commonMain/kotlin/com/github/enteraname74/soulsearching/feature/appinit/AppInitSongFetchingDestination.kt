package com.github.enteraname74.soulsearching.feature.appinit

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object AppInitSongFetchingDestination : NavKey {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navScope: AppInitSongFetchingNavScope,
    ) {
        entryProviderScope.entry<AppInitSongFetchingDestination> {
            val holder: AppInitSongFetchingViewHolder = koinViewModel()
            holder.Screen(
                navigation = navScope,
            )
        }
    }
}
