package com.github.enteraname74.soulsearching

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.di.appModule
import com.github.enteraname74.soulsearching.di.injectElement
import com.github.enteraname74.soulsearching.feature.mainpage.domain.viewmodel.MainActivityViewModel
import com.github.enteraname74.soulsearching.shareddi.LocalDatabaseInitializer
import org.koin.compose.KoinApplication

@Composable
fun SoulSearchingDesktop() {
    LocalDatabaseInitializer.init()
    val mainActivityViewModel = injectElement<MainActivityViewModel>()

    with(mainActivityViewModel) {
        isReadPermissionGranted = true
        isPostNotificationGranted = true
    }

    SoulSearchingApplication()
}