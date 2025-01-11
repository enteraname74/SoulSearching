package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.repository.MusicRepository
import com.github.enteraname74.domain.util.FlowResult
import kotlinx.coroutines.flow.Flow

class GetCloudUploadMusicUseCase(
    private val musicRepository: MusicRepository,
) {
    operator fun invoke(): Flow<FlowResult<Unit>> =
        musicRepository.uploadFlow
}