package com.github.enteraname74.soulsearching.composables.bottomsheets

import com.github.enteraname74.soulsearching.coreui.strings.SoulTextSpec
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.strings.SoulText
import com.github.enteraname74.soulsearching.coreui.strings.toTextSpec
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.enteraname74.soulsearching.coreui.UiConstants
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.CoreRes
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_cloud_filled
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_playlist_add
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_playlist_play
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_playlist_remove
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_queue_music
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_library_music
import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.ic_quick_access_filled
import com.github.enteraname74.soulsearching.coreui.ext.clickableWithHandCursor
import com.github.enteraname74.soulsearching.coreui.image.SoulIcon
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingColorTheme
import org.jetbrains.compose.resources.DrawableResource

data class BottomSheetRowSpec(
    val icon: DrawableResource,
    val title: SoulTextSpec,
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
                contentDescription = title.string,
                color = SoulSearchingColorTheme.colorScheme.onSecondary,
            )
            SoulText(
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
                    CoreRes.string.remove_from_quick_access.toTextSpec()
                } else {
                    CoreRes.string.add_to_quick_access.toTextSpec()
                },
                onClick = onClick,
            )

        fun addToPlaylist(
            onClick: () -> Unit,
        ): BottomSheetRowSpec =
            BottomSheetRowSpec(
                icon = CoreRes.drawable.ic_playlist_add,
                title = CoreRes.string.add_to_playlist.toTextSpec(),
                onClick = onClick,
            )

        fun addToCollection(onClick: () -> Unit): BottomSheetRowSpec =
            BottomSheetRowSpec(
                icon = CoreRes.drawable.ic_library_music,
                title = CoreRes.string.add_to_collection.toTextSpec(),
                onClick = onClick,
            )

        fun playNext(
            onClick: () -> Unit,
        ): BottomSheetRowSpec =
            BottomSheetRowSpec(
                icon = CoreRes.drawable.ic_playlist_play,
                title = CoreRes.string.play_next.toTextSpec(),
                onClick = onClick,
            )

        fun addToQueue(
            onClick: () -> Unit,
        ): BottomSheetRowSpec =
            BottomSheetRowSpec(
                icon = CoreRes.drawable.ic_queue_music,
                title = CoreRes.string.add_to_queue.toTextSpec(),
                onClick = onClick,
            )

        fun removeFromPlayedList(
            onClick: () -> Unit,
        ): BottomSheetRowSpec =
            BottomSheetRowSpec(
                icon = CoreRes.drawable.ic_playlist_remove,
                title = CoreRes.string.remove_from_played_list.toTextSpec(),
                onClick = onClick,
            )

        fun startSharedPlayedList(
            onClick: () -> Unit,
        ) : BottomSheetRowSpec =
            BottomSheetRowSpec(
                icon = CoreRes.drawable.ic_cloud_filled,
                title = CoreRes.string.start_shared_played_list.toTextSpec(),
                onClick = onClick,
            )
    }
}
