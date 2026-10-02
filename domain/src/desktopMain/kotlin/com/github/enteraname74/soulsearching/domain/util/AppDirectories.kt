package com.github.enteraname74.soulsearching.domain.util

import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories

/**
 * XDG-compliant directories used by the desktop application.
 *
 * Flatpak redirects these base directories to the application's private storage.
 */
object AppDirectories {
    private const val APP_ID = "io.github.enteraname74.soulsearching"

    private val home: Path
        get() = Path(
            System.getProperty("user.home")
                ?: error("The user.home system property is unavailable")
        )

    private val appDirectoryName: String
        get() = if (AppEnvironment.IS_IN_DEVELOPMENT) "$APP_ID.dev" else APP_ID

    val data: Path by lazy {
        xdgDirectory("XDG_DATA_HOME", home.resolve(".local/share"))
            .resolve(appDirectoryName)
            .createDirectories()
    }

    val config: Path by lazy {
        xdgDirectory("XDG_CONFIG_HOME", home.resolve(".config"))
            .resolve(appDirectoryName)
            .createDirectories()
    }

    val cache: Path by lazy {
        xdgDirectory("XDG_CACHE_HOME", home.resolve(".cache"))
            .resolve(appDirectoryName)
            .createDirectories()
    }

    /**
     * Flatpak exposes this directory through the `xdg-music` filesystem grant.
     * `xdg-user-dir` resolves the user's configured directory both inside and outside Flatpak.
     * ~/Music is used when XDG user directories are unavailable.
     */
    val music: Path by lazy(::resolveMusicDirectory)

    private fun resolveMusicDirectory(): Path =
        runCatching {
            val process = ProcessBuilder("xdg-user-dir", "MUSIC")
                .redirectErrorStream(true)
                .start()
            val output = process.inputStream.bufferedReader().use { it.readText().trim() }

            if (process.waitFor() == 0 && output.isNotBlank()) {
                Path(output).takeIf { it.isAbsolute }
            } else {
                null
            }
        }.getOrNull() ?: home.resolve("Music")

    private fun xdgDirectory(environmentVariable: String, fallback: Path): Path =
        System.getenv(environmentVariable)
            ?.takeIf(String::isNotBlank)
            ?.let(::Path)
            ?: fallback
}
