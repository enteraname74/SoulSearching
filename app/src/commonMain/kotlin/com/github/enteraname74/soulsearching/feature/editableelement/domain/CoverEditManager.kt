package com.github.enteraname74.soulsearching.feature.editableelement.domain

import com.github.enteraname74.soulsearching.domain.model.Cover
import com.github.enteraname74.soulsearching.domain.model.SoulResult
import com.github.enteraname74.soulsearching.domain.usecase.cover.CommonCoverUseCase
import kotlin.uuid.Uuid

class CoverEditManager(
    private val commonCoverUseCase: CommonCoverUseCase,
) {
    suspend fun getSimpleCover(
        simpleCoverEditMode: CoverEditMode.Simple,
    ): Cover.Simple? {
        val coverId: Uuid? = simpleCoverEditMode.newCover?.let { coverData ->
            val newCoverId: Uuid = Uuid.random()
            commonCoverUseCase.upsert(
                id = newCoverId,
                data = coverData,
            )
            newCoverId
        } ?: (simpleCoverEditMode.initialCover as? Cover.CoverFile)?.fileCoverId

        return (simpleCoverEditMode.initialCover as? Cover.CoverFile)?.copy(
            fileCoverId = coverId,
        ) ?: coverId?.let { Cover.CoverFile(fileCoverId = it) } ?: simpleCoverEditMode.initialCover
    }

    private suspend fun getGridCover(
        gridCoverEditMode: CoverEditMode.Grid,
    ): Cover.Grid? {
        if (!gridCoverEditMode.isValid()) return gridCoverEditMode.initialCover

        val savedNewCovers = mutableListOf<Pair<ByteArray, Cover.Simple>>()
        val simpleCovers: List<Cover.Simple> = gridCoverEditMode.updatedCells().map { gridCell ->
            when (gridCell) {
                is GridUpdatedCells.ExistingCell -> gridCell.cover
                is GridUpdatedCells.NewCell -> {
                    savedNewCovers
                        .firstOrNull { (data) -> data.contentEquals(gridCell.byteArray) }
                        ?.second
                        ?: run {
                            val newCoverId: Uuid = Uuid.random()
                            commonCoverUseCase.upsert(
                                id = newCoverId,
                                data = gridCell.byteArray,
                            )
                            Cover.CoverFile(fileCoverId = newCoverId).also { cover ->
                                savedNewCovers.add(gridCell.byteArray to cover)
                            }
                        }
                }
                is GridUpdatedCells.Empty -> {
                    error("A grid cell cannot be empty on save")
                }
            }
        }

        return Cover.Grid(covers = simpleCovers)
    }

    suspend fun getUpdatedCover(
        coverEditMode: CoverEditMode,
    ): SoulResult<Cover?> = SoulResult.runCatching {
        when (coverEditMode.selectedType) {
            CoverEditMode.Type.Simple -> getSimpleCover(coverEditMode.simple)
            CoverEditMode.Type.Grid -> coverEditMode.grid?.let { getGridCover(coverEditMode.grid) }
        }
    }
}
