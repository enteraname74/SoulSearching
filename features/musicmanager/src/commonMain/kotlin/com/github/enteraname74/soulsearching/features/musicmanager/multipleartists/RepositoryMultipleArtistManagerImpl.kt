package com.github.enteraname74.soulsearching.features.musicmanager.multipleartists

import com.github.enteraname74.domain.model.Album
import com.github.enteraname74.domain.model.AlbumArtist
import com.github.enteraname74.domain.model.Artist
import com.github.enteraname74.domain.model.MusicArtist
import com.github.enteraname74.domain.usecase.album.DeleteAlbumUseCase
import com.github.enteraname74.domain.usecase.album.GetAlbumsOfArtistUseCase
import com.github.enteraname74.domain.usecase.album.GetCorrespondingAlbumUseCase
import com.github.enteraname74.domain.usecase.albumartist.UpsertAlbumArtistUseCase
import com.github.enteraname74.domain.usecase.artist.*
import com.github.enteraname74.domain.usecase.musicalbum.UpdateMusicsAlbumUseCase
import com.github.enteraname74.domain.usecase.musicartist.UpsertMusicIntoArtistUseCase
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class RepositoryMultipleArtistManagerImpl : MultipleArtistManager(), KoinComponent {
    private val getAlbumsOfArtistUseCase: GetAlbumsOfArtistUseCase by inject()
    private val getArtistFromNameUseCase: GetArtistFromNameUseCase by inject()
    private val upsertArtistUseCase: UpsertArtistUseCase by inject()
    private val deleteArtistUseCase: DeleteArtistUseCase by inject()
    private val getArtistWithMusicsUseCase: GetArtistWithMusicsUseCase by inject()
    private val upsertMusicIntoArtistUseCase: UpsertMusicIntoArtistUseCase by inject()
    private val upsertAlbumArtistUseCase: UpsertAlbumArtistUseCase by inject()
    private val getCorrespondingAlbumUseCase: GetCorrespondingAlbumUseCase by inject()
    private val updateMusicsAlbumUseCase: UpdateMusicsAlbumUseCase by inject()
    private val deleteAlbumUseCase: DeleteAlbumUseCase by inject()
    private val getAllArtistWithMusicsUseCase: GetAllArtistWithMusicsUseCase by inject()

    override suspend fun getAlbumsOfMultipleArtist(artist: Artist): List<Album> =
        getAlbumsOfArtistUseCase(artistId = artist.artistId).firstOrNull() ?: emptyList()

    override suspend fun getArtistFromName(artistName: String): Artist? =
        getArtistFromNameUseCase(artistName = artistName)

    override suspend fun createNewArtist(artistName: String): Artist {
        val newArtist = Artist(
            artistName = artistName,
        )
        upsertArtistUseCase(artist = newArtist)

        return getArtistFromNameUseCase(artistName = artistName)!!
    }

    override suspend fun deleteArtist(
        artist: Artist,
        musicIdsOfInitialArtist: List<UUID>,
        albumIdsOfInitialArtist: List<UUID>
    ) {
        deleteArtistUseCase(artistId = artist.artistId)
    }

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

    override suspend fun linkMusicToArtist(musicId: UUID, artistId: UUID) {
        upsertMusicIntoArtistUseCase(
            musicArtist = MusicArtist(
                musicId = musicId,
                artistId = artistId,
            )
        )
    }

    override suspend fun linkAlbumToArtist(albumId: UUID, artistId: UUID) {
        upsertAlbumArtistUseCase(
            albumArtist = AlbumArtist(
                albumId = albumId,
                artistId = artistId,
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
}