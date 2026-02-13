package com.github.enteraname74.soulsearching.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.editableelement.ModifyElementNavigationHandler
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.MainPageDestination
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.MultipleArtistsChoiceDestination
import com.github.enteraname74.soulsearching.feature.playlistdetail.PlaylistDetailNavigationHandler
import com.github.enteraname74.soulsearching.feature.settings.SettingsNavigationHandler

@Composable
fun MainAppNavigationHandler(
    backStack: NavBackStack<NavKey>,
    navigator: Navigator,
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
    MainPageDestination.register(
        entryProviderScope = this,
        navigator = navigator,
    )

    MultipleArtistsChoiceDestination.register(
        entryProviderScope = this,
        navigator = navigator,
    )

    SettingsNavigationHandler.register(
        entryProviderScope = this,
        navigator = navigator,
    )

    ModifyElementNavigationHandler.register(
        entryProviderScope = this,
        navigator = navigator,
    )

    PlaylistDetailNavigationHandler.register(
        entryProviderScope = this,
        navigator = navigator,
    )
}