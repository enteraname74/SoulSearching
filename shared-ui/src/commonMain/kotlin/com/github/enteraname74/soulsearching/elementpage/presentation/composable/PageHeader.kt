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
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.SoulSearchingContext
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import com.github.enteraname74.soulsearching.coreui.image.SoulImage
import com.github.enteraname74.soulsearching.coreui.ScreenOrientation


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
                    .padding(UiConstants.Spacing.large),
                horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
            ) {
                SoulImage(
                    bitmap = cover,
                    size = UiConstants.ImageSize.huge,
                    roundedPercent = 5
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small)
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
                        start = UiConstants.Spacing.large,
                        end = UiConstants.Spacing.large,
                        bottom = UiConstants.Spacing.large
                    ),
                verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SoulImage(
                    bitmap = cover,
                    size = UiConstants.ImageSize.veryHuge,
                    roundedPercent = 5
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(UiConstants.Spacing.small),
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