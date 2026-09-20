package com.github.enteraname74.soulsearching.feature.onboarding

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.appinit.AppInitSongFetchingDestination
import com.github.enteraname74.soulsearching.feature.appinit.AppInitSongFetchingNavScope
import com.github.enteraname74.soulsearching.feature.managefolders.ManageFoldersDestination
import com.github.enteraname74.soulsearching.feature.managefolders.ManageFoldersNavScope
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.MultipleArtistsChoiceDestination
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.MultipleArtistsChoiceMode
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.MultipleArtistsChoiceNavScope
import com.github.enteraname74.soulsearching.navigation.BottomSheetSceneStrategy
import com.github.enteraname74.soulsearching.navigation.NavigationAnimations
import com.github.enteraname74.soulsearching.navigation.Navigator
import com.github.enteraname74.soulsearching.navigation.OnboardingSerializerModule

@Composable
internal fun OnboardingScreen(
    navScope: OnboardingNavScope,
    loadingManager: LoadingManager = injectElement(),
) {
    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration { serializersModule = OnboardingSerializerModule },
        AppInitSongFetchingDestination,
    )
    val navigator = remember { Navigator(backStack) }

    val entryProvider = buildEntryProvider(
        navigator = navigator,
        navScope = navScope,
    )
    val isLoading: Boolean by loadingManager.state.collectAsStateWithLifecycle()
    val bottomSheetStrategy = remember { BottomSheetSceneStrategy<NavKey>() }

    NavDisplay(
        modifier = Modifier
            .fillMaxSize(),
        backStack = backStack,
        sceneStrategies = listOf(bottomSheetStrategy),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        onBack = {
            if (!isLoading) {
                navigator.pop()
            }
        },
        transitionSpec = {
            NavigationAnimations.horizontal
        },
        popTransitionSpec = {
            NavigationAnimations.horizontal
        },
        entryProvider = entryProvider,
    )
}

private fun buildEntryProvider(
    navigator: Navigator,
    navScope: OnboardingNavScope,
): (NavKey) -> NavEntry<NavKey> = entryProvider {
    AppInitSongFetchingDestination.register(
        entryProviderScope = this,
        navScope = object : AppInitSongFetchingNavScope {
            override fun toFoldersSelection() {
                navigator.push(
                    route = ManageFoldersDestination(
                        mode = ManageFoldersDestination.Mode.InitialFetch,
                    )
                ) {
                    clearBackStack = true
                }
            }

            override fun toApp() {
                navScope.toApp()
            }
        }
    )
    ManageFoldersDestination.register(
        entryProviderScope = this,
        navScope = object : ManageFoldersNavScope {
            override fun navigateBack() {
                // no-op
            }

            override fun navigateToMultipleArtists() {
                navigator.push(MultipleArtistsChoiceDestination(MultipleArtistsChoiceMode.InitialFetch))
            }

            override fun navigateToApp() {
                navScope.toApp()
            }
        },
    )
    MultipleArtistsChoiceDestination.register(
        entryProviderScope = this,
        navScope = object : MultipleArtistsChoiceNavScope {
            override fun toApp() {
                navScope.toApp()
            }

            override fun navigateBack() {
                navigator.pop()
            }
        }
    )
}
