package com.github.enteraname74.soulsearching.features.playback.environment

import android.content.ComponentName
import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.github.enteraname74.soulsearching.features.playback.PlayerLibraryService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@OptIn(UnstableApi::class)
class MediaServiceConnector(
    context: Context,
) {
    private val appContext = context.applicationContext
    private val mutex = Mutex()
    private var browser: MediaBrowser? = null

    suspend fun ensureMediaServiceConnected() {
        mutex.withLock {
            browser?.let { return }

            withContext(Dispatchers.Main) {
                val sessionToken = SessionToken(
                    appContext,
                    ComponentName(appContext, PlayerLibraryService::class.java),
                )

                MediaBrowser.Builder(appContext, sessionToken)
                    .buildAsync()
                    .await()
                    .also { browser = it }
            }
        }
    }

    suspend fun release() {
        mutex.withLock {
            val browserToRelease = browser ?: return
            browser = null

            withContext(Dispatchers.Main) {
                browserToRelease.release()
            }
        }
    }
}
