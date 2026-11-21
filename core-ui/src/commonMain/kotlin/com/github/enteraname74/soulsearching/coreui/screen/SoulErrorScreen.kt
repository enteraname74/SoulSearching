package com.github.enteraname74.soulsearching.coreui.screen

import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.an_error_occurred
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_error
import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.topbar.TopBarActionSpec

@Composable
fun SoulErrorScreen(
    leftAction: TopBarActionSpec,
    text: SoulTextSpec,
    title: SoulTextSpec? = CoreRes.string.an_error_occurred.toTextSpec(),
) {
    SoulTemplateScreen(
        leftAction = leftAction,
        text = text,
        buttonSpec = null,
        rightAction = null,
        title = title,
        icon = CoreRes.drawable.ic_error,
    )
}
