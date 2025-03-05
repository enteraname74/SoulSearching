package com.github.enteraname74.soulsearching

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.application.ApplicationViewModel
import com.github.enteraname74.soulsearching.shareddi.LocalDatabaseInitializer

@Composable
fun SoulSearchingDesktop() {
    LocalDatabaseInitializer.init()
    val applicationViewModel = injectElement<ApplicationViewModel>()

    with(applicationViewModel) {
        isReadPermissionGranted = true
        isPostNotificationGranted = true
    }

    SoulSearchingApplication()
}