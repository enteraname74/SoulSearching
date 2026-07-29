package com.github.enteraname74.soulsearching.features.playback.player

import com.github.enteraname74.domain.model.Music
import com.github.enteraname74.domain.util.WorkDispatcher
import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLAudioElement
import kotlin.js.unsafeCast

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
class SoulSearchingWebPlayerImpl(
    workDispatcher: WorkDispatcher,
    private val signedPlaybackUrlProvider: SignedPlaybackUrlProvider,
) : SoulSearchingPlayer {
    private val audio: HTMLAudioElement = document
        .createElement("audio")
        .unsafeCast<HTMLAudioElement>()
    private val workScope = CoroutineScope(workDispatcher.dispatcher)

    private var currentSource: String? = null
    private var sourceVersion: Int = 0
    private var suppressedPauseEvents: Int = 0

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
            if (audio.ended || suppressedPauseEvents > 0) {
                suppressedPauseEvents = (suppressedPauseEvents - 1).coerceAtLeast(0)
                return@addEventListener
            }

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

    override suspend fun setMusic(music: Music) {
        val version = ++sourceVersion
        val token = signedPlaybackUrlProvider.getUpdatedToken()

        if (version != sourceVersion) return

        if (token == null || music.remoteId == null) {
            listener?.onError()
            return
        }

        val musicUrl = signedPlaybackUrlProvider.getMusicUrl(
            token = token,
            remoteId = music.remoteId.orEmpty(),
        )

        if (version != sourceVersion) return
        if (currentSource == musicUrl) return

        pauseSilently()
        audio.currentTime = 0.0
        currentSource = musicUrl
        audio.src = musicUrl
        audio.load()
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

    override suspend fun getState(): SoulSearchingPlayer.State =
        when {
            currentSource == null -> SoulSearchingPlayer.State.Idle
            isPlaying() -> SoulSearchingPlayer.State.Playing
            else -> SoulSearchingPlayer.State.Paused
        }

    override suspend fun dismiss() {
        sourceVersion++
        pauseSilently()
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

    private fun pauseSilently() {
        if (!audio.paused) {
            suppressedPauseEvents++
            audio.pause()
        }
    }

    private companion object {
        const val MILLIS_IN_SECOND = 1000.0
    }
}
