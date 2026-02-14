package com.github.enteraname74.domain.di

import com.github.enteraname74.domain.usecase.album.CommonAlbumUseCase
import com.github.enteraname74.domain.usecase.album.DeleteAlbumIfEmptyUseCase
import com.github.enteraname74.domain.usecase.album.DeleteAlbumUseCase
import com.github.enteraname74.domain.usecase.album.GetCorrespondingAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.domain.usecase.artist.DeleteArtistUseCase
import com.github.enteraname74.domain.usecase.cover.CommonCoverUseCase
import com.github.enteraname74.domain.usecase.folder.CommonFolderUseCase
import com.github.enteraname74.domain.usecase.lyrics.CommonLyricsUseCase
import com.github.enteraname74.domain.usecase.month.GetMonthMusicListUseCase
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.domain.usecase.music.DeleteMusicUseCase
import com.github.enteraname74.domain.usecase.music.GetAllMusicsSortedUseCase
import com.github.enteraname74.domain.usecase.music.IsMusicInFavoritePlaylistUseCase
import com.github.enteraname74.domain.usecase.music.ToggleMusicFavoriteStatusUseCase
import com.github.enteraname74.domain.usecase.musicartist.CommonMusicArtistUseCase
import com.github.enteraname74.domain.usecase.musicfolder.GetAllMusicFolderListUseCase
import com.github.enteraname74.domain.usecase.musicfolder.GetMusicFolderListUseCase
import com.github.enteraname74.domain.usecase.musicplaylist.CommonMusicPlaylistUseCase
import com.github.enteraname74.domain.usecase.playlist.CommonPlaylistUseCase
import com.github.enteraname74.domain.usecase.playlist.GetAllPlaylistWithMusicsSortedUseCase
import com.github.enteraname74.domain.usecase.quickaccess.GetAllQuickAccessElementsUseCase
import com.github.enteraname74.domain.usecase.release.CommonReleaseUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    // USE CASES
    // Album
    factoryOf(::CommonAlbumUseCase)
    factoryOf(::DeleteAlbumIfEmptyUseCase)
    factoryOf(::DeleteAlbumUseCase)
    factoryOf(::GetCorrespondingAlbumUseCase)

    // Artist
    factoryOf(::CommonArtistUseCase)
    factoryOf(::DeleteArtistUseCase)

    // Folder
    factoryOf(::CommonFolderUseCase)

    // ImageCover
    factoryOf(::CommonCoverUseCase)

    // Lyrics
    factoryOf(::CommonLyricsUseCase)

    // MonthMusic
    factoryOf(::GetMonthMusicListUseCase)

    // Music
    factoryOf(::CommonMusicUseCase)
    factoryOf(::DeleteMusicUseCase)
    factoryOf(::GetAllMusicsSortedUseCase)
    factoryOf(::IsMusicInFavoritePlaylistUseCase)
    factoryOf(::ToggleMusicFavoriteStatusUseCase)

    // MusicArtist
    factoryOf(::CommonMusicArtistUseCase)

    // MusicFolder
    factoryOf(::GetAllMusicFolderListUseCase)
    factoryOf(::GetMusicFolderListUseCase)

    // MusicPlaylist
    factoryOf(::CommonMusicPlaylistUseCase)

    // Playlist
    factoryOf(::CommonPlaylistUseCase)
    factoryOf(::GetAllPlaylistWithMusicsSortedUseCase)

    // QuickAccess
    factoryOf(::GetAllQuickAccessElementsUseCase)

    // Release
    factoryOf(::CommonReleaseUseCase)
}