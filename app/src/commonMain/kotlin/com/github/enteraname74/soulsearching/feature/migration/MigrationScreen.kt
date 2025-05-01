package com.github.enteraname74.soulsearching.feature.migration

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.screen.SoulLoadingScreen
import com.github.enteraname74.soulsearching.coreui.strings.strings

@Composable
fun MigrationScreen() {
    SoulLoadingScreen(
        text = strings.appMigration,
    )
}