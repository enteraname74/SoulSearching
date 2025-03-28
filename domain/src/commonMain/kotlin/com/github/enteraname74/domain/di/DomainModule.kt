package com.github.enteraname74.domain.di

import com.github.enteraname74.domain.usecase.album.*
import com.github.enteraname74.domain.usecase.albumartist.GetAllAlbumArtistUseCase
import com.github.enteraname74.domain.usecase.albumartist.UpsertAlbumArtistUseCase
import com.github.enteraname74.domain.usecase.albumartist.UpsertAllAlbumArtistUseCase
import com.github.enteraname74.domain.usecase.artist.*
import com.github.enteraname74.domain.usecase.cover.DeleteCoverUseCase
import com.github.enteraname74.domain.usecase.cover.IsCoverUsedUseCase
import com.github.enteraname74.domain.usecase.cover.UpsertImageCoverUseCase
import com.github.enteraname74.domain.usecase.folder.*
import com.github.enteraname74.domain.usecase.lyrics.GetLyricsOfSongUseCase
import com.github.enteraname74.domain.usecase.month.GetAllMonthMusicUseCase
import com.github.enteraname74.domain.usecase.month.GetMonthMusicListUseCase
import com.github.enteraname74.domain.usecase.music.*
import com.github.enteraname74.domain.usecase.musicalbum.*
import com.github.enteraname74.domain.usecase.musicartist.GetAllMusicArtistUseCase
import com.github.enteraname74.domain.usecase.musicartist.UpsertAllMusicArtistsUseCase
import com.github.enteraname74.domain.usecase.musicartist.UpsertMusicIntoArtistUseCase
import com.github.enteraname74.domain.usecase.musicfolder.GetAllMusicFolderListUseCase
import com.github.enteraname74.domain.usecase.musicfolder.GetMusicFolderListUseCase
import com.github.enteraname74.domain.usecase.musicplaylist.DeleteMusicFromPlaylistUseCase
import com.github.enteraname74.domain.usecase.musicplaylist.UpsertMusicIntoPlaylistUseCase
import com.github.enteraname74.domain.usecase.playlist.*
import com.github.enteraname74.domain.usecase.quickaccess.GetAllQuickAccessElementsUseCase
import com.github.enteraname74.domain.usecase.release.FetchLatestReleaseUseCase
import com.github.enteraname74.domain.usecase.release.GetLatestReleaseUseCase
import com.github.enteraname74.domain.usecase.release.GetLatestViewedReleaseUseCase
import com.github.enteraname74.domain.usecase.release.SetLatestViewedReleaseUseCase
import com.github.enteraname74.domain.usecase.release.DeleteLatestReleaseUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule = module {
    // USE CASES
    // Album
    singleOf(::DeleteAlbumIfEmptyUseCase)
    singleOf(::DeleteAlbumUseCase)
    singleOf(::DeleteAllAlbumsUseCase)
    singleOf(::GetAlbumsNameFromSearchStringUseCase)
    singleOf(::GetAlbumsOfArtistUseCase)
    singleOf(::GetAlbumsWithMusicsOfArtistUseCase)
    singleOf(::GetAlbumUseCase)
    singleOf(::GetAlbumWithMusicsUseCase)
    singleOf(::GetAllAlbumsUseCase)
    singleOf(::GetAllAlbumsWithMusicsFromQuickAccessUseCase)
    singleOf(::GetAllAlbumsWithArtistUseCase)
    singleOf(::GetAllAlbumsWithMusicsUseCase)
    singleOf(::GetAllAlbumWithMusicsSortedUseCase)
    singleOf(::GetCorrespondingAlbumUseCase)
    singleOf(::GetDuplicatedAlbumUseCase)
    singleOf(::GetNumberOfAlbumsWithCoverIdUseCase)
    singleOf(::UpdateAlbumCoverUseCase)
    singleOf(::UpdateAlbumNbPlayedUseCase)
    singleOf(::UpsertAlbumUseCase)
    singleOf(::UpsertAllAlbumsUseCase)

    // AlbumArtist
    singleOf(::GetAllAlbumArtistUseCase)
    singleOf(::UpsertAlbumArtistUseCase)
    singleOf(::UpsertAllAlbumArtistUseCase)

    // Artist
    singleOf(::DeleteAllArtistsUseCase)
    singleOf(::DeleteArtistIfEmptyUseCase)
    singleOf(::DeleteArtistUseCase)
    singleOf(::GetAllArtistsUseCase)
    singleOf(::GetAllArtistsWithNameUseCase)
    singleOf(::GetAllArtistWithMusicsFromQuickAccessUseCase)
    singleOf(::GetAllArtistWithMusicsSortedByMostSongsUseCase)
    singleOf(::GetAllArtistWithMusicsSortedUseCase)
    singleOf(::GetAllArtistWithMusicsUseCase)
    singleOf(::GetArtistFromNameUseCase)
    singleOf(::GetArtistsNameFromSearchStringUseCase)
    singleOf(::GetArtistsOfMusicUseCase)
    singleOf(::GetArtistWithMusicsUseCase)
    singleOf(::GetDuplicatedArtistUseCase)
    singleOf(::UpdateArtistCoverUseCase)
    singleOf(::UpdateArtistNbPlayedUseCase)
    singleOf(::UpsertAllArtistsUseCase)
    singleOf(::UpsertArtistUseCase)

    // Folder
    singleOf(::DeleteAllFoldersUseCase)
    singleOf(::DeleteFolderUseCase)
    singleOf(::GetAllFoldersUseCase)
    singleOf(::GetHiddenFoldersPathUseCase)
    singleOf(::UpsertAllFoldersUseCase)
    singleOf(::UpsertFolderUseCase)

    // ImageCover
    singleOf(::DeleteCoverUseCase)
    singleOf(::IsCoverUsedUseCase)
    singleOf(::UpsertImageCoverUseCase)

    // Lyrics
    singleOf(::GetLyricsOfSongUseCase)

    // MonthMusic
    singleOf(::GetAllMonthMusicUseCase)
    singleOf(::GetMonthMusicListUseCase)

    // Music
    singleOf(::DeleteAllMusicsUseCase)
    singleOf(::DeleteMusicUseCase)
    singleOf(::GetAllMusicFromFolderPathUseCase)
    singleOf(::GetAllMusicFromQuickAccessUseCase)
    singleOf(::GetAllMusicsSortedUseCase)
    singleOf(::GetAllMusicUseCase)
    singleOf(::GetMusicUseCase)
    singleOf(::IsMusicAlreadySavedUseCase)
    singleOf(::IsMusicInFavoritePlaylistUseCase)
    singleOf(::ToggleMusicFavoriteStatusUseCase)
    singleOf(::UpdateAlbumOfMusicUseCase)
    singleOf(::UpdateMusicNbPlayedUseCase)
    singleOf(::UpsertAllMusicsUseCase)
    singleOf(::UpsertMusicUseCase)

    // MusicAlbum
    singleOf(::GetAlbumIdFromMusicIdUseCase)
    singleOf(::GetAllMusicAlbumUseCase)
    singleOf(::UpdateMusicsAlbumUseCase)
    singleOf(::UpsertAllMusicAlbumUseCase)
    singleOf(::UpsertMusicIntoAlbumUseCase)

    // MusicArtist
    singleOf(::GetAllMusicArtistUseCase)
    singleOf(::UpsertAllMusicArtistsUseCase)
    singleOf(::UpsertMusicIntoArtistUseCase)

    // MusicFolder
    singleOf(::GetAllMusicFolderListUseCase)
    singleOf(::GetMusicFolderListUseCase)

    // MusicPlaylist
    singleOf(::DeleteMusicFromPlaylistUseCase)
    singleOf(::UpsertMusicIntoPlaylistUseCase)

    // Playlist
    singleOf(::DeleteAllPlaylistsUseCase)
    singleOf(::DeletePlaylistUseCase)
    singleOf(::GetAllPlaylistsUseCase)
    singleOf(::GetAllPlaylistWithMusicsNumberFromQuickAccessUseCase)
    singleOf(::GetAllPlaylistWithMusicsSortedUseCase)
    singleOf(::GetAllPlaylistWithMusicsUseCase)
    singleOf(::GetFavoritePlaylistWithMusicsUseCase)
    singleOf(::GetPlaylistUseCase)
    singleOf(::GetPlaylistWithMusicsUseCase)
    singleOf(::GetSelectablePlaylistWithMusicsForMusicUseCase)
    singleOf(::UpdatePlaylistNbPlayedUseCase)
    singleOf(::UpsertAllPlaylistsUseCase)
    singleOf(::UpsertPlaylistUseCase)

    // QuickAccess
    singleOf(::GetAllQuickAccessElementsUseCase)

    // Release
    singleOf(::FetchLatestReleaseUseCase)
    singleOf(::GetLatestReleaseUseCase)
    singleOf(::GetLatestViewedReleaseUseCase)
    singleOf(::SetLatestViewedReleaseUseCase)
    singleOf(::DeleteLatestReleaseUseCase)
}