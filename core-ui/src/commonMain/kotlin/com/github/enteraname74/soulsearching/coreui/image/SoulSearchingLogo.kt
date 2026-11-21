package com.github.enteraname74.soulsearching.coreui.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.app_icon
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.app_logo
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.app_name
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SoulSearchingLogo() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(UiConstants.ImageSize.veryLarge),
            painter = painterResource(CoreRes.drawable.app_icon),
            contentDescription = stringResource(CoreRes.string.app_logo),
            colorFilter = ColorFilter.tint(color = SoulSearchingColorTheme.colorScheme.onPrimary)
        )
        SoulText(
            text = CoreRes.string.app_name.toTextSpec(),
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
            style = UiConstants.Typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
    }
}
