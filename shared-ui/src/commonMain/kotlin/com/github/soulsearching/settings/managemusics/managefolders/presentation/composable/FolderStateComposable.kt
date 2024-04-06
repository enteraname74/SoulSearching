package com.github.soulsearching.settings.managemusics.managefolders.presentation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.github.soulsearching.Constants
import com.github.soulsearching.SoulSearchingContext
import com.github.soulsearching.composables.SoulSearchingLogo
import com.github.soulsearching.colortheme.domain.model.SoulSearchingColorTheme
import com.github.soulsearching.domain.model.types.ScreenOrientation

@Composable
fun FolderStateComposable(
    stateTitle: String
) {
    when (SoulSearchingContext.orientation) {
        ScreenOrientation.HORIZONTAL-> {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = SoulSearchingColorTheme.colorScheme.primary),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SoulSearchingLogo()
                Text(
                    text = stateTitle,
                    color = SoulSearchingColorTheme.colorScheme.onPrimary
                )
            }
        }
        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = SoulSearchingColorTheme.colorScheme.primary)
                    .padding(top = Constants.Spacing.large),
            ) {
                Box(modifier = Modifier.align(Alignment.TopCenter)) {
                    SoulSearchingLogo()
                }
                Box(modifier = Modifier.align(Alignment.Center)) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = stateTitle,
                        color = SoulSearchingColorTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}