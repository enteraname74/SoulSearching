package com.github.soulsearching.classes.utils

import com.github.soulsearching.viewmodel.SettingsViewModel

/**
 * Object containing the instance of the setting view model.
 * It is static because we need to access this view model in multiple parts of the applications
 */
object SettingsUtils {
    val settingsViewModel = SettingsViewModel()
}