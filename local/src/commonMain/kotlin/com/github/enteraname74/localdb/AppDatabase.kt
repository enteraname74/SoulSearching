package com.github.enteraname74.localdb

import androidx.room3.ColumnTypeConverters
import androidx.room3.ConstructedBy
import androidx.room3.DaoReturnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import androidx.room3.paging.PagingSourceDaoReturnTypeConverter
import com.github.enteraname74.domain.util.LocalDatabaseVersion
import com.github.enteraname74.localdb.converters.DurationConverters
import com.github.enteraname74.localdb.converters.InstantConverters
import com.github.enteraname74.localdb.converters.StorageTypeConverters
import com.github.enteraname74.localdb.converters.UserTypeConverters
import com.github.enteraname74.localdb.converters.UuidTypeConverters
import com.github.enteraname74.localdb.dao.AlbumDao
import com.github.enteraname74.localdb.dao.ArtistDao
import com.github.enteraname74.localdb.dao.CloudPreferencesDao
import com.github.enteraname74.localdb.dao.CoverDao
import com.github.enteraname74.localdb.dao.DeviceIdDao
import com.github.enteraname74.localdb.dao.FolderDao
import com.github.enteraname74.localdb.dao.ListeningStatisticsDao
import com.github.enteraname74.localdb.dao.MusicArtistDao
import com.github.enteraname74.localdb.dao.MusicDao
import com.github.enteraname74.localdb.dao.MusicPlaylistDao
import com.github.enteraname74.localdb.dao.PlayerMusicDao
import com.github.enteraname74.localdb.dao.PlayerMusicProgressDao
import com.github.enteraname74.localdb.dao.PlayerMusicUserDao
import com.github.enteraname74.localdb.dao.PlayerPlayedListDao
import com.github.enteraname74.localdb.dao.PlaylistDao
import com.github.enteraname74.localdb.dao.SharedPlayedListPreviewDao
import com.github.enteraname74.localdb.dao.SharedPlayedListUserDao
import com.github.enteraname74.localdb.dao.SimpleUserDao
import com.github.enteraname74.localdb.dao.UserDao
import com.github.enteraname74.localdb.dao.UserInscriptionCodeDao
import com.github.enteraname74.localdb.model.RoomAlbum
import com.github.enteraname74.localdb.model.RoomArtist
import com.github.enteraname74.localdb.model.RoomCloudPreferences
import com.github.enteraname74.localdb.model.RoomDeviceId
import com.github.enteraname74.localdb.model.RoomFolder
import com.github.enteraname74.localdb.model.RoomMusic
import com.github.enteraname74.localdb.model.RoomMusicArtist
import com.github.enteraname74.localdb.model.RoomMusicPlaylist
import com.github.enteraname74.localdb.model.RoomPlaylist
import com.github.enteraname74.localdb.model.RoomSimpleUser
import com.github.enteraname74.localdb.model.RoomUser
import com.github.enteraname74.localdb.model.RoomUserInscriptionCode
import com.github.enteraname74.localdb.model.RoomUserStorage
import com.github.enteraname74.localdb.model.listeningstatistics.RoomListeningStatistics
import com.github.enteraname74.localdb.model.player.RoomPlayerMusic
import com.github.enteraname74.localdb.model.player.RoomPlayerMusicProgress
import com.github.enteraname74.localdb.model.player.RoomPlayerMusicUser
import com.github.enteraname74.localdb.model.player.RoomPlayerPlayedList
import com.github.enteraname74.localdb.model.player.RoomSharedPlayedListPreview
import com.github.enteraname74.localdb.model.player.RoomSharedPlayedListUser
import com.github.enteraname74.localdb.view.CurrentPlayerMusicsView
import com.github.enteraname74.localdb.view.RoomAlbumPreview
import com.github.enteraname74.localdb.view.RoomArtistPreview
import com.github.enteraname74.localdb.view.RoomMonthMusicPreview
import com.github.enteraname74.localdb.view.RoomMusicFolderPreview
import com.github.enteraname74.localdb.view.RoomPlaylistPreview

@Database(
    version = LocalDatabaseVersion.VERSION,
    entities = [
        RoomMusic::class,
        RoomAlbum::class,
        RoomArtist::class,
        RoomPlaylist::class,
        RoomMusicPlaylist::class,
        RoomMusicArtist::class,
        RoomPlayerMusic::class,
        RoomPlayerMusicProgress::class,
        RoomPlayerPlayedList::class,
        RoomFolder::class,
        RoomUser::class,
        RoomCloudPreferences::class,
        RoomDeviceId::class,
        RoomSharedPlayedListUser::class,
        RoomUserInscriptionCode::class,
        RoomSimpleUser::class,
        RoomPlayerMusicUser::class,
        RoomSharedPlayedListPreview::class,
        RoomUserStorage::class,
        RoomListeningStatistics::class,
    ],
    views = [
        CurrentPlayerMusicsView::class,
        RoomMusicFolderPreview::class,
        RoomMonthMusicPreview::class,
        RoomAlbumPreview::class,
        RoomArtistPreview::class,
        RoomPlaylistPreview::class,
    ]
)
@ConstructedBy(AppDatabaseConstructor::class)
@ColumnTypeConverters(
    InstantConverters::class,
    UserTypeConverters::class,
    UuidTypeConverters::class,
    StorageTypeConverters::class,
    DurationConverters::class,
)
@DaoReturnTypeConverters(PagingSourceDaoReturnTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val musicDao: MusicDao
    abstract val playlistDao: PlaylistDao
    abstract val albumDao: AlbumDao
    abstract val artistDao: ArtistDao
    abstract val musicPlaylistDao: MusicPlaylistDao
    abstract val musicArtistDao: MusicArtistDao
    abstract val playerMusicDao: PlayerMusicDao
    abstract val playerPlayedListDao: PlayerPlayedListDao
    abstract val folderDao: FolderDao
    abstract val coverDao: CoverDao
    abstract val playerMusicProgressDao: PlayerMusicProgressDao
    abstract val userDao: UserDao
    abstract val cloudPreferencesDao: CloudPreferencesDao
    abstract val deviceIdDao: DeviceIdDao
    abstract val sharedPlayedListUserDao: SharedPlayedListUserDao
    abstract val userInscriptionCodeDao: UserInscriptionCodeDao
    abstract val simpleUserDao: SimpleUserDao
    abstract val playerMusicUserDao: PlayerMusicUserDao
    abstract val sharedPlayedListPreviewDao: SharedPlayedListPreviewDao
    abstract val listeningStatisticsDao: ListeningStatisticsDao
}

// The Room compiler generates the `actual` implementations.
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

expect class RoomPlatformBuilder {
    fun builder(): RoomDatabase.Builder<AppDatabase>
}
