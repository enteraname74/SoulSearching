package com.github.enteraname74.soulsearching.feature.settings.aboutpage.developers

import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import com.github.enteraname74.soulsearching.coreui.menu.SoulMenuElement
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.feature.settings.presentation.composable.SettingPage

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
            function = strings.leadDeveloper,
            link = "https://github.com/enteraname74"
        )
    )

    val uriHandler = LocalUriHandler.current

    SettingPage(
        navigateBack = navigateBack,
        title = strings.developersTitle,
    ) {
        items(developers) { developer ->
            SoulMenuElement(
                title = developer.name,
                subTitle = developer.function,
                onClick = {
                    uriHandler.openUri(
                        uri = developer.link,
                    )
                },
                trailIcon = Icons.AutoMirrored.Rounded.OpenInNew,
            )
        }
    }
}