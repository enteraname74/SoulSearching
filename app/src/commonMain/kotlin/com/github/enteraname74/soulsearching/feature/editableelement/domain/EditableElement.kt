package com.github.enteraname74.soulsearching.feature.editableelement.domain

import com.github.enteraname74.domain.model.Cover

data class EditableElement(
    val initialCover: Cover?,
    val newCover: ByteArray?,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EditableElement

        if (initialCover != other.initialCover) return false
        if (newCover != null) {
            if (other.newCover == null) return false
            if (!newCover.contentEquals(other.newCover)) return false
        } else if (other.newCover != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = initialCover?.hashCode() ?: 0
        result = 31 * result + (newCover?.contentHashCode() ?: 0)
        return result
    }
}
