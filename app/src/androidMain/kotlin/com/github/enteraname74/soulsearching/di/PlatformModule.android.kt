package com.github.enteraname74.soulsearching.di

import android.content.Context
import com.github.enteraname74.domain.model.settings.SoulSearchingSettings
import com.github.enteraname74.domain.model.settings.SoulSearchingSettingsKeys
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpAndroidManager
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.domain.model.settings.SoulSearchingSettingsImpl
import com.github.enteraname74.domain.usecase.cloud.CloudBackgroundSyncJob
import com.github.enteraname74.soulsearching.feature.settings.cloud.worker.CloudBackgroundSyncJobAndroidImpl
import com.github.enteraname74.soulsearching.feature.settings.cloud.worker.CloudSyncWorker
import com.github.enteraname74.soulsearching.util.FileOperation
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule = module {
    singleOf(::FeedbackPopUpAndroidManager) bind FeedbackPopUpManager::class
    single<SoulSearchingSettings> {
        SoulSearchingSettingsImpl(
            settings = SharedPreferencesSettings(
                delegate = androidApplication().getSharedPreferences(
                    SoulSearchingSettingsKeys.SHARED_PREF_KEY,
                    Context.MODE_PRIVATE
                )
            )
        )
    }
    factory {
        FileOperation(androidApplication())
    }
    workerOf(::CloudSyncWorker)
    factoryOf(::CloudBackgroundSyncJobAndroidImpl) bind CloudBackgroundSyncJob::class
}