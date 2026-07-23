package com.github.enteraname74.soulsearching.features.playback.notification

import com.github.enteraname74.domain.model.Cover
import com.github.enteraname74.domain.repository.CloudPreferencesRepository
import com.github.enteraname74.domain.util.WorkDispatcher
import com.github.enteraname74.soulsearching.features.playback.manager.PlaybackManager
import com.github.enteraname74.soulsearching.features.playback.model.UpdateData
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.net.URI

class SoulSearchingDesktopNotification(
    private val cloudPreferencesRepository: CloudPreferencesRepository,
    workDispatcher: WorkDispatcher,
) : SoulSearchingNotification, KoinComponent {
    private val playbackManager: PlaybackManager by inject()
    private val mprisMediaSession: MprisMediaSession by lazy {
        MprisMediaSession(
            playbackManager = playbackManager,
            workDispatcher = workDispatcher,
        )
    }

    override suspend fun update(updateData: UpdateData) {
        mprisMediaSession.update(
            updateData = updateData,
            artUrl = updateData.music.cover.toArtUrl(),
        )
    }

    override fun dismiss() {
        mprisMediaSession.dismiss()
    }

    private suspend fun Cover.toArtUrl(): String? =
        when (this) {
            is Cover.CoverFile -> initialCoverPath
                ?.takeIf { it.isNotBlank() }
                ?.let { path -> File(path).toURI().toString() }

            is Cover.Url -> url
                .takeIf { it.isNotBlank() }
                ?.toAbsoluteUrl()
        }

    private suspend fun String.toAbsoluteUrl(): String? =
        when {
            startsWith("file://") -> this
            startsWith("http://") || startsWith("https://") -> this
            else -> runCatching {
                val baseUrl = cloudPreferencesRepository.observeUrl().firstOrNull().orEmpty()
                URI(baseUrl.ensureTrailingSlash()).resolve(this).toString()
            }.getOrNull()
        }

    private fun String.ensureTrailingSlash(): String =
        if (endsWith("/")) this else "$this/"
}
