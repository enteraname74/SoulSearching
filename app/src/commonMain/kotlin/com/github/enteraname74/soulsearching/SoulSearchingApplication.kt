package com.github.enteraname74.soulsearching

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.CachePolicy
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpScaffold
import com.github.enteraname74.soulsearching.coreui.loading.LoadingManager
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.permission.MissingPermissionsScreen
import com.github.enteraname74.soulsearching.navigation.ApplicationNavigationHandler
import com.github.enteraname74.soulsearching.navigation.ApplicationSerializerModule
import com.github.enteraname74.soulsearching.navigation.Navigator
import com.github.enteraname74.soulsearching.view.LoadingView

@Composable
fun SoulSearchingApplication(
    viewModel: ApplicationViewModel = injectElement()
) {
    val state: ApplicationState by viewModel.state.collectAsStateWithLifecycle()

    setSingletonImageLoaderFactory {
        ImageLoader(it)
            .newBuilder()
            .networkCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            //            .logger(DebugLogger())
            .build()
    }
    ApplicationRoute(
        state = state,
    )
}

@Composable
private fun ApplicationRoute(
    loadingManager: LoadingManager = injectElement(),
    feedbackPopUpManager: FeedbackPopUpManager = injectElement(),
    state: ApplicationState,
) {
    SoulSearchingTheme {
        FeedbackPopUpScaffold(
            feedbackPopUpManager = feedbackPopUpManager,
        ) {
            if (state.hasPermissions) {
                state.initialRoute?.let {
                    MainRoute(it)
                }
            } else {
                MissingPermissionsScreen()
            }
            LoadingView(
                loadingManager = loadingManager
            )
        }
    }
}

@Composable
private fun MainRoute(
    initialRoute: NavKey,
) {
    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration { serializersModule = ApplicationSerializerModule },
        initialRoute,
    )
    val navigator = remember { Navigator(backStack) }
    ApplicationNavigationHandler(
        navigator = navigator,
        backStack = backStack,
    )
}
