package com.github.enteraname74.soulsearching.feature.permission

expect class PermissionManager {
    fun isReadStorageGranted(): Boolean
    fun isPostNotificationGranted(): Boolean
}