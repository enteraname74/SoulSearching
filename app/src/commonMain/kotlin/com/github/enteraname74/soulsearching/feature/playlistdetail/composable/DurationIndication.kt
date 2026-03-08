package com.github.enteraname74.soulsearching.feature.playlistdetail.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import kotlin.time.Duration

@Composable
internal fun DurationIndication(
    duration: Duration,
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(UiConstants.Spacing.medium),
        textAlign = TextAlign.Center,
        color = SoulSearchingColorTheme.colorScheme.subPrimaryText,
        text = strings.duration(duration = duration),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 15.sp
    )
}