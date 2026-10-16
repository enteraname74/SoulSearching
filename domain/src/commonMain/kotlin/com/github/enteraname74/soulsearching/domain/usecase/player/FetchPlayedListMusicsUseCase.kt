package com.github.enteraname74.soulsearching.domain.usecase.player

import com.github.enteraname74.soulsearching.domain.model.MergeMode
import com.github.enteraname74.soulsearching.domain.model.Music
import com.github.enteraname74.soulsearching.domain.model.player.PlayerMusic
import com.github.enteraname74.soulsearching.domain.model.player.PlayerMusicUser
import com.github.enteraname74.soulsearching.domain.model.player.SharedPlayerMusic
import com.github.enteraname74.soulsearching.domain.repository.SettingsRepository
import com.github.enteraname74.soulsearching.domain.repository.MusicRepository
import com.github.enteraname74.soulsearching.domain.repository.PlayerRepository
import com.github.enteraname74.soulsearching.domain.usecase.music.CloudMusicToMusicUseCase
import com.github.enteraname74.soulsearching.domain.util.DateUtils
import kotlin.uuid.Uuid

class FetchPlayedListMusicsUseCase(
    private val playerRepository: PlayerRepository,
    private val musicRepository: MusicRepository,
    private val cloudMusicToMusicUseCase: CloudMusicToMusicUseCase,
    private val settingsRepository: SettingsRepository,
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

        settingsRepository.setSharedPlayedListMusicUpdateMillis(newUpdateTimestamp)
        return Pair(musics, playerMusicUsers)
    }
}
