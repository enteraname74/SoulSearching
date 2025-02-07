package com.github.enteraname74.soulsearching.feature.settings.cloud.composable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddReaction
import androidx.compose.runtime.Composable
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonDefaults
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonSpec
import com.github.enteraname74.soulsearching.coreui.card.SoulInformationCard
import com.github.enteraname74.soulsearching.coreui.strings.strings

@Composable
fun SettingsCloudGenerateCodeCard(
    onGenerateCode: () -> Unit,
) {
    SoulInformationCard(
        title = strings.cloudSignInCodeTitle,
        text = strings.cloudSignInCodeText,
        icon = Icons.Rounded.AddReaction,
        buttonSpec = SoulButtonSpec(
            text = strings.cloudSignInCodeButton,
            onClick = onGenerateCode,
            colors = SoulButtonDefaults.primaryColors(),
        )
    )
}