package com.github.enteraname74.soulsearching.coreui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.SoulCircularProgressIndicator
import com.github.enteraname74.soulsearching.coreui.topbar.SoulTopBar

@Composable
fun SoulLoadingScreen(
    navigateBack: () -> Unit,
) {
    SoulScreen {
        Column {
            SoulTopBar(leftAction = navigateBack)
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                SoulCircularProgressIndicator()
            }
        }
    }
}