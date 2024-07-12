package com.github.enteraname74.soulsearching.localdesktop.tables

import com.github.enteraname74.domain.model.PlayerMusic
import com.github.enteraname74.domain.model.PlayerWithMusicItem
import com.github.enteraname74.soulsearching.localdesktop.tables.MusicTable
import com.github.enteraname74.soulsearching.localdesktop.tables.toMusic
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow

/**
 * Table for storing PlayerMusics.
 */
internal object PlayerMusicTable: LongIdTable() {
    val playerMusicId = reference("musicId", MusicTable.id, onDelete = ReferenceOption.CASCADE)
}

internal fun ResultRow.toPlayerMusic(): PlayerMusic = PlayerMusic(
    id = this[PlayerMusicTable.id].value,
    playerMusicId = this[PlayerMusicTable.playerMusicId].value,
)

internal fun ResultRow.toPlayerWithMusicItem(): PlayerWithMusicItem = PlayerWithMusicItem(
    playerMusic = this.toPlayerMusic(),
    music = this.toMusic(),
)