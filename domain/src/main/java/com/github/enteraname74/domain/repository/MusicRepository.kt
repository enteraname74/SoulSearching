package com.github.enteraname74.domain.repository

import com.github.enteraname74.domain.datasource.MusicDataSource
import com.github.enteraname74.domain.model.Music
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

/**
 * Repository of a Music.
 */
class MusicRepository @Inject constructor(
    private val musicDataSource: MusicDataSource
) {
    /**
     * Inserts or updates a Music.
     */
    suspend fun insertMusic(music: Music) = musicDataSource.insertMusic(
        music = music
    )

    /**
     * Deletes a Music.
     */
    suspend fun deleteMusic(music: Music) = musicDataSource.deleteMusic(
        music = music
    )

    /**
     * Remove a Music from an Album.
     */
    suspend fun deleteMusicFromAlbum(album: String, artist: String) =
        musicDataSource.deleteMusicFromAlbum(
            album = album,
            artist = artist
        )

    /**
     * Tries to retrieve a Music from its path.
     */
    suspend fun getMusicFromPath(musicPath: String): Music? = musicDataSource.getMusicFromPath(
        musicPath = musicPath
    )

    /**
     * Retrieve a music from its id.
     */
    suspend fun getMusicFromId(musicId: UUID): Music = musicDataSource.getMusicFromId(
        musicId = musicId
    )

    /**
     * Tries to retrieve a music from the favorite playlist.
     */
    suspend fun getMusicFromFavoritePlaylist(musicId: UUID): Music? =
        musicDataSource.getMusicFromFavoritePlaylist(
            musicId = musicId
        )

    /**
     * Retrieves all musics path.
     */
    suspend fun getAllMusicsPaths(): List<String> = musicDataSource.getAllMusicsPaths()

    /**
     * Retrieves a flow of all Music, sorted by name asc.
     */
    fun getAllMusicsSortByNameAscAsFlow(): Flow<List<Music>> =
        musicDataSource.getAllMusicsSortByNameAscAsFlow()

    /**
     * Retrieves a flow of all Music, sorted by name desc.
     */
    fun getAllMusicsSortByNameDescAsFlow(): Flow<List<Music>> =
        musicDataSource.getAllMusicsSortByNameDescAsFlow()

    /**
     * Retrieves a flow of all Music, sorted by added date asc.
     */
    fun getAllMusicsSortByAddedDateAscAsFlow(): Flow<List<Music>> =
        musicDataSource.getAllMusicsSortByAddedDateAscAsFlow()

    /**
     * Retrieves a flow of all Music, sorted by added date desc.
     */
    fun getAllMusicsSortByAddedDateDescAsFlow(): Flow<List<Music>> =
        musicDataSource.getAllMusicsSortByAddedDateDescAsFlow()

    /**
     * Retrieves a flow of all Music, sorted by nb played asc.
     */
    fun getAllMusicsSortByNbPlayedAscAsFlow(): Flow<List<Music>> =
        musicDataSource.getAllMusicsSortByNbPlayedAscAsFlow()

    /**
     * Retrieves a flow of all Music, sorted by nb played desc.
     */
    fun getAllMusicsSortByNbPlayedDescAsFlow(): Flow<List<Music>> =
        musicDataSource.getAllMusicsSortByNbPlayedDescAsFlow()

    /**
     * Retrieves a flow of all Music in the quick access.
     */
    fun getAllMusicsFromQuickAccessAsFlow(): Flow<List<Music>> =
        musicDataSource.getAllMusicsFromQuickAccessAsFlow()

    /**
     * Retrieves all musics of an Album.
     */
    suspend fun getAllMusicFromAlbum(albumId: UUID): List<Music> =
        musicDataSource.getAllMusicFromAlbum(
            albumId = albumId
        )

    /**
     * Retrieves the number of musics sharing the same cover.
     */
    suspend fun getNumberOfMusicsWithCoverId(coverId: UUID): Int =
        musicDataSource.getNumberOfMusicsWithCoverId(
            coverId = coverId
        )

    /**
     * Updates the status of quick access of a Music.
     */
    suspend fun updateQuickAccessState(newQuickAccessState: Boolean, musicId: UUID) =
        musicDataSource.updateQuickAccessState(
            newQuickAccessState = newQuickAccessState,
            musicId = musicId
        )

    /**
     * Retrieves the number of time a Music has been played.
     */
    suspend fun getNbPlayedOfMusic(musicId: UUID): Int = musicDataSource.getNbPlayedOfMusic(
        musicId = musicId
    )

    /**
     * Update the total of played time of a Music.
     */
    suspend fun updateNbPlayed(newNbPlayed: Int, musicId: UUID) = musicDataSource.updateNbPlayed(
        newNbPlayed = newNbPlayed,
        musicId = musicId
    )

    /**
     * Hide or show musics in a given folder.
     */
    suspend fun updateMusicsHiddenState(folderName: String, newIsHidden: Boolean) =
        musicDataSource.updateMusicsHiddenState(
            folderName = folderName,
            newIsHidden = newIsHidden
        )

    /**
     * Retrieves all musics from a Folder.
     */
    suspend fun getMusicsFromFolder(folderName: String): List<Music> =
        musicDataSource.getMusicsFromFolder(
            folderName = folderName
        )
}