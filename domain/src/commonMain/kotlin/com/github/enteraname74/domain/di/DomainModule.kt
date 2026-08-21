package com.github.enteraname74.domain.di

import com.github.enteraname74.domain.usecase.DeleteEmptyAlbumsAndArtistsUseCase
import com.github.enteraname74.domain.usecase.album.CloudAlbumToAlbumUseCase
import com.github.enteraname74.domain.usecase.album.CommonAlbumUseCase
import com.github.enteraname74.domain.usecase.album.DeleteAlbumIfEmptyUseCase
import com.github.enteraname74.domain.usecase.album.DeleteAlbumUseCase
import com.github.enteraname74.domain.usecase.album.GetCorrespondingAlbumUseCase
import com.github.enteraname74.domain.usecase.artist.CloudArtistToArtistUseCase
import com.github.enteraname74.domain.usecase.artist.CommonArtistUseCase
import com.github.enteraname74.domain.usecase.artist.DeleteArtistUseCase
import com.github.enteraname74.domain.usecase.cloud.CommonCloudPreferencesUseCase
import com.github.enteraname74.domain.usecase.cloud.HasValidCloudInformationUseCase
import com.github.enteraname74.domain.usecase.cover.CommonCoverUseCase
import com.github.enteraname74.domain.usecase.folder.CommonFolderUseCase
import com.github.enteraname74.domain.usecase.lyrics.CommonLyricsUseCase
import com.github.enteraname74.domain.usecase.music.CloudMusicToMusicUseCase
import com.github.enteraname74.domain.usecase.music.CommonMusicUseCase
import com.github.enteraname74.domain.usecase.music.DeleteMusicUseCase
import com.github.enteraname74.domain.usecase.music.IsMusicInFavoritePlaylistUseCase
import com.github.enteraname74.domain.usecase.music.ObserveDataChangedForCloudSync
import com.github.enteraname74.domain.usecase.music.RemoveLocallyOrDeleteMusicUseCase
import com.github.enteraname74.domain.usecase.music.SyncDataWithCloudUseCase
import com.github.enteraname74.domain.usecase.music.ToggleMusicFavoriteStatusUseCase
import com.github.enteraname74.domain.usecase.music.UpdateMusicToCloudUseCase
import com.github.enteraname74.domain.usecase.music.UploadMusicToCloudUseCase
import com.github.enteraname74.domain.usecase.musicartist.CommonMusicArtistUseCase
import com.github.enteraname74.domain.usecase.musicplaylist.CommonMusicPlaylistUseCase
import com.github.enteraname74.domain.usecase.player.AddMusicsToSharedPlayedListUseCase
import com.github.enteraname74.domain.usecase.player.CreateSharedPlayedListUseCase
import com.github.enteraname74.domain.usecase.player.FetchPlayedListMusicsUseCase
import com.github.enteraname74.domain.usecase.player.JoinSharedPlayedListUseCase
import com.github.enteraname74.domain.usecase.player.RegisterSharedPlayedListEventsListenerUseCase
import com.github.enteraname74.domain.usecase.player.RemoveMusicsFromSharedPlayedListUseCase
import com.github.enteraname74.domain.usecase.player.SyncMusicForPlayerIfNeededUseCase
import com.github.enteraname74.domain.usecase.player.SyncPlayedListInformationUseCase
import com.github.enteraname74.domain.usecase.player.SyncPlayedListMusicsUseCase
import com.github.enteraname74.domain.usecase.playlist.CommonPlaylistUseCase
import com.github.enteraname74.domain.usecase.playlist.UploadPlaylistToCloudUseCase
import com.github.enteraname74.domain.usecase.playlist.UpsertCloudPlaylistUseCase
import com.github.enteraname74.domain.usecase.quickaccess.GetAllQuickAccessElementsUseCase
import com.github.enteraname74.domain.usecase.release.CommonReleaseUseCase
import com.github.enteraname74.domain.usecase.user.ClearUserDataUseCase
import com.github.enteraname74.domain.usecase.user.CommonUserUseCase
import com.github.enteraname74.domain.usecase.user.DeleteSavedRemoteDataUseCase
import com.github.enteraname74.domain.usecase.user.DeleteUserUseCase
import com.github.enteraname74.domain.usecase.user.LogoutFromCloudUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule: Module = module {
    // USE CASES
    // Album
    factoryOf(::CommonAlbumUseCase)
    factoryOf(::DeleteAlbumIfEmptyUseCase)
    factoryOf(::DeleteAlbumUseCase)
    factoryOf(::GetCorrespondingAlbumUseCase)
    factoryOf(::CloudAlbumToAlbumUseCase)

    // Artist
    factoryOf(::CommonArtistUseCase)
    factoryOf(::DeleteArtistUseCase)
    factoryOf(::CloudArtistToArtistUseCase)

    // Folder
    factoryOf(::CommonFolderUseCase)

    // ImageCover
    factoryOf(::CommonCoverUseCase)

    // Lyrics
    factoryOf(::CommonLyricsUseCase)

    // Music
    factoryOf(::CommonMusicUseCase)
    factoryOf(::DeleteMusicUseCase)
    factoryOf(::RemoveLocallyOrDeleteMusicUseCase)
    factoryOf(::IsMusicInFavoritePlaylistUseCase)
    singleOf(::ObserveDataChangedForCloudSync)
    factoryOf(::ToggleMusicFavoriteStatusUseCase)
    factoryOf(::CloudMusicToMusicUseCase)
    factoryOf(::UploadMusicToCloudUseCase)
    factoryOf(::UpdateMusicToCloudUseCase)
    singleOf(::SyncDataWithCloudUseCase)

    factoryOf(::DeleteEmptyAlbumsAndArtistsUseCase)

    // MusicArtist
    factoryOf(::CommonMusicArtistUseCase)

    // MusicPlaylist
    factoryOf(::CommonMusicPlaylistUseCase)

    // Playlist
    factoryOf(::CommonPlaylistUseCase)
    factoryOf(::UpsertCloudPlaylistUseCase)
    factoryOf(::UploadPlaylistToCloudUseCase)

    // QuickAccess
    factoryOf(::GetAllQuickAccessElementsUseCase)

    // Release
    factoryOf(::CommonReleaseUseCase)

    // User
    factoryOf(::CommonUserUseCase)
    factoryOf(::LogoutFromCloudUseCase)
    factoryOf(::ClearUserDataUseCase)
    factoryOf(::DeleteSavedRemoteDataUseCase)
    factoryOf(::DeleteUserUseCase)

    // CloudPreferences
    factoryOf(::CommonCloudPreferencesUseCase)

    // Cloud
    factoryOf(::HasValidCloudInformationUseCase)

    // Player
    factoryOf(::CreateSharedPlayedListUseCase)
    factoryOf(::FetchPlayedListMusicsUseCase)
    factoryOf(::SyncMusicForPlayerIfNeededUseCase)
    factoryOf(::SyncPlayedListMusicsUseCase)
    factoryOf(::SyncPlayedListInformationUseCase)
    factoryOf(::RegisterSharedPlayedListEventsListenerUseCase)
    factoryOf(::AddMusicsToSharedPlayedListUseCase)
    factoryOf(::RemoveMusicsFromSharedPlayedListUseCase)
    factoryOf(::JoinSharedPlayedListUseCase)
}
