package com.github.enteraname74.soulsearching.feature.settings.cloud.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.HowToReg
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.enteraname74.domain.model.ConnectedUser
import com.github.enteraname74.domain.model.User
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.screen.SoulTemplateComposable
import com.github.enteraname74.soulsearching.coreui.screen.TemplateScreenButtonSpec
import com.github.enteraname74.soulsearching.coreui.strings.strings

@Composable
fun SettingsCloudConnectedView(
    user: ConnectedUser,
    onLogOut: () -> Unit,
) {
    SoulTemplateComposable(
        modifier = Modifier
            .padding(bottom = UiConstants.Spacing.medium),
        icon = Icons.Rounded.HowToReg,
        text = strings.certifyUserConnected(user),
        buttonSpec = TemplateScreenButtonSpec(
            text = strings.cloudLogOut,
            onClick = onLogOut,
            colors = { SoulButtonDefaults.primaryColors() }
        )
    )
}