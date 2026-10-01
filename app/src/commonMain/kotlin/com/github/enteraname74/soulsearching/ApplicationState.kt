package com.github.enteraname74.soulsearching

import androidx.navigation3.runtime.NavKey

data class ApplicationState(
    val hasNotificationPermissions: Boolean,
    val hasReadStoragePermission: Boolean,
    val initialRoute: NavKey?,
) {
    val hasPermissions: Boolean = hasNotificationPermissions && hasReadStoragePermission
}