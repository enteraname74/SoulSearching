package com.github.enteraname74.soulsearching.di

import com.github.enteraname74.domain.usecase.cloud.CloudBackgroundSyncJob
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpNonAndroidManager
import com.github.enteraname74.soulsearching.settings.cloud.worker.CloudBackgroundSyncJobNonAndroidImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule: Module = module {
    includes(nonAndroidModule)

    singleOf(::CloudBackgroundSyncJobNonAndroidImpl) bind CloudBackgroundSyncJob::class
    singleOf(::FeedbackPopUpNonAndroidManager) bind FeedbackPopUpManager::class
}