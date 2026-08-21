package com.github.enteraname74.soulsearching.features.playback.player

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.player.PlayerToken
import com.github.enteraname74.domain.repository.CloudPreferencesRepository
import com.github.enteraname74.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlin.math.max
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

class SignedPlaybackUrlProvider(
    private val playerRepository: PlayerRepository,
    private val cloudPreferencesRepository: CloudPreferencesRepository,
) {
    private var playerToken: PlayerToken? = null

    /**
     * Checks if the token is valid.
     * The checks are:
     * - if the current token is null, then false
     * - if the current token cannot do the length of the current song, then false
     * - if the current token is near its end, then false
     */
    private suspend fun isTokenValid(): Boolean {
        if (playerToken == null) return false

        val currentMusicDuration: Long = playerRepository.getCurrentMusic().firstOrNull()?.music?.duration ?: 0
        val currentProgress: Int = playerRepository.getCurrentProgress().firstOrNull() ?: 0

        /*
        Token is valid if the remaining millis doesn't surpass the duration of the token.
        The minimum value to check in the future is 2 minutes.
         */
        val remainingMillis = max(2.minutes.inWholeMilliseconds, currentMusicDuration - currentProgress)

        return isValidInNearFuture(inNear = remainingMillis.milliseconds)
    }

    /**
     * Checks if the token is still valid in the near future (sometimes ahead or the duration of the song).
     * If not, it generates a new token and indicate the user to reload the music.
     */
    suspend fun shouldReloadMusic(): Boolean {
        val isTokenValid = isTokenValid()

        if (!isTokenValid) {
            playerToken = playerRepository.getPlayerToken().getOrNull()
        }
        return !isTokenValid
    }

    /**
     * Given a duration to add to the current instant,
     * checks if the token will still be valid in this near future.
     */
    private fun isValidInNearFuture(
        inNear: Duration,
    ): Boolean {
        val nearFuture = Clock.System.now().plus(inNear)

        return playerToken?.expireAt?.let { expireAt ->
            expireAt > nearFuture
        } ?: false
    }

    /**
     * Retrieves the updated token. Checks if the token will be valid in the duration of a song.
     */
    suspend fun getUpdatedToken(music: Music): PlayerToken? {
        if (isValidInNearFuture(music.duration.milliseconds)) return playerToken

        playerToken = playerRepository.getPlayerToken().getOrNull()
        return playerToken
    }

    suspend fun getMusicUrl(
        token: PlayerToken,
        remoteId: String,
    ): String =
        "${cloudPreferencesRepository.observeUrl().firstOrNull().orEmpty()}/music/token?token=${token.token}&musicId=$remoteId"
}