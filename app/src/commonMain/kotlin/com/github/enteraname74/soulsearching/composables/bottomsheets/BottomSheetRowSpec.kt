package com.github.enteraname74.soulsearching.composables.bottomsheets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.DoubleArrow
import androidx.compose.material.icons.rounded.PlaylistRemove
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme

data class BottomSheetRowSpec(
    val icon: ImageVector,
    val title: String,
    val onClick: () -> Unit,
) {
    @Composable
    fun Row() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickableWithHandCursor {
                    onClick()
                }
                .padding(
                    vertical = UiConstants.Spacing.mediumPlus,
                    horizontal = UiConstants.Spacing.large,
                ),
            horizontalArrangement = Arrangement.spacedBy(UiConstants.Spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(UiConstants.ImageSize.medium),
                imageVector = icon,
                contentDescription = title,
                colorFilter = ColorFilter.tint(SoulSearchingColorTheme.colorScheme.onSecondary)
            )
            Text(
                text = title,
                color = SoulSearchingColorTheme.colorScheme.onSecondary
            )
        }
    }
    
    companion object {
        fun quickAccess(
            onClick: () -> Unit,
            isInQuickAccess: Boolean,
        ): BottomSheetRowSpec =
            BottomSheetRowSpec(
                icon = Icons.Rounded.DoubleArrow,
                title = if (isInQuickAccess) {
                    strings.removeFromQuickAccess
                } else {
                    strings.addToQuickAccess
                },
                onClick = onClick,
            )

        fun addToPlaylist(
            onClick: () -> Unit,
        ): BottomSheetRowSpec =
            BottomSheetRowSpec(
                icon = Icons.AutoMirrored.Rounded.PlaylistAdd,
                title = strings.addToPlaylist,
                onClick = onClick,
            )

        fun playNext(
            onClick: () -> Unit,
        ): BottomSheetRowSpec =
            BottomSheetRowSpec(
                icon = Icons.AutoMirrored.Rounded.PlaylistPlay,
                title = strings.playNext,
                onClick = onClick,
            )

        fun addToQueue(
            onClick: () -> Unit,
        ): BottomSheetRowSpec =
            BottomSheetRowSpec(
                icon = Icons.AutoMirrored.Rounded.QueueMusic,
                title = strings.addToQueue,
                onClick = onClick,
            )

        fun removeFromPlayedList(
            onClick: () -> Unit,
        ): BottomSheetRowSpec =
            BottomSheetRowSpec(
                icon = Icons.Rounded.PlaylistRemove,
                title = strings.removeFromPlayedList,
                onClick = onClick,
            )
    }
}