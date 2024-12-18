package com.github.enteraname74.domain.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import com.github.enteraname74.domain.usecase.album.*
import com.github.enteraname74.domain.usecase.albumartist.*
import com.github.enteraname74.domain.usecase.artist.*
import com.github.enteraname74.domain.usecase.folder.*
import com.github.enteraname74.domain.usecase.cover.*
import com.github.enteraname74.domain.usecase.lyrics.*
import com.github.enteraname74.domain.usecase.music.*
import com.github.enteraname74.domain.usecase.musicalbum.*
import com.github.enteraname74.domain.usecase.musicartist.*
import com.github.enteraname74.domain.usecase.musicplaylist.*
import com.github.enteraname74.domain.usecase.playlist.*
import com.github.enteraname74.domain.usecase.quickaccess.*
import com.github.enteraname74.domain.usecase.month.*
import com.github.enteraname74.domain.usecase.musicfolder.*

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
    singleOf(::GetAllArtistWithMusicsFromQuickAccessUseCase)
    singleOf(::GetAllArtistWithMusicsSortedByMostSongsUseCase)
    singleOf(::GetAllArtistWithMusicsSortedUseCase)
    singleOf(::GetAllArtistWithMusicsUseCase)
    singleOf(::GetArtistFromNameUseCase)
    singleOf(::GetArtistsNameFromSearchStringUseCase)
    singleOf(::GetArtistsOfMusicUseCase)
    singleOf(::GetArtistWithMusicsUseCase)
    singleOf(::GetCorrespondingArtistUseCase)
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
    singleOf(::SaveMusicAndCreateMissingArtistAndAlbumUseCase)
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
    singleOf(::GetArtistIdFromMusicIdUseCase)
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
}