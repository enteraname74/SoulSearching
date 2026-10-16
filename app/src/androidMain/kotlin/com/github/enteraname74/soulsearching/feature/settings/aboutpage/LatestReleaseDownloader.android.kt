package com.github.enteraname74.soulsearching.feature.settings.aboutpage

import android.app.Activity
import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.core.content.FileProvider
import com.github.enteraname74.soulsearching.coreui.feedbackmanager.FeedbackPopUpManager
import com.github.enteraname74.soulsearching.coreui.strings.Strings
import com.github.enteraname74.soulsearching.coreui.strings.StringsUtils
import com.github.enteraname74.soulsearching.domain.model.Release
import com.github.enteraname74.soulsearching.domain.model.ReleaseAsset
import com.github.enteraname74.soulsearching.domain.model.SoulResult
import com.github.enteraname74.soulsearching.domain.usecase.release.CommonReleaseUseCase
import java.io.File
import java.security.MessageDigest
import kotlin.coroutines.resume
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

actual class LatestReleaseDownloader(
    private val context: Context,
    private val commonReleaseUseCase: CommonReleaseUseCase,
    private val feedbackPopUpManager: FeedbackPopUpManager,
) {
    private val strings: Strings
        get() = StringsUtils.getStrings(context)

    private val operationMutex = Mutex()
    private var activeDownloadId: Long? = null

    actual suspend fun downloadLatestRelease(release: Release): SoulResult<Unit> {
        val asset = release.apkAsset
            ?.takeIf(::hasSafeApkName)
            ?: return SoulResult.Error(strings.releaseDownloadUnavailable)

        if (getInstalledProductionPackage() == null) {
            return SoulResult.Error(strings.releaseProductionAppMissing)
        }

        if (!canRequestPackageInstalls() && !awaitInstallPermission()) {
            return SoulResult.Error(strings.releaseInstallPermissionDenied)
        }

        return operationMutex.withLock {
            if (activeDownloadId != null) {
                return@withLock SoulResult.Error(strings.releaseDownloadInProgress)
            }

            val destination = getDestinationFile(asset)
                ?: return@withLock SoulResult.Error(strings.releaseDownloadFailed)

            if (destination.exists()) {
                val validationError = withContext(Dispatchers.IO) {
                    validateApk(destination, asset)
                }
                if (validationError == null) {
                    return@withLock launchInstaller(destination)
                }

                destination.delete()
                return@withLock SoulResult.Error(validationError)
            }

            cleanupOtherApks(destination)
            enqueueDownload(asset, destination)
        }
    }

    internal suspend fun handleDownloadCompleted(downloadId: Long) {
        val asset = commonReleaseUseCase.getLatest().firstOrNull()?.apkAsset ?: return
        val download = getDownload(downloadId) ?: return

        if (download.sourceUrl != asset.downloadUrl) return

        if (activeDownloadId == downloadId) {
            activeDownloadId = null
        }

        if (download.status != DownloadManager.STATUS_SUCCESSFUL) {
            feedbackPopUpManager.showFeedback(strings.releaseDownloadFailed)
            return
        }

        val expectedFile = getDestinationFile(asset) ?: run {
            feedbackPopUpManager.showFeedback(strings.releaseDownloadFailed)
            return
        }
        val downloadedFile = download.localUri
            ?.takeIf { it.scheme == "file" }
            ?.path
            ?.let(::File)

        if (
            downloadedFile == null ||
            downloadedFile.canonicalFile != expectedFile.canonicalFile ||
            !expectedFile.exists()
        ) {
            feedbackPopUpManager.showFeedback(strings.releaseDownloadFailed)
            return
        }

        val validationError = withContext(Dispatchers.IO) {
            validateApk(expectedFile, asset)
        }
        if (validationError != null) {
            expectedFile.delete()
            feedbackPopUpManager.showFeedback(validationError)
            return
        }

        launchInstaller(expectedFile).let { result ->
            (result as? SoulResult.Error)?.let {
                feedbackPopUpManager.showFeedback(
                    it.error ?: strings.releaseInstallerUnavailable
                )
            }
        }
    }

    private fun enqueueDownload(asset: ReleaseAsset, destination: File): SoulResult<Unit> =
        try {
            destination.parentFile?.mkdirs()
            val request = DownloadManager.Request(Uri.parse(asset.downloadUrl))
                .setTitle(asset.name)
                .setMimeType(APK_MIME_TYPE)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalFilesDir(
                    context,
                    Environment.DIRECTORY_DOWNLOADS,
                    "$UPDATE_DIRECTORY_NAME/${asset.name}",
                )

            activeDownloadId = downloadManager.enqueue(request)
            SoulResult.ofSuccess()
        } catch (_: Exception) {
            SoulResult.Error(strings.releaseDownloadFailed)
        }

    private fun getDownload(downloadId: Long): CompletedDownload? = runCatching {
        downloadManager.query(
            DownloadManager.Query().setFilterById(downloadId)
        )?.use { cursor ->
            if (!cursor.moveToFirst()) return@use null

            CompletedDownload(
                status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)),
                sourceUrl = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_URI)),
                localUri = cursor
                    .getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI))
                    ?.let(Uri::parse),
            )
        }
    }.getOrNull()

    private suspend fun awaitInstallPermission(): Boolean = suspendCancellableCoroutine { continuation ->
        val application = context.applicationContext as Application
        var settingsOpened = false

        val callbacks = object : Application.ActivityLifecycleCallbacks {
            override fun onActivityResumed(activity: Activity) {
                if (!settingsOpened || !continuation.isActive) return
                application.unregisterActivityLifecycleCallbacks(this)
                continuation.resume(canRequestPackageInstalls())
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
            override fun onActivityStarted(activity: Activity) = Unit
            override fun onActivityPaused(activity: Activity) = Unit
            override fun onActivityStopped(activity: Activity) = Unit
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
            override fun onActivityDestroyed(activity: Activity) = Unit
        }

        application.registerActivityLifecycleCallbacks(callbacks)
        continuation.invokeOnCancellation {
            application.unregisterActivityLifecycleCallbacks(callbacks)
        }

        val settingsIntent = Intent(
            Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
            Uri.parse("package:${context.packageName}"),
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            settingsOpened = true
            context.startActivity(settingsIntent)
        } catch (_: Exception) {
            application.unregisterActivityLifecycleCallbacks(callbacks)
            if (continuation.isActive) continuation.resume(false)
        }
    }

    private fun canRequestPackageInstalls(): Boolean =
        context.packageManager.canRequestPackageInstalls()

    private fun validateApk(file: File, asset: ReleaseAsset): String? {
        if (!file.isFile || file.length() != asset.size) {
            return strings.releaseDigestMismatch
        }

        asset.sha256?.let { expectedDigest ->
            if (file.sha256() != expectedDigest.lowercase()) {
                return strings.releaseDigestMismatch
            }
        }

        val archivePackage = getArchivePackage(file)
            ?: return strings.releaseInvalidApk
        if (archivePackage.packageName != PRODUCTION_APPLICATION_ID) {
            return strings.releaseWrongPackage
        }

        val installedPackage = getInstalledProductionPackage()
            ?: return strings.releaseProductionAppMissing
        if (archivePackage.versionCodeLong() <= installedPackage.versionCodeLong()) {
            return strings.releaseNotNewer
        }

        val archiveSigners = archivePackage.signerDigests()
        val installedSigners = installedPackage.signerDigests()
        if (
            archiveSigners.isEmpty() ||
            installedSigners.isEmpty() ||
            archiveSigners.intersect(installedSigners).isEmpty()
        ) {
            return strings.releaseSignatureMismatch
        }

        return null
    }

    @Suppress("DEPRECATION")
    private fun getArchivePackage(file: File): PackageInfo? =
        context.packageManager.getPackageArchiveInfo(file.absolutePath, packageInfoFlags)

    @Suppress("DEPRECATION")
    private fun getInstalledProductionPackage(): PackageInfo? = try {
        context.packageManager.getPackageInfo(PRODUCTION_APPLICATION_ID, packageInfoFlags)
    } catch (_: PackageManager.NameNotFoundException) {
        null
    }

    private fun launchInstaller(file: File): SoulResult<Unit> = try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.updates",
            file,
        )
        context.startActivity(
            Intent(Intent.ACTION_INSTALL_PACKAGE)
                .setData(uri)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        SoulResult.ofSuccess()
    } catch (_: Exception) {
        SoulResult.Error(strings.releaseInstallerUnavailable)
    }

    private fun getDestinationFile(asset: ReleaseAsset): File? =
        context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            ?.resolve(UPDATE_DIRECTORY_NAME)
            ?.resolve(asset.name)

    private fun cleanupOtherApks(destination: File) {
        destination.parentFile
            ?.listFiles { file -> file.isFile && file.extension.equals("apk", ignoreCase = true) }
            ?.filterNot { it == destination }
            ?.forEach(File::delete)
    }

    private fun hasSafeApkName(asset: ReleaseAsset): Boolean =
        File(asset.name).name == asset.name && asset.name.endsWith(".apk", ignoreCase = true)

    private val downloadManager: DownloadManager
        get() = context.getSystemService(DownloadManager::class.java)

    @Suppress("DEPRECATION")
    private val packageInfoFlags: Int
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            PackageManager.GET_SIGNING_CERTIFICATES
        } else {
            PackageManager.GET_SIGNATURES
        }

    @Suppress("DEPRECATION")
    private fun PackageInfo.versionCodeLong(): Long =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) longVersionCode else versionCode.toLong()

    @Suppress("DEPRECATION")
    private fun PackageInfo.signerDigests(): Set<String> {
        val certificates = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            signingInfo?.let { info ->
                if (info.hasMultipleSigners()) {
                    info.apkContentsSigners
                } else {
                    info.signingCertificateHistory
                }
            }
        } else {
            signatures
        }.orEmpty()

        return certificates.mapTo(mutableSetOf()) { signature ->
            MessageDigest.getInstance(SHA256).digest(signature.toByteArray()).toHexString()
        }
    }

    private fun File.sha256(): String {
        val digest = MessageDigest.getInstance(SHA256)
        inputStream().buffered().use { input ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            while (true) {
                val read = input.read(buffer)
                if (read < 0) break
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().toHexString()
    }

    private fun ByteArray.toHexString(): String = joinToString(separator = "") { byte ->
        (byte.toInt() and 0xff).toString(16).padStart(2, '0')
    }

    private data class CompletedDownload(
        val status: Int,
        val sourceUrl: String,
        val localUri: Uri?,
    )

    private companion object {
        const val PRODUCTION_APPLICATION_ID = "com.github.enteraname74.soulsearching"
        const val UPDATE_DIRECTORY_NAME = "updates"
        const val APK_MIME_TYPE = "application/vnd.android.package-archive"
        const val SHA256 = "SHA-256"
    }
}
