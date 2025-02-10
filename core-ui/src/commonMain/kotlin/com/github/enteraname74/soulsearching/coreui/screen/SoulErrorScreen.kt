package com.github.enteraname74.soulsearching.coreui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarActionSpec

@Composable
fun SoulErrorScreen(
    leftAction: TopBarActionSpec,
    text: String,
    title: String? = strings.anErrorOccurred,
) {
   SoulTemplateScreen(
       leftAction = leftAction,
       text = text,
       buttonSpec = null,
       rightAction = null,
       title = title,
       icon = Icons.Rounded.ErrorOutline
   )
}