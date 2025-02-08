package com.github.enteraname74.domain.usecase.music

import com.github.enteraname74.domain.repository.MusicRepository

class UploadAllMusicToCloudUseCase(
    private val musicRepository: MusicRepository,
) {
    suspend operator fun invoke(
        onProgressChanged: suspend (Float) -> Unit = {},
    ) {
        musicRepository.uploadAllMusicToCloud(
            onProgressChanged = onProgressChanged,
        )
    }
}