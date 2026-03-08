package com.github.enteraname74.domain.model.player

import com.github.enteraname74.domain.model.Music
import java.util.UUID
import kotlin.time.Clock

data class PlayedListSetup(
    val musics: List<Music>,
    val selectedMusic: Music?,
    val listId: String?,
    val isMain: Boolean,
    val state: PlayedListState,
    val seekTo: Long? = null,
    val mode: PlayerMode = PlayerMode.Normal,
    val forceOverride: Boolean = false,
) {
    private val playedListId: UUID = UUID.randomUUID()

    fun toPlayedList(): PlayerPlayedList =
        PlayerPlayedList(
            id = playedListId,
            playlistId = listId,
            isMainPlaylist = isMain,
            mode = mode,
            state = state,
        )

    fun toPlayerMusics(): List<PlayerMusic> {
        val currentMusicId: UUID = selectedMusic?.musicId ?: musics.first().musicId

        return musics.mapIndexed { index, music ->
            PlayerMusic(
                music = music,
                playedListId = playedListId,
                order = index.toDouble(),
                shuffledOrder = index.toDouble(),
                lastPlayedMillis = if (currentMusicId == music.musicId) {
                    Clock.System.now().toEpochMilliseconds()
                } else {
                    null
                }
            )
        }
    }

    companion object {
        fun fromSelection(
            musics: List<Music>,
            playlistId: String?,
            isMain: Boolean,
            state: PlayedListState = PlayedListState.Loading,
        ): PlayedListSetup =
            PlayedListSetup(
                musics = musics,
                selectedMusic = musics.first(),
                seekTo = null,
                listId = playlistId,
                isMain = isMain,
                state = state,
                mode = PlayerMode.Normal,
            )
    }
}
