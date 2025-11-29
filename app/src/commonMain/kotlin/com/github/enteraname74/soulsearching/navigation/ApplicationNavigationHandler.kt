package com.github.enteraname74.soulsearching.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.github.enteraname74.soulsearching.feature.appinit.songfetching.AppInitSongFetchingDestination
import com.github.enteraname74.soulsearching.feature.application.MainAppDestination
import com.github.enteraname74.soulsearching.feature.migration.MigrationDestination
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.MultipleArtistsChoiceDestination

@Composable
fun ApplicationNavigationHandler(
    navigator: Navigator,
    backStack: NavBackStack<NavKey>,
) {
    val entryProvider = buildEntryProvider(navigator = navigator)

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        onBack = navigator::goBack,
        entryProvider = entryProvider,
    )
}

private fun buildEntryProvider(
    navigator: Navigator,
): (NavKey) -> NavEntry<NavKey> = entryProvider {
    MigrationDestination.register(
        entryProviderScope = this,
        navigator = navigator,
    )
    MainAppDestination.register(
        entryProviderScope = this,
    )
    AppInitSongFetchingDestination.register(
        entryProviderScope = this,
        navigator = navigator,
    )
    MultipleArtistsChoiceDestination.register(
        entryProviderScope = this,
        navigator = navigator,
    )
}