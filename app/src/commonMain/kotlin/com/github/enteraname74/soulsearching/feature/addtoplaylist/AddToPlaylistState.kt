package com.github.enteraname74.soulsearching.feature.addtoplaylist

import androidx.paging.PagingData
import com.github.enteraname74.soulsearching.domain.model.Music
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

data class AddToPlaylistState(
    val musics: Flow<PagingData<Music>>,
    val selectedMusicIds: Set<Uuid>,
    val onSearch: (String) -> Unit,
    val onToggleSelection: (Uuid) -> Unit,
    val onSave: () -> Unit,
    val navigateBack: () -> Unit,
)
