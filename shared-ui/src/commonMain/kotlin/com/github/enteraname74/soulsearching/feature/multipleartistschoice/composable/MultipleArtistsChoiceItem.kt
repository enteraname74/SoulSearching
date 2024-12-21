package com.github.enteraname74.soulsearching.feature.multipleartistschoice.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.feature.multipleartistschoice.state.ArtistChoice

@Composable
fun MultipleArtistsChoiceItem(
    artistChoice: ArtistChoice,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableWithHandCursor { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .weight(1f),
            text = artistChoice.artist.artistName,
            color = SoulSearchingColorTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelLarge,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Normal
        )
        Checkbox(
            checked = artistChoice.isSelected,
            onCheckedChange = {
                onClick()
            },
            colors = CheckboxDefaults.colors(
                checkmarkColor = SoulSearchingColorTheme.colorScheme.onPrimary,
                checkedColor = Color.Transparent,
                uncheckedColor = SoulSearchingColorTheme.colorScheme.onPrimary
            )
        )
    }
}