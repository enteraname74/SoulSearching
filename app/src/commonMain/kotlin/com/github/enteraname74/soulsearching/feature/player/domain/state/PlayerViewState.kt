package com.github.enteraname74.soulsearching.feature.player.domain.state

import androidx.compose.ui.graphics.Color
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.player.FullPlayerMusicUser
import com.github.enteraname74.domain.model.player.PlayedListScope
import com.github.enteraname74.domain.model.player.PlayerMode
import com.github.enteraname74.domain.model.player.PlayerUserStatus
import com.github.enteraname74.soulsearching.coreui.dialog.SoulDialog
import com.github.enteraname74.soulsearching.coreui.ext.blend
import com.github.enteraname74.soulsearching.coreui.theme.color.SoulSearchingDarkLightThemes
import java.util.*
import kotlin.uuid.Uuid

sealed interface PlayerViewState {
    data object Closed : PlayerViewState
    data class Data(
        val currentMusic: Music,
        val currentMusicIndex: Int,
        val isCurrentMusicInFavorite: Boolean,
        val playedList: List<Music>,
        val playerMode: PlayerMode,
        val isPlaying: Boolean,
        val aroundSongs: List<Music>,
        val playerMusicUsers: List<FullPlayerMusicUser>,
        val playedListScope: PlayedListScope,
        val sharedListState: SharedListState?,
        val dialog: SoulDialog?,
    ) : PlayerViewState {

        fun getUserTag(
            musicId: UUID,
        ): UserTag? {
            val correspondingUser = playerMusicUsers.find { it.musicId == musicId }?.user ?: return null
            val index: Int = sharedListState?.let { state ->
                (listOf(state.host) + state.guests)
                    .filterNotNull()
                    .indexOfFirst { it.id == correspondingUser.id && it.deviceId == correspondingUser.deviceId }
            } ?: 0

            // TODO SHARED PLAYED LIST: Better theme for UserTag?
            val theme = SoulSearchingDarkLightThemes
                .themes
                .getOrNull(index.mod(SoulSearchingDarkLightThemes.themes.lastIndex))
                ?.lightTheme ?: SoulSearchingDarkLightThemes.themes.first().lightTheme

            return UserTag(
                username = correspondingUser.username,
                contentColor = theme.onSecondary,
                containerColor = theme.secondary.blend(Color.Black, 0.2f)
            )
        }
    }
}

data class SharedListState(
    val code: String,
    val host: User?,
    val guests: List<User>,
) {
    data class User(
        val id: Uuid,
        val deviceId: String,
        val username: String,
        val appearance: Int?,
        val status: PlayerUserStatus,
        val isCurrentUser: Boolean,
        val onRemove: (() -> Unit)?,
    )
}
