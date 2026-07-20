package com.github.enteraname74.soulsearching.features.playback.player

import com.github.enteraname74.domain.model.player.PlayerToken
import com.github.enteraname74.domain.repository.CloudPreferencesRepository
import com.github.enteraname74.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

class SignedPlaybackUrlProvider(
    private val playerRepository: PlayerRepository,
    private val cloudPreferencesRepository: CloudPreferencesRepository,
) {
    private var playerToken: PlayerToken? = null

    suspend fun getUpdatedToken(): PlayerToken? {
        val nearFuture = Clock.System.now().plus(2.minutes)

        val isTokenValid = playerToken?.expireAt?.let { expireAt ->
            expireAt > nearFuture
        } ?: false

        if (isTokenValid) return playerToken

        playerToken = playerRepository.getPlayerToken().getOrNull()
        return playerToken
    }

    suspend fun getMusicUrl(
        token: PlayerToken,
        remoteId: String,
    ): String =
        "${cloudPreferencesRepository.observeUrl().firstOrNull().orEmpty()}/music/token?token=${token.token}&musicId=$remoteId"
}