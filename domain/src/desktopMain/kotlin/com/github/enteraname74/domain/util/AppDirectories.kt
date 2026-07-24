package com.github.enteraname74.domain.util

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
        get() = if (AppEnvironment.IS_IN_DEVELOPMENT) "$APP_ID/dev" else APP_ID

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
     * XDG_MUSIC_DIR may be supplied by the environment; ~/Music is the fallback.
     */
    val music: Path by lazy {
        System.getenv("XDG_MUSIC_DIR")
            ?.takeIf(String::isNotBlank)
            ?.let(::Path)
            ?: home.resolve("Music")
    }

    private fun xdgDirectory(environmentVariable: String, fallback: Path): Path =
        System.getenv(environmentVariable)
            ?.takeIf(String::isNotBlank)
            ?.let(::Path)
            ?: fallback
}
