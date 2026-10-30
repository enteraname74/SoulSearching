package com.github.enteraname74.soulsearching.feature.search.all

import androidx.compose.ui.focus.FocusRequester
import com.github.enteraname74.soulsearching.domain.model.AlbumPreview
import com.github.enteraname74.soulsearching.domain.model.ArtistPreview
import com.github.enteraname74.soulsearching.domain.model.CollectionPreview
import com.github.enteraname74.soulsearching.domain.model.Music
import com.github.enteraname74.soulsearching.domain.model.PlaylistPreview
import kotlin.uuid.Uuid

data class SearchAllState(
    val search: String,
    val musics: List<Music>,
    val albums: List<AlbumPreview>,
    val selectedSearchableElements: Set<SearchableElement>,
    val playlists: List<PlaylistPreview>,
    val artists: List<ArtistPreview>,
    val collections: List<CollectionPreview>,
    val focusRequester: FocusRequester,
    val navigateBack: () -> Unit,
    val onSearch: (String) -> Unit,
    val showMusicBottomSheet: (Uuid) -> Unit,
    val showAlbumBottomSheet: (Uuid) -> Unit,
    val showPlaylistBottomSheet: (Uuid) -> Unit,
    val showArtistBottomSheet: (Uuid) -> Unit,
    val showCollectionBottomSheet: (Uuid) -> Unit,
    val onMusicClick: (Music) -> Unit,
    val navigateToPlaylist: (Uuid) -> Unit,
    val navigateToCollection: (Uuid) -> Unit,
    val navigateToArtist: (Uuid) -> Unit,
    val navigateToAlbum: (Uuid) -> Unit,
    val onSearchInAllElements: () -> Unit,
    val onToggleSearchableElement: (SearchableElement) -> Unit,
) {
    enum class SearchableElement {
        Collections,
        Playlists,
        Artists,
        Albums,
        Musics,
    }
}
