package com.github.enteraname74.soulsearching.feature.permission

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

// TODO: Use missing permission destination
data object MissingPermissionsDestination : NavKey {
    fun register(
        entryProviderScope: EntryProviderScope<NavKey>,
    ) {
        entryProviderScope.entry<MissingPermissionsDestination> {
            MissingPermissionsScreen()
        }
    }
}
