package com.github.enteraname74.domain.usecase.player

import com.github.enteraname74.domain.model.MergeMode
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.player.PlayerMusic
import com.github.enteraname74.domain.model.player.PlayerMusicUser
import com.github.enteraname74.domain.model.player.SharedPlayerMusic
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.repository.PlayerRepository
import com.github.enteraname74.domain.usecase.music.CloudMusicToMusicUseCase
import com.github.enteraname74.domain.util.DateUtils
import kotlin.uuid.Uuid

class FetchPlayedListMusicsUseCase(
    private val playerRepository: PlayerRepository,
    private val musicRepository: MusicRepository,
    private val cloudMusicToMusicUseCase: CloudMusicToMusicUseCase,
    private val settings: SoulSearchingSettings,
) {
    suspend operator fun invoke(playedListId: Uuid): Pair<List<PlayerMusic>, List<PlayerMusicUser>> {
        val newUpdateTimestamp = DateUtils.now()
        val sharedPlayerMusics: List<SharedPlayerMusic> = playerRepository.fetchPlayedListMusics(playedListId)
        val playerMusicUsers: MutableList<PlayerMusicUser> = mutableListOf()
        val musics: MutableList<PlayerMusic> = mutableListOf()

        sharedPlayerMusics.forEach { sharedPlayerMusic ->
            val music: Music = cloudMusicToMusicUseCase(
                cloudMusic = sharedPlayerMusic.music,
                mergeMode = MergeMode.LocalFirst,
                cachedArtists = emptySet(),
                cachedAlbums = emptySet(),
                cachedMusics = emptySet(),
            )
            musicRepository.upsert(music)

            val playerMusicUser = PlayerMusicUser(
                playedListId = sharedPlayerMusic.playedListId,
                userId = sharedPlayerMusic.music.userId,
                musicId = music.musicId,
            )

            musics.add(sharedPlayerMusic.toPlayerMusic(music))
            playerMusicUsers.add(playerMusicUser)
        }

        settings.set(
            key = SoulSearchingSettingsKeys.Player.SHARED_PLAYED_LIST_MUSIC_UPDATE_MILLIS.key,
            value = newUpdateTimestamp,
        )
        return Pair(musics, playerMusicUsers)
    }
}
