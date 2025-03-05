package com.github.enteraname74.soulsearching.coreui.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.Res
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.app_icon
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import org.jetbrains.compose.resources.painterResource

@Composable
fun SoulSearchingLogo(
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(UiConstants.ImageSize.veryLarge),
            painter = painterResource(Res.drawable.app_icon),
            contentDescription = strings.appLogo,
            colorFilter = ColorFilter.tint(color = SoulSearchingColorTheme.colorScheme.onPrimary)
        )
        Text(
            text = strings.appName,
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
            style = UiConstants.Typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
    }
}