package com.github.enteraname74.soulsearching.features.playback.player

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.model.player.PlayerToken
import com.github.enteraname74.domain.repository.CloudPreferencesRepository
import com.github.enteraname74.domain.repository.PlayerRepository
import com.github.enteraname74.domain.util.WorkDispatcher
import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLAudioElement
import kotlin.js.unsafeCast
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
class SoulSearchingWebPlayerImpl(
    private val playerRepository: PlayerRepository,
    private val cloudPreferencesRepository: CloudPreferencesRepository,
    workDispatcher: WorkDispatcher,
) : SoulSearchingPlayer {
    private val audio: HTMLAudioElement = document
        .createElement("audio")
        .unsafeCast<HTMLAudioElement>()
    private val workScope = CoroutineScope(workDispatcher.dispatcher)

    private var playerToken: PlayerToken? = null
    private var currentSource: String? = null

    override var listener: SoulSearchingPlayer.Listener? = null

    init {
        audio.preload = "auto"
        audio.addEventListener("ended") {
            workScope.launch {
                listener?.onCompletion()
            }
        }
        audio.addEventListener("play") {
            workScope.launch {
                listener?.onPlay()
            }
        }
        audio.addEventListener("pause") {
            workScope.launch {
                listener?.onPause()
            }
        }
        audio.addEventListener("error") {
            workScope.launch {
                listener?.onError()
            }
        }
    }

    private suspend fun getUpdatedToken(): PlayerToken? {
        val nearFuture = Clock.System.now().plus(2.minutes)

        val isTokenValid = playerToken?.expireAt?.let { expireAt ->
            expireAt < nearFuture
        } ?: false

        if (isTokenValid) return playerToken

        playerToken = playerRepository.getPlayerToken().getOrNull()
        return playerToken
    }

    private suspend fun getMusicUrl(
        token: PlayerToken,
        remoteId: String,
    ): String =
        "${cloudPreferencesRepository.observeUrl().firstOrNull().orEmpty()}/music/token?token=${token.token}&musicId=$remoteId"

    override suspend fun setMusic(music: Music) {
        val token = getUpdatedToken()

        if (token == null || music.remoteId == null) {
            listener?.onError()
            return
        }

        val musicUrl = getMusicUrl(
            token = token,
            remoteId = music.remoteId.orEmpty(),
        )

        if (currentSource == musicUrl) return

        audio.pause()
        audio.currentTime = 0.0
        currentSource = musicUrl
        audio.src = musicUrl
        audio.load()
    }

    override suspend fun onlyLoadMusic(seekTo: Int) {
        audio.currentTime = seekTo / MILLIS_IN_SECOND
    }

    override suspend fun launchMusic() {
        play()
    }

    override suspend fun play() {
        try {
            audio.play().catch {
                null
            }
        } catch (_: Throwable) {
            listener?.onError()
        }
    }

    override suspend fun pause() {
        audio.pause()
    }

    override suspend fun seekToPosition(millis: Int) {
        audio.currentTime = millis / MILLIS_IN_SECOND
    }

    override suspend fun isPlaying(): Boolean =
        !audio.paused

    override suspend fun dismiss() {
        audio.pause()
        audio.removeAttribute("src")
        currentSource = null
        audio.load()
    }

    override suspend fun getProgress(): Int =
        (audio.currentTime * MILLIS_IN_SECOND).toInt()

    override suspend fun getMusicDuration(): Int {
        val duration = audio.duration
        return if (duration.isFinite()) {
            (duration * MILLIS_IN_SECOND).toInt()
        } else {
            0
        }
    }

    override suspend fun setPlayerVolume(volume: Float) {
        audio.volume = volume
            .coerceIn(0f, 1f)
            .toDouble()
    }

    private companion object {
        const val MILLIS_IN_SECOND = 1000.0
    }
}
