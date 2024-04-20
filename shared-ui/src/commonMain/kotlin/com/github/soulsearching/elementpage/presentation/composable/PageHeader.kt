package com.github.soulsearching.elementpage.presentation.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.github.soulsearching.Constants
import com.github.soulsearching.SoulSearchingContext
import com.github.soulsearching.colortheme.domain.model.SoulSearchingColorTheme
import com.github.soulsearching.composables.AppImage
import com.github.soulsearching.domain.model.types.ScreenOrientation


@Composable
fun PageHeader(
    title: String,
    text: String = "",
    onTextClicked: () -> Unit = {},
    cover: ImageBitmap?
) {
    when (SoulSearchingContext.orientation) {
        ScreenOrientation.HORIZONTAL -> {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Constants.Spacing.large),
                horizontalArrangement = Arrangement.spacedBy(Constants.Spacing.medium),
            ) {
                AppImage(
                    bitmap = cover,
                    size = Constants.ImageSize.huge,
                    roundedPercent = 5
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(Constants.Spacing.small)
                ) {
                    Text(
                        color = SoulSearchingColorTheme.colorScheme.onPrimary,
                        text = title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp
                    )
                    if (text.isNotBlank()) {
                        Text(
                            modifier = Modifier.clickable {
                                onTextClicked()
                            },
                            color = SoulSearchingColorTheme.colorScheme.subText,
                            text = text,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
        else -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = Constants.Spacing.large,
                        end = Constants.Spacing.large,
                        bottom = Constants.Spacing.large
                    ),
                verticalArrangement = Arrangement.spacedBy(Constants.Spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppImage(
                    bitmap = cover,
                    size = Constants.ImageSize.veryHuge,
                    roundedPercent = 5
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(Constants.Spacing.small),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        color = SoulSearchingColorTheme.colorScheme.onPrimary,
                        text = title,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp
                    )
                    if (text.isNotBlank()) {
                        Text(
                            modifier = Modifier.clickable {
                                onTextClicked()
                            },
                            textAlign = TextAlign.Center,
                            color = SoulSearchingColorTheme.colorScheme.subText,
                            text = text,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}