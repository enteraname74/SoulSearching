package com.github.enteraname74.soulsearching.feature.application

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import com.github.enteraname74.soulsearching.navigation.ApplicationNavigationHandler
import com.github.enteraname74.soulsearching.navigation.ApplicationSerializerModule
import com.github.enteraname74.soulsearching.navigation.Navigator
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ApplicationRoute() {
    val applicationViewModel: ApplicationViewModel = koinViewModel()
    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration { serializersModule = ApplicationSerializerModule },
        applicationViewModel.initialRoute,
    )
    val navigator = remember { Navigator(backStack) }

    ApplicationNavigationHandler(
        navigator = navigator,
        backStack = backStack,
    )
}