package com.github.enteraname74.soulsearching.features.musicmanager.multipleartists

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.domain.usecase.album.DeleteAlbumUseCase
import com.github.enteraname74.domain.usecase.album.GetAlbumsOfArtistUseCase
import com.github.enteraname74.domain.usecase.album.GetCorrespondingAlbumUseCase
import com.github.enteraname74.domain.usecase.album.UpsertAllAlbumsUseCase
import com.github.enteraname74.domain.usecase.artist.*
import com.github.enteraname74.domain.usecase.music.UpdateMusicsAlbumUseCase
import com.github.enteraname74.domain.usecase.musicartist.UpsertAllMusicArtistsUseCase
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class RepositoryMultipleArtistManagerImpl : MultipleArtistManager(), KoinComponent {
    private val getAlbumsOfArtistUseCase: GetAlbumsOfArtistUseCase by inject()
    private val deleteAllArtistsUseCase: DeleteAllArtistsUseCase by inject()
    private val getArtistWithMusicsUseCase: GetArtistWithMusicsUseCase by inject()
    private val upsertAllMusicArtistsUseCase: UpsertAllMusicArtistsUseCase by inject()
    private val upsertAllAlbumUseCase: UpsertAllAlbumsUseCase by inject()
    private val getCorrespondingAlbumUseCase: GetCorrespondingAlbumUseCase by inject()
    private val updateMusicsAlbumUseCase: UpdateMusicsAlbumUseCase by inject()
    private val deleteAlbumUseCase: DeleteAlbumUseCase by inject()
    private val getAllArtistWithMusicsUseCase: GetAllArtistWithMusicsUseCase by inject()
    private val getAllArtistsWithNameUseCase: GetAllArtistsWithNameUseCase by inject()
    private val upsertAllArtistsUseCase: UpsertAllArtistsUseCase by inject()

    private val cachedArtists: ArrayList<Artist> = arrayListOf()
    private val cachedAlbums: ArrayList<Album> = arrayListOf()
    private val cachedMusicArtists: ArrayList<MusicArtist> = arrayListOf()

    override suspend fun getAlbumsOfMultipleArtist(artist: Artist): List<Album> =
        getAlbumsOfArtistUseCase(artistId = artist.artistId).firstOrNull() ?: emptyList()

    override suspend fun createNewArtist(artistName: String): Artist {
        val newArtist = Artist(
            artistName = artistName,
        )
        cachedArtists.add(newArtist)
        return newArtist
    }

    override suspend fun getAllArtistFromName(artistsNames: List<String>): List<Artist> =
        getAllArtistsWithNameUseCase(artistsNames)

    override suspend fun deleteArtists(
        artists: List<Artist>,
    ) {
        deleteAllArtistsUseCase(artistsIds = artists.map { it.artistId })
    }

    override suspend fun getArtistFromName(artistName: String): Artist? =
        cachedArtists.find { it.artistName == artistName }

    override suspend fun getMusicIdsOfArtist(artist: Artist): List<UUID> =
        getArtistWithMusicsUseCase(artistId = artist.artistId)
            .firstOrNull()
            ?.musics
            ?.map { it.musicId }
            ?: emptyList()

    override suspend fun getAlbumIdsOfArtist(artist: Artist): List<UUID> =
        getAlbumsOfArtistUseCase(artistId = artist.artistId)
            .firstOrNull()
            ?.map { it.albumId }
            ?: emptyList()

    override suspend fun linkSongsToArtist(musicIds: List<UUID>, artistId: UUID) {
        cachedMusicArtists.addAll(
            musicIds.map {
                MusicArtist(
                    musicId = it,
                    artistId = artistId,
                )
            }
        )
    }

    override suspend fun linkAlbumToArtist(album: Album, artist: Artist) {
        cachedAlbums.add(
            album.copy(
                artistId = artist.artistId,
            )
        )
    }

    override suspend fun getExistingAlbumOfFirstArtist(albumName: String, firstArtistName: String): Album? =
        getCorrespondingAlbumUseCase(
            albumName = albumName,
            artistName = firstArtistName,
        )

    override suspend fun moveSongsOfAlbum(fromAlbum: Album, toAlbum: Album, multipleArtistName: String) {
        updateMusicsAlbumUseCase(
            newAlbumId = toAlbum.albumId,
            legacyAlbumId = fromAlbum.albumId,
        )
        deleteAlbumUseCase.onlyAlbum(albumId = fromAlbum.albumId)
    }

    suspend fun getPotentialMultipleArtists(): List<Artist> =
        getAllArtistWithMusicsUseCase()
            .firstOrNull()
            ?.filter { it.artist.isComposedOfMultipleArtists() }
            ?.map { it.artist }
            ?: emptyList()

    override suspend fun handleMultipleArtists(artistsToDivide: List<Artist>) {
        super.handleMultipleArtists(artistsToDivide)
        upsertAllArtistsUseCase(cachedArtists)
        upsertAllMusicArtistsUseCase(cachedMusicArtists)
        upsertAllAlbumUseCase(cachedAlbums)
    }
}