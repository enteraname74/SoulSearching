package com.github.enteraname74.soulsearching.feature.settings.managemusics.addmusics.presentation.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.soulsearching.composables.SoulImage
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.image.SoulBitmapImage
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import java.util.UUID

@Composable
fun MusicSelectableComposable(
    music: Music,
    onClick: () -> Unit,
    isSelected: Boolean,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary
) {
    InnerContent(
        music = music,
        coverType = CoverType.Id(music.coverId),
        isSelected = isSelected,
        textColor = textColor,
        onClick = onClick,
    )
}

private sealed interface CoverType{
    data class Id(val id: UUID?): CoverType
    data class Bitmap(val bitmap: ImageBitmap?): CoverType
}

@Composable
fun MusicSelectableComposable(
    music: Music,
    cover: ImageBitmap?,
    onClick: () -> Unit,
    isSelected: Boolean,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary
) {
    InnerContent(
        music = music,
        coverType = CoverType.Bitmap(bitmap = cover),
        isSelected = isSelected,
        textColor = textColor,
        onClick = onClick,
    )
}

@Composable
private fun InnerContent(
    music: Music,
    coverType: CoverType,
    onClick: () -> Unit,
    isSelected: Boolean,
    textColor: Color = SoulSearchingColorTheme.colorScheme.onPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(UiConstants.Spacing.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when(coverType) {
                is CoverType.Bitmap -> {
                    SoulBitmapImage(
                        bitmap = coverType.bitmap,
                        size = UiConstants.CoverSize.small,
                        tint = textColor
                    )
                }
                is CoverType.Id -> {
                    SoulImage(
                        coverId = music.coverId,
                        size = UiConstants.CoverSize.small,
                        tint = textColor
                    )
                }
            }
            Column(
                modifier = Modifier
                    .height(55.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = music.name,
                    color = textColor,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = "${music.artist} | ${music.album}",
                    color = textColor,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Normal
                )

            }
            Checkbox(
                checked = isSelected,
                onCheckedChange = {
                    onClick()
                },
                colors = CheckboxDefaults.colors(
                    checkmarkColor = textColor,
                    checkedColor = Color.Transparent,
                    uncheckedColor = textColor
                )
            )
        }
    }
}