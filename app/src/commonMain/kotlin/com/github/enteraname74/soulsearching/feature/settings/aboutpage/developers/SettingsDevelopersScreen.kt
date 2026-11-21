package com.github.enteraname74.soulsearching.feature.settings.aboutpage.developers

import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_open_in_new
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuElement
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec

@Composable
fun SettingsDevelopersRoute(
    navigateBack: () -> Unit,
) {
    SettingsDevelopersScreenView(
        navigateBack = navigateBack
    )
}

@Composable
private fun SettingsDevelopersScreenView(
    navigateBack: () -> Unit
) {
    val developers = listOf(
        Developer(
            name = "Noah Penin",
            function = CoreRes.string.lead_developer.toTextSpec(),
            link = "https://github.com/enteraname74"
        )
    )

    val uriHandler = LocalUriHandler.current

    SettingPage(
        navigateBack = navigateBack,
        title = CoreRes.string.developers_title.toTextSpec(),
    ) {
        items(developers) { developer ->
            SoulMenuElement(
                title = developer.name.toTextSpec(),
                subTitle = developer.function,
                onClick = {
                    uriHandler.openUri(
                        uri = developer.link,
                    )
                },
                trailIcon = CoreRes.drawable.ic_open_in_new,
            )
        }
    }
}
