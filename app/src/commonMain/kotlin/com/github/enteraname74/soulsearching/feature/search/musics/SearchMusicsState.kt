package com.github.enteraname74.soulsearching.feature.search.musics

import androidx.compose.ui.focus.FocusRequester
import com.github.enteraname74.soulsearching.domain.model.Music
import kotlin.uuid.Uuid

data class SearchMusicsState(
    val search: String,
    val musics: List<Music>,
    val onSearch: (String) -> Unit,
    val focusRequester: FocusRequester,
    val navigateBack: () -> Unit,
    val onMusicClick: (Music) -> Unit,
    val showMusicBottomSheet: (Uuid) -> Unit,
)
