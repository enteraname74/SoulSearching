package com.github.enteraname74.domain.datasource

import com.github.enteraname74.domain.model.Music
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Data source of a Music.
 */
interface MusicDataSource {
    /**
     * Inserts or updates a Music.
     */
    suspend fun insertMusic(music: Music)

    /**
     * Deletes a Music.
     */
    suspend fun deleteMusic(music: Music)

    /**
     * Remove a Music from an Album.
     */
    suspend fun deleteMusicFromAlbum(album: String, artist: String)

    /**
     * Tries to retrieve a Music from its path.
     */
    suspend fun getMusicFromPath(musicPath: String): Music?

    /**
     * Retrieve a music from its id.
     */
    suspend fun getMusicFromId(musicId: UUID): Music

    /**
     * Tries to retrieve a music from the favorite playlist.
     */
    suspend fun getMusicFromFavoritePlaylist(musicId: UUID): Music?

    /**
     * Retrieves all musics path.
     */
    suspend fun getAllMusicsPaths(): List<String>

    /**
     * Retrieves a flow of all Music, sorted by name asc.
     */
    fun getAllMusicsSortByNameAscAsFlow(): Flow<List<Music>>

    /**
     * Retrieves a flow of all Music, sorted by name desc.
     */
    fun getAllMusicsSortByNameDescAsFlow(): Flow<List<Music>>

    /**
     * Retrieves a flow of all Music, sorted by added date asc.
     */
    fun getAllMusicsSortByAddedDateAscAsFlow(): Flow<List<Music>>

    /**
     * Retrieves a flow of all Music, sorted by added date desc.
     */
    fun getAllMusicsSortByAddedDateDescAsFlow(): Flow<List<Music>>

    /**
     * Retrieves a flow of all Music, sorted by nb played asc.
     */
    fun getAllMusicsSortByNbPlayedAscAsFlow(): Flow<List<Music>>

    /**
     * Retrieves a flow of all Music, sorted by nb played desc.
     */
    fun getAllMusicsSortByNbPlayedDescAsFlow(): Flow<List<Music>>

    /**
     * Retrieves a flow of all Music in the quick access.
     */
    fun getAllMusicsFromQuickAccessAsFlow(): Flow<List<Music>>

    /**
     * Retrieves all musics of an Album.
     */
    suspend fun getAllMusicFromAlbum(albumId: UUID): List<Music>

    /**
     * Retrieves the number of musics sharing the same cover.
     */
    suspend fun getNumberOfMusicsWithCoverId(coverId: UUID): Int

    /**
     * Updates the status of quick access of a Music.
     */
    suspend fun updateQuickAccessState(newQuickAccessState: Boolean, musicId: UUID)

    /**
     * Retrieves the number of time a Music has been played.
     */
    suspend fun getNbPlayedOfMusic(musicId: UUID): Int

    /**
     * Update the total of played time of a Music.
     */
    suspend fun updateNbPlayed(newNbPlayed: Int, musicId: UUID)

    /**
     * Hide or show musics in a given folder.
     */
    suspend fun updateMusicsHiddenState(folderName: String, newIsHidden: Boolean)

    /**
     * Retrieves all musics from a Folder.
     */
    suspend fun getMusicsFromFolder(folderName: String): List<Music>
}