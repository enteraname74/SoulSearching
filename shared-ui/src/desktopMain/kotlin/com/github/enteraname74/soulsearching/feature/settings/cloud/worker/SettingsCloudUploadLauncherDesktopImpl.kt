package com.github.enteraname74.soulsearching.feature.settings.cloud.worker

import com.github.enteraname74.domain.usecase.music.UploadAllMusicToCloudUseCase
import com.github.enteraname74.soulsearching.feature.settings.cloud.worker.SettingsCloudUploadLauncher

class SettingsCloudUploadLauncherDesktopImpl(
    private val uploadAllMusicToCloudUseCase: UploadAllMusicToCloudUseCase,
): SettingsCloudUploadLauncher {
    override suspend fun launchWorker() {
        uploadAllMusicToCloudUseCase()
    }
}