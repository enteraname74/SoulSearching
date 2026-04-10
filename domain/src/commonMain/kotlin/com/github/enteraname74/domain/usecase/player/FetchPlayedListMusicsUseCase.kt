package com.github.enteraname74.domain.usecase.player

import com.github.enteraname74.domain.model.MergeMode
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.player.PlayerMusic
import com.github.enteraname74.domain.model.player.SharedPlayerMusic
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.domain.repository.PlayerRepository
import com.github.enteraname74.domain.usecase.music.UpsertCloudMusicUseCase
import com.github.enteraname74.domain.util.DateUtils
import kotlin.uuid.Uuid

class FetchPlayedListMusicsUseCase(
    private val playerRepository: PlayerRepository,
    private val upsertCloudMusicUseCase: UpsertCloudMusicUseCase,
    private val settings: SoulSearchingSettings,
) {
    suspend operator fun invoke(playedListId: Uuid): List<PlayerMusic> {
        val newUpdateTimestamp = DateUtils.now()
        val sharedPlayerMusics: List<SharedPlayerMusic> = playerRepository.fetchPlayedListMusics(playedListId)

        val musics: List<PlayerMusic> = sharedPlayerMusics.map { sharedPlayerMusic ->
            val music: Music = upsertCloudMusicUseCase(
                cloudMusic = sharedPlayerMusic.music,
                mergeMode = MergeMode.LocalFirst,
            )
            sharedPlayerMusic.toPlayerMusic(music)
        }

        settings.set(
            key = SoulSearchingSettingsKeys.Player.SHARED_PLAYED_LIST_MUSIC_UPDATE_MILLIS.key,
            value = newUpdateTimestamp,
        )
        return musics
    }
}