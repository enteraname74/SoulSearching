package com.github.enteraname74.localdb.migration

import androidx.room3.RoomDatabase
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.async.executeSQL
import com.github.enteraname74.localdb.model.toRoom
import com.github.enteraname74.localdb.settings.LegacySettingsReader

internal class EndMigrationCallback(
    private val legacySettingsReader: LegacySettingsReader,
) : RoomDatabase.Callback() {
    override suspend fun onOpen(connection: SQLiteConnection) {
        super.onOpen(connection)
        val settings = legacySettingsReader.read().toRoom()
        val cover = settings.cover
        val release = settings.release
        connection.executeSQL(
            """
            INSERT OR IGNORE INTO RoomSoulSearchingSettings (
                id, hasMusicsBeenFetched, isMusicFileModificationOn,
                musicSort_type, musicSort_direction, albumSort_type, albumSort_direction,
                artistSort_type, artistSort_direction, playlistSort_type, playlistSort_direction,
                collectionSort_type, collectionSort_direction,
                player_isPlayerSwipeEnabled, player_soulMixTotalByList, player_isRewindEnabled,
                player_isMinimisedSongProgressionShown, player_playerVolume,
                player_isRemoteLyricsFetchEnabled, player_sharedPlayedListMusicUpdateMillis,
                colorTheme_type, colorTheme_dynamicPlayerTheme, colorTheme_dynamicPlaylistTheme,
                colorTheme_dynamicOtherViewsTheme, colorTheme_forceDarkTheme, colorTheme_forceLightTheme,
                colorTheme_usedTheme, colorTheme_paletteSeed, mainPage_isQuickAccessShown, mainPage_arePlaylistsShown,
                mainPage_areCollectionsShown, mainPage_areAlbumsShown, mainPage_areArtistsShown,
                mainPage_areMusicsByMonthsShown, mainPage_areMusicsByFoldersShown,
                mainPage_isUsingVerticalAccessBar, mainPage_initialTab, cover_isActivated, cover_folderModePath,
                cover_fileModePath, cover_coverFileName, cover_fileExtension,
                cover_whiteSpaceRuleActivated, cover_whiteSpaceReplacement, cover_lowerCaseRule,
                cover_mode, desktop_windowHeight, desktop_windowWidth, desktop_isFullscreen,
                release_isFetchReleaseFromGithubEnabled, release_shouldShowReleaseBottomEnableHint,
                release_latestViewedReleaseTag, release_latestReleaseName, release_latestReleaseTag,
                release_latestReleaseGithubUrl, release_latestReleaseChangelog,
                album_shouldShowTrackPositionInAlbumView
            ) VALUES (
                ${settings.id.sql()}, ${settings.hasMusicsBeenFetched.sql()}, ${settings.isMusicFileModificationOn.sql()},
                ${settings.musicSort.type.value}, ${settings.musicSort.direction.value}, ${settings.albumSort.type.value}, ${settings.albumSort.direction.value},
                ${settings.artistSort.type.value}, ${settings.artistSort.direction.value}, ${settings.playlistSort.type.value}, ${settings.playlistSort.direction.value},
                ${settings.collectionSort.type.value}, ${settings.collectionSort.direction.value},
                ${settings.player.isPlayerSwipeEnabled.sql()}, ${settings.player.soulMixTotalByList}, ${settings.player.isRewindEnabled.sql()},
                ${settings.player.isMinimisedSongProgressionShown.sql()}, ${settings.player.playerVolume},
                ${settings.player.isRemoteLyricsFetchEnabled.sql()}, ${settings.player.sharedPlayedListMusicUpdateMillis},
                ${settings.colorTheme.type.value}, ${settings.colorTheme.dynamicPlayerTheme.sql()}, ${settings.colorTheme.dynamicPlaylistTheme.sql()},
                ${settings.colorTheme.dynamicOtherViewsTheme.sql()}, ${settings.colorTheme.forceDarkTheme.sql()}, ${settings.colorTheme.forceLightTheme.sql()},
                ${settings.colorTheme.usedTheme.value.sql()}, ${settings.colorTheme.paletteSeed.sql()}, ${settings.mainPage.isQuickAccessShown.sql()}, ${settings.mainPage.arePlaylistsShown.sql()},
                ${settings.mainPage.areCollectionsShown.sql()}, ${settings.mainPage.areAlbumsShown.sql()}, ${settings.mainPage.areArtistsShown.sql()},
                ${settings.mainPage.areMusicsByMonthsShown.sql()}, ${settings.mainPage.areMusicsByFoldersShown.sql()},
                ${settings.mainPage.isUsingVerticalAccessBar.sql()}, ${settings.mainPage.initialTab.sql()}, ${cover.isActivated.sql()}, ${cover.folderModePath.sql()},
                ${cover.fileModePath.sql()}, ${cover.coverFileName.sql()}, ${cover.fileExtension.sql()},
                ${cover.whiteSpaceRuleActivated.sql()}, ${cover.whiteSpaceReplacement.sql()}, ${cover.lowerCaseRule.sqlNullable()},
                ${cover.mode.name.sql()}, ${settings.desktop.windowHeight}, ${settings.desktop.windowWidth}, ${settings.desktop.isFullscreen.sql()},
                ${release.isFetchReleaseFromGithubEnabled.sql()}, ${release.shouldShowReleaseBottomEnableHint.sql()},
                ${release.latestViewedReleaseTag.sql()}, ${release.latestReleaseName.sql()}, ${release.latestReleaseTag.sql()},
                ${release.latestReleaseGithubUrl.sql()}, ${release.latestReleaseChangelog.sql()},
                ${settings.album.shouldShowTrackPositionInAlbumView.sql()}
            )
            """.trimIndent()
        )
        legacySettingsReader.clear()
    }
}

private fun Boolean.sql() = if (this) "1" else "0"
private fun Boolean?.sqlNullable() = this?.sql() ?: "NULL"
private fun String?.sql() = this?.let { "'${it.replace("'", "''")}'" } ?: "NULL"
