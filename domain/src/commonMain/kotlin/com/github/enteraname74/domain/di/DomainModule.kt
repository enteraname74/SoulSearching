package com.github.enteraname74.domain.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import com.github.enteraname74.domain.usecase.album.*
import com.github.enteraname74.domain.usecase.albumartist.*
import com.github.enteraname74.domain.usecase.artist.*
import com.github.enteraname74.domain.usecase.folder.*
import com.github.enteraname74.domain.usecase.imagecover.*
import com.github.enteraname74.domain.usecase.lyrics.*
import com.github.enteraname74.domain.usecase.music.*
import com.github.enteraname74.domain.usecase.musicalbum.*
import com.github.enteraname74.domain.usecase.musicartist.*
import com.github.enteraname74.domain.usecase.musicplaylist.*
import com.github.enteraname74.domain.usecase.playlist.*
import com.github.enteraname74.domain.usecase.quickaccess.*

val domainModule = module {
    // USE CASES
    // Album
    singleOf(::DeleteAlbumIfEmptyUseCase)
    singleOf(::DeleteAlbumUseCase)
    singleOf(::GetAlbumsNameFromSearchString)
    singleOf(::GetAlbumsOfArtistsUseCase)
    singleOf(::GetAlbumUseCase)
    singleOf(::GetAlbumWithMusicsUseCase)
    singleOf(::GetAllAlbumsUseCase)
    singleOf(::GetAllAlbumsWithArtistFromQuickAccessUseCase)
    singleOf(::GetAllAlbumsWithMusicsUseCase)
    singleOf(::GetAllAlbumWithMusicsSortedUseCase)
    singleOf(::GetCorrespondingAlbumUseCase)
    singleOf(::GetDuplicatedAlbumUseCase)
    singleOf(::GetNumberOfAlbumsWithCoverIdUseCase)
    singleOf(::UpdateAlbumCoverUseCase)
    singleOf(::UpdateAlbumUseCase)
    singleOf(::UpsertAlbumUseCase)

    // AlbumArtist
    singleOf(::UpsertAlbumArtistUseCase)

    // Artist
    singleOf(::DeleteArtistIfEmptyUseCase)
    singleOf(::DeleteArtistUseCase)
    singleOf(::GetAllArtistWithMusicsFromQuickAccessUseCase)
    singleOf(::GetAllArtistWithMusicsSortedUseCase)
    singleOf(::GetArtistFromNameUseCase)
    singleOf(::GetArtistsNameFromSearchStringUseCase)
    singleOf(::GetArtistWithMusicsUseCase)
    singleOf(::GetCorrespondingArtistUseCase)
    singleOf(::GetDuplicatedArtistUseCase)
    singleOf(::UpdateArtistCoverUseCase)
    singleOf(::UpdateArtistUseCase)
    singleOf(::UpsertArtistUseCase)

    // Folder
    singleOf(::DeleteFolderUseCase)
    singleOf(::GetAllFoldersUseCase)
    singleOf(::GetHiddenFoldersPathUseCase)
    singleOf(::UpsertFolderUseCase)

    // ImageCover
    singleOf(::DeleteImageCoverUseCase)
    singleOf(::GetAllImageCoverUseCase)
    singleOf(::GetCoverOfElementUseCase)
    singleOf(::IsImageCoverUsedUseCase)
    singleOf(::UpsertImageCoverUseCase)

    // Lyrics
    singleOf(::GetLyricsOfSongUseCase)

    // Music
    singleOf(::DeleteMusicUseCase)
    singleOf(::GetAllMusicFromFolderPathUseCase)
    singleOf(::GetAllMusicFromQuickAccessUseCase)
    singleOf(::GetAllMusicsSortedUseCase)
    singleOf(::GetAllMusicUseCase)
    singleOf(::GetMusicUseCase)
    singleOf(::IsMusicAlreadySavedUseCase)
    singleOf(::IsMusicInFavoritePlaylistUseCase)
    singleOf(::SaveMusicAndCreateMissingArtistAndAlbumUseCase)
    singleOf(::SaveMusicUseCase)
    singleOf(::ToggleMusicFavoriteStatusUseCase)
    singleOf(::UpdateAlbumOfMusicUseCase)
    singleOf(::UpdateMusicUseCase)
    singleOf(::UpsertMusicUseCase)

    // MusicAlbum
    singleOf(::GetAlbumIdFromMusicIdUseCase)
    singleOf(::UpsertMusicIntoAlbumUseCase)

    // MusicArtist
    singleOf(::GetArtistIdFromMusicIdUseCase)
    singleOf(::UpsertMusicIntoArtistUseCase)

    // MusicPlaylist
    singleOf(::DeleteMusicFromPlaylistUseCase)
    singleOf(::UpsertMusicIntoPlaylistUseCase)

    // Playlist
    singleOf(::DeletePlaylistUseCase)
    singleOf(::GetAllPlaylistWithMusicsNumberFromQuickAccessUseCase)
    singleOf(::GetAllPlaylistWithMusicsSortedUseCase)
    singleOf(::GetAllPlaylistWithMusicsUseCase)
    singleOf(::GetFavoritePlaylistWithMusicsUseCase)
    singleOf(::GetPlaylistUseCase)
    singleOf(::GetPlaylistWithMusicsUseCase)
    singleOf(::GetSelectablePlaylistWithMusicsForMusic)
    singleOf(::UpsertPlaylistUseCase)

    // QuickAccess
    singleOf(::GetAllQuickAccessElementsUseCase)
}