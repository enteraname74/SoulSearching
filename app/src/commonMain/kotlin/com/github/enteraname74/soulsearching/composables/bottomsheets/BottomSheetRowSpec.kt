package com.github.enteraname74.soulsearching.composables.bottomsheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_playlist_add
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_playlist_play
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_playlist_remove
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_queue_music
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_quick_access_filled
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.strings.strings
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import org.jetbrains.compose.resources.DrawableResource

data class BottomSheetRowSpec(
    val icon: DrawableResource,
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
            SoulIcon(
                size = UiConstants.ImageSize.medium,
                icon = icon,
                contentDescription = title,
                color = SoulSearchingColorTheme.colorScheme.onSecondary,
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
                icon = CoreRes.drawable.ic_quick_access_filled,
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
                icon = CoreRes.drawable.ic_playlist_add,
                title = strings.addToPlaylist,
                onClick = onClick,
            )

        fun playNext(
            onClick: () -> Unit,
        ): BottomSheetRowSpec =
            BottomSheetRowSpec(
                icon = CoreRes.drawable.ic_playlist_play,
                title = strings.playNext,
                onClick = onClick,
            )

        fun addToQueue(
            onClick: () -> Unit,
        ): BottomSheetRowSpec =
            BottomSheetRowSpec(
                icon = CoreRes.drawable.ic_queue_music,
                title = strings.addToQueue,
                onClick = onClick,
            )

        fun removeFromPlayedList(
            onClick: () -> Unit,
        ): BottomSheetRowSpec =
            BottomSheetRowSpec(
                icon = CoreRes.drawable.ic_playlist_remove,
                title = strings.removeFromPlayedList,
                onClick = onClick,
            )
    }
}