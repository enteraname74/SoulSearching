package com.github.enteraname74.soulsearching.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.*
import androidx.navigation3.ui.NavDisplay
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.appinit.songfetching.AppInitSongFetchingDestination
import com.github.enteraname74.soulsearching.feature.application.MainAppDestination
import com.github.enteraname74.soulsearching.feature.migration.MigrationDestination
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.MultipleArtistsChoiceDestination

@Composable
fun ApplicationNavigationHandler(
    navigator: Navigator,
    backStack: NavBackStack<NavKey>,
    loadingManager: LoadingManager = injectElement(),
) {
    val entryProvider = buildEntryProvider(navigator = navigator)
    val isLoading: Boolean by loadingManager.state.collectAsStateWithLifecycle()

    NavDisplay(
        modifier = Modifier
            .fillMaxSize(),
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        onBack = {
            if (!isLoading) {
                navigator.goBack()
            }
        },
        transitionSpec = {
            NavigationAnimations.default
        },
        popTransitionSpec = {
            NavigationAnimations.default
        },
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