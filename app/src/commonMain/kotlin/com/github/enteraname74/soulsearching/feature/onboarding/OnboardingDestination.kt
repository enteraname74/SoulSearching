package com.github.enteraname74.soulsearching.feature.onboarding

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.github.enteraname74.soulsearching.feature.application.MainAppDestination
import com.github.enteraname74.soulsearching.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data object OnboardingDestination : NavKey {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
        navigator: Navigator,
    ) {
        entryProviderScope.entry<OnboardingDestination> {
            OnboardingScreen(
                navScope = object : OnboardingNavScope {
                    override fun toApp() {
                        navigator.push(MainAppDestination) {
                            clearBackStack = true
                        }
                    }
                }
            )
        }
    }
}