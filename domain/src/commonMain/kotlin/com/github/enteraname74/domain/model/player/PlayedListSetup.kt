package com.github.enteraname74.domain.model.player

import com.github.enteraname74.domain.model.Music
import kotlin.uuid.Uuid
import kotlin.time.Clock

data class PlayedListSetup(
    val musics: List<Music>,
    val selectedMusic: Music?,
    val listId: String?,
    val isMain: Boolean,
    val state: PlayedListState,
    val seekTo: Long? = null,
    val mode: PlayerMode = PlayerMode.Normal,
    val type: PlayedListType,
    val scope: PlayedListScope,
    val forceOverride: Boolean = false,
) {
    private val playedListId: Uuid = Uuid.random()

    fun toPlayedList(): PlayerPlayedList =
        PlayerPlayedList(
            id = playedListId,
            playlistId = listId,
            isMainPlaylist = isMain,
            mode = mode,
            state = state,
            type = type,
            scope = scope,
        )

    fun toPlayerMusics(): List<PlayerMusic> {
        val currentMusicId: Uuid = selectedMusic?.musicId ?: musics.first().musicId

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
            type: PlayedListType,
            scope: PlayedListScope,
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
                type = type,
                scope = scope,
            )
    }
}
