package com.github.enteraname74.soulsearching.coreui.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.button.SoulButton
import com.github.enteraname74.soulsearching.coreui.button.SoulButtonSpec
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon

@Composable
fun SoulInformationCard(
    title: String?,
    text: String,
    icon: ImageVector,
    buttonSpec: SoulButtonSpec?,
    modifier: Modifier = Modifier,
    colors: SoulCardColors = SoulCardDefaults.colors(),
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            contentColor = colors.contentColor,
            containerColor = colors.containerColor,
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(UiConstants.Spacing.large),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SoulIcon(
                icon = icon,
                size = UiConstants.ImageSize.mediumPlus,
                tint = colors.contentColor,
            )
            title?.let {
                Text(
                    modifier = Modifier
                        .padding(
                            vertical = UiConstants.Spacing.small,
                        ),
                    text = it,
                    color = colors.contentColor,
                    textAlign = TextAlign.Center,
                    style = UiConstants.Typography.bodyTitle,
                )
            }

            Text(
                text = text,
                color = colors.contentColor,
                textAlign = TextAlign.Center,
                style = UiConstants.Typography.body,
            )

            buttonSpec?.let {
                SoulButton(
                    spec = it,
                    modifier = Modifier
                        .padding(
                            top = UiConstants.Spacing.medium,
                        ),
                )
            }
        }
    }
}