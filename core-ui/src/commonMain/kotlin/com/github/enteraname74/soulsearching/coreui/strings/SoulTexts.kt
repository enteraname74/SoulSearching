package com.github.enteraname74.soulsearching.coreui.strings

import com.github.enteraname74.soulsearching.coreui.core_ui.generated.resources.*
import com.github.enteraname74.soulsearching.coreui.theme.color.ColorPaletteSeed
import com.github.enteraname74.soulsearching.domain.model.player.SharedPlayedListPreview
import com.github.enteraname74.soulsearching.domain.model.statistics.Period
import com.github.enteraname74.soulsearching.domain.model.user.UserType
import com.github.enteraname74.soulsearching.domain.usecase.music.SyncDataWithCloudUseCase
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.StringResource
import kotlin.time.Duration

/**
 * Texts that depend on a value (counts, enums, durations...) and cannot be expressed as a single resource.
 */
object SoulTexts {
    fun musics(total: Int): SoulTextSpec =
        counted(total, CoreRes.string.musics_count_zero, CoreRes.plurals.musics_count)

    fun deletedMusicsAutomatically(total: Int): SoulTextSpec =
        counted(total, CoreRes.string.deleted_musics_automatically_zero, CoreRes.plurals.deleted_musics_automatically)

    fun plays(total: Int): SoulTextSpec =
        counted(total, CoreRes.string.plays_zero, CoreRes.plurals.plays)

    fun selectedElements(total: Int): SoulTextSpec =
        counted(total, CoreRes.string.selected_elements_zero, CoreRes.plurals.selected_elements)

    fun selectedMusics(total: Int): SoulTextSpec =
        counted(total, CoreRes.string.selected_musics_zero, CoreRes.plurals.selected_musics)

    fun sharedListPreviewUsers(preview: SharedPlayedListPreview): SoulTextSpec =
        counted(
            preview.totalUsers,
            CoreRes.string.shared_list_preview_users_zero,
            CoreRes.plurals.shared_list_preview_users,
        )

    fun sharedListPreviewConnectedUsers(preview: SharedPlayedListPreview): SoulTextSpec =
        counted(
            preview.connectedUsers,
            CoreRes.string.shared_list_preview_connected_users_zero,
            CoreRes.plurals.shared_list_preview_connected_users,
        )

    fun fetchingMusicsFrom(path: String): SoulTextSpec =
        CoreRes.string.fetching_musics_from.toTextSpec(path)

    fun newReleaseAvailableText(releaseName: String): SoulTextSpec =
        CoreRes.string.new_release_available_text.toTextSpec(releaseName)

    fun artistCoverMethodExampleTitle(artist: String): SoulTextSpec =
        CoreRes.string.artist_cover_method_example_title.toTextSpec(artist)

    fun activateRemoteLyricsFetchText(): SoulTextSpec =
        CoreRes.string.activate_remote_lyrics_fetch_text.toTextSpec(CoreRes.string.lyrics_provider_name.toTextSpec())

    fun activateRemoteLyricsFetchHint(): SoulTextSpec =
        CoreRes.string.activate_remote_lyrics_fetch_hint.toTextSpec(CoreRes.string.lyrics_provider_name.toTextSpec())

    fun userType(type: UserType): SoulTextSpec =
        when (type) {
            UserType.User -> CoreRes.string.user_type_user
            UserType.Admin -> CoreRes.string.user_type_admin
            UserType.Unknown -> CoreRes.string.user_type_unknown
        }.toTextSpec()

    fun colorPaletteSeed(seed: ColorPaletteSeed): SoulTextSpec =
        when (seed) {
            ColorPaletteSeed.DarkVibrant -> CoreRes.string.color_palette_seed_dark_vibrant
            ColorPaletteSeed.DarkMuted -> CoreRes.string.color_palette_seed_dark_muted
            ColorPaletteSeed.LightMuted -> CoreRes.string.color_palette_seed_light_muted
            ColorPaletteSeed.LightVibrant -> CoreRes.string.color_palette_seed_light_vibrant
            ColorPaletteSeed.Dominant -> CoreRes.string.color_palette_seed_dominant
            ColorPaletteSeed.Muted -> CoreRes.string.color_palette_seed_muted
            ColorPaletteSeed.Vibrant -> CoreRes.string.color_palette_seed_vibrant
        }.toTextSpec()

    fun cloudSyncNotificationTitle(state: SyncDataWithCloudUseCase.State): SoulTextSpec =
        when (state) {
            SyncDataWithCloudUseCase.State.SyncingSettings -> CoreRes.string.cloud_sync_notification_title_syncing_settings
            is SyncDataWithCloudUseCase.State.Failure -> CoreRes.string.cloud_sync_notification_title_failure
            SyncDataWithCloudUseCase.State.Idle -> CoreRes.string.cloud_sync_notification_title_idle
            SyncDataWithCloudUseCase.State.NoMusicsToSend -> CoreRes.string.cloud_sync_notification_title_no_musics_to_send
            SyncDataWithCloudUseCase.State.CheckingMusicsToSend -> CoreRes.string.cloud_sync_notification_title_checking_musics_to_send
            SyncDataWithCloudUseCase.State.Cleaning -> CoreRes.string.cloud_sync_notification_title_cleaning
            SyncDataWithCloudUseCase.State.ClearingRemoteMusicIds -> CoreRes.string.cloud_sync_notification_title_clearing_remote_music_ids
            SyncDataWithCloudUseCase.State.FetchingFromRemote -> CoreRes.string.cloud_sync_notification_title_fetching_from_remote
            is SyncDataWithCloudUseCase.State.SavingRemote -> CoreRes.string.cloud_sync_notification_title_saving_remote
            is SyncDataWithCloudUseCase.State.UpdateMusics -> CoreRes.string.cloud_sync_notification_title_update_musics
            is SyncDataWithCloudUseCase.State.UploadMusics -> CoreRes.string.cloud_sync_notification_title_upload_musics
            SyncDataWithCloudUseCase.State.FetchingRemotePlaylists -> CoreRes.string.cloud_sync_notification_title_fetching_remote_playlists
            is SyncDataWithCloudUseCase.State.UploadingPlaylists -> CoreRes.string.cloud_sync_notification_title_uploading_playlists
            SyncDataWithCloudUseCase.State.SavingRemotePlaylists -> CoreRes.string.cloud_sync_notification_title_saving_remote_playlists
            SyncDataWithCloudUseCase.State.Finish -> CoreRes.string.cloud_sync_notification_title_finish
            SyncDataWithCloudUseCase.State.ClearingRemotePlaylistIds -> CoreRes.string.cloud_sync_notification_title_clearing_remote_playlist_ids
            SyncDataWithCloudUseCase.State.FetchingRemoteCollections -> CoreRes.string.cloud_sync_notification_title_fetching_remote_collections
            is SyncDataWithCloudUseCase.State.UploadingCollections -> CoreRes.string.cloud_sync_notification_title_uploading_collections
            SyncDataWithCloudUseCase.State.SavingRemoteCollections -> CoreRes.string.cloud_sync_notification_title_saving_remote_collections
            SyncDataWithCloudUseCase.State.ClearingRemoteCollectionIds -> CoreRes.string.cloud_sync_notification_title_clearing_remote_collection_ids
            SyncDataWithCloudUseCase.State.FetchingRemoteStats -> CoreRes.string.cloud_sync_notification_title_fetching_remote_stats
            is SyncDataWithCloudUseCase.State.UploadingStats -> CoreRes.string.cloud_sync_notification_title_uploading_stats
        }.toTextSpec()

    fun cloudSyncNotificationText(state: SyncDataWithCloudUseCase.State): SoulTextSpec =
        when (state) {
            SyncDataWithCloudUseCase.State.SyncingSettings -> CoreRes.string.cloud_sync_notification_text_syncing_settings.toTextSpec()
            is SyncDataWithCloudUseCase.State.Failure -> CoreRes.string.cloud_sync_notification_text_failure.toTextSpec(
                state.error?.toTextSpec() ?: CoreRes.string.cloud_sync_notification_unknown_error.toTextSpec(),
            )
            SyncDataWithCloudUseCase.State.Finish -> CoreRes.string.cloud_sync_notification_text_finish.toTextSpec()
            SyncDataWithCloudUseCase.State.Idle -> CoreRes.string.cloud_sync_notification_text_idle.toTextSpec()
            SyncDataWithCloudUseCase.State.NoMusicsToSend -> CoreRes.string.cloud_sync_notification_text_no_musics_to_send.toTextSpec()
            SyncDataWithCloudUseCase.State.CheckingMusicsToSend -> CoreRes.string.cloud_sync_notification_text_checking_musics_to_send.toTextSpec()
            SyncDataWithCloudUseCase.State.Cleaning -> CoreRes.string.cloud_sync_notification_text_cleaning.toTextSpec()
            SyncDataWithCloudUseCase.State.ClearingRemoteMusicIds -> CoreRes.string.cloud_sync_notification_text_clearing_remote_music_ids.toTextSpec()
            SyncDataWithCloudUseCase.State.FetchingFromRemote -> CoreRes.string.cloud_sync_notification_text_fetching_from_remote.toTextSpec()
            is SyncDataWithCloudUseCase.State.SavingRemote -> CoreRes.string.cloud_sync_notification_text_saving_remote.toTextSpec()
            is SyncDataWithCloudUseCase.State.UpdateMusics -> CoreRes.string.cloud_sync_notification_text_update_musics.toTextSpec()
            is SyncDataWithCloudUseCase.State.UploadMusics -> CoreRes.string.cloud_sync_notification_text_upload_musics.toTextSpec()
            SyncDataWithCloudUseCase.State.FetchingRemotePlaylists -> CoreRes.string.cloud_sync_notification_text_fetching_remote_playlists.toTextSpec()
            is SyncDataWithCloudUseCase.State.UploadingPlaylists -> CoreRes.string.cloud_sync_notification_text_uploading_playlists.toTextSpec()
            SyncDataWithCloudUseCase.State.SavingRemotePlaylists -> CoreRes.string.cloud_sync_notification_text_saving_remote_playlists.toTextSpec()
            SyncDataWithCloudUseCase.State.ClearingRemotePlaylistIds -> CoreRes.string.cloud_sync_notification_text_clearing_remote_playlist_ids.toTextSpec()
            SyncDataWithCloudUseCase.State.FetchingRemoteCollections -> CoreRes.string.cloud_sync_notification_text_fetching_remote_collections.toTextSpec()
            is SyncDataWithCloudUseCase.State.UploadingCollections -> CoreRes.string.cloud_sync_notification_text_uploading_collections.toTextSpec()
            SyncDataWithCloudUseCase.State.SavingRemoteCollections -> CoreRes.string.cloud_sync_notification_text_saving_remote_collections.toTextSpec()
            SyncDataWithCloudUseCase.State.ClearingRemoteCollectionIds -> CoreRes.string.cloud_sync_notification_text_clearing_remote_collection_ids.toTextSpec()
            SyncDataWithCloudUseCase.State.FetchingRemoteStats -> CoreRes.string.cloud_sync_notification_text_fetching_remote_stats.toTextSpec()
            is SyncDataWithCloudUseCase.State.UploadingStats -> CoreRes.string.cloud_sync_notification_text_uploading_stats.toTextSpec()
        }

    fun duration(duration: Duration): SoulTextSpec {
        val hours = duration.inWholeHours
        val minutes = duration.inWholeMinutes

        return when {
            hours > 0 -> CoreRes.string.duration_format.toTextSpec(
                hours(hours),
                minutes(minutes.mod(60).toLong()),
            )
            minutes > 0 -> CoreRes.string.duration_format.toTextSpec(
                minutes(minutes),
                seconds(duration.inWholeSeconds.mod(60).toLong()),
            )
            else -> seconds(duration.inWholeSeconds)
        }
    }

    fun hours(hours: Long): SoulTextSpec =
        CoreRes.plurals.hours.toTextSpec(hours.toInt(), hours)

    fun minutes(minutes: Long): SoulTextSpec =
        CoreRes.plurals.minutes.toTextSpec(minutes.toInt(), minutes)

    fun seconds(seconds: Long): SoulTextSpec =
        CoreRes.plurals.seconds.toTextSpec(seconds.toInt(), seconds)

    fun period(period: Period): SoulTextSpec =
        when (period) {
            is Period.All -> CoreRes.string.statistics_all_period_label.toTextSpec()
            is Period.Month -> CoreRes.string.period_month_format.toTextSpec(month(period.month), period.year)
            is Period.Year -> period.year.toString().toTextSpec()
        }

    fun month(monthNumber: Int): SoulTextSpec =
        when (monthNumber) {
            1 -> CoreRes.string.month_january
            2 -> CoreRes.string.month_february
            3 -> CoreRes.string.month_march
            4 -> CoreRes.string.month_april
            5 -> CoreRes.string.month_may
            6 -> CoreRes.string.month_june
            7 -> CoreRes.string.month_july
            8 -> CoreRes.string.month_august
            9 -> CoreRes.string.month_september
            10 -> CoreRes.string.month_october
            11 -> CoreRes.string.month_november
            else -> CoreRes.string.month_december
        }.toTextSpec()

    private fun counted(
        total: Int,
        zero: StringResource,
        plural: PluralStringResource,
    ): SoulTextSpec =
        if (total == 0) {
            zero.toTextSpec()
        } else {
            plural.toTextSpec(total, total)
        }
}
