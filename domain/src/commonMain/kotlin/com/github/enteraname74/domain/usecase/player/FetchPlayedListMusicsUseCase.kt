package com.github.enteraname74.domain.usecase.player

import com.github.enteraname74.domain.model.MergeMode
import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.player.PlayerMusic
import com.github.enteraname74.domain.model.player.SharedPlayerMusic
import com.github.enteraname74.domain.repository.PlayerRepository
import com.github.enteraname74.domain.usecase.music.UpsertCloudMusicUseCase
import kotlin.uuid.Uuid

class FetchPlayedListMusicsUseCase(
    private val playerRepository: PlayerRepository,
    private val upsertCloudMusicUseCase: UpsertCloudMusicUseCase,
) {
    suspend operator fun invoke(playedListId: Uuid): List<PlayerMusic> {
        val sharedPlayerMusics: List<SharedPlayerMusic> = playerRepository.fetchPlayedListMusics(playedListId)

        return sharedPlayerMusics.map { sharedPlayerMusic ->
            val music: Music = upsertCloudMusicUseCase(
                cloudMusic = sharedPlayerMusic.music,
                mergeMode = MergeMode.LocalFirst,
            )
            sharedPlayerMusic.toPlayerMusic(music)
        }
    }
}