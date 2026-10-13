package com.github.enteraname74.soulsearching.feature.editableelement.domain

import com.github.enteraname74.soulsearching.domain.model.Cover

data class CoverEditMode(
    val simple: Simple,
    val grid: Grid?,
    val selectedType: Type,
) {
    val selectionAllowed: Boolean = grid != null

    fun isValid(): Boolean =
        when (selectedType) {
            // No validity check on simple mode
            Type.Simple -> true
            // Grid must be filled
            Type.Grid -> grid?.isValid() == true
        }

    data class Simple(
        val initialCover: Cover.Simple?,
        val newCover: ByteArray?,
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Simple

            if (initialCover != other.initialCover) return false
            if (!newCover.contentEquals(other.newCover)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = initialCover?.hashCode() ?: 0
            result = 31 * result + (newCover?.contentHashCode() ?: 0)
            return result
        }
    }

    data class Grid(
        val initialCover: Cover.Grid?,
        val newCover: GridContent,
    ) {

        /**
         * Grid is the combination of the initial and new cover doesn't leave any empty space.
         */
        fun isValid(): Boolean =
            updatedCells().none { it is GridUpdatedCells.Empty }

        fun updatedCells(): List<GridUpdatedCells> = buildList {
            val newList = newCover.asList()
            for (index in 0 until Cover.Grid.GRID_SIZE) {
                val initial = initialCover?.list?.getOrNull(index)?.takeIf { !it.isEmpty() }
                val new = newList.getOrNull(index)

                when {
                    // A new cell has the precedence on a legacy one.
                    new != null -> add(GridUpdatedCells.NewCell(new))
                    initial != null -> add(GridUpdatedCells.ExistingCell(initial))
                    else -> add(GridUpdatedCells.Empty)
                }
            }
        }

        data class GridContent(
            val topStart: ByteArray?,
            val topEnd: ByteArray?,
            val bottomStart: ByteArray?,
            val bottomEnd: ByteArray?,
        ) {
            constructor(list: List<ByteArray?>) : this(
                topStart = list.getOrNull(0),
                topEnd = list.getOrNull(1),
                bottomStart = list.getOrNull(2),
                bottomEnd = list.getOrNull(3),
            )

            fun asList(): List<ByteArray?> =
                listOf(
                    topStart,
                    topEnd,
                    bottomStart,
                    bottomEnd,
                )

            fun setAt(pos: Int, data: ByteArray): GridContent {
                val mutableList = asList().toMutableList()
                mutableList.set(pos, data)
                return GridContent(mutableList)
            }

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other == null || this::class != other::class) return false

                other as GridContent

                if (!topStart.contentEquals(other.topStart)) return false
                if (!topEnd.contentEquals(other.topEnd)) return false
                if (!bottomStart.contentEquals(other.bottomStart)) return false
                if (!bottomEnd.contentEquals(other.bottomEnd)) return false

                return true
            }

            override fun hashCode(): Int {
                var result = topStart?.contentHashCode() ?: 0
                result = 31 * result + (topEnd?.contentHashCode() ?: 0)
                result = 31 * result + (bottomStart?.contentHashCode() ?: 0)
                result = 31 * result + (bottomEnd?.contentHashCode() ?: 0)
                return result
            }
        }
    }

    enum class Type {
        Simple,
        Grid;

        companion object {
            fun fromCover(cover: Cover?): Type =
                when (cover) {
                    null, is Cover.Simple -> Simple
                    is Cover.Grid -> Grid
                }
        }
    }
}

sealed interface GridUpdatedCells {
    data class ExistingCell(val cover: Cover) : GridUpdatedCells
    data class NewCell(val byteArray: ByteArray) : GridUpdatedCells {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as NewCell

            if (!byteArray.contentEquals(other.byteArray)) return false

            return true
        }

        override fun hashCode(): Int {
            return byteArray.contentHashCode()
        }
    }

    data object Empty : GridUpdatedCells
}
