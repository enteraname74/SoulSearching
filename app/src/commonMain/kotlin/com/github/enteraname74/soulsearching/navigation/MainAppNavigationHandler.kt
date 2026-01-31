package com.github.enteraname74.soulsearching.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.*
import androidx.navigation3.ui.NavDisplay
import com.github.enteraname74.soulsearching.feature.editableelement.ModifyElementNavigationHandler
import com.github.enteraname74.soulsearching.feature.mainpage.presentation.MainPageDestination
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.MultipleArtistsChoiceDestination
import com.github.enteraname74.soulsearching.feature.playlistdetail.PlaylistDetailNavigationHandler
import com.github.enteraname74.soulsearching.feature.settings.SettingsNavigationHandler

@Composable
fun MainAppNavigationHandler(
    backStack: NavBackStack<NavKey>,
    navigator: Navigator,
) {
    val entryProvider = buildEntryProvider(navigator = navigator)

    NavDisplay(
        modifier = Modifier
            .fillMaxSize(),
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        predictivePopTransitionSpec = { NavigationAnimations.predictivePop },
        onBack = navigator::goBack,
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