/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.custom

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.StrictMode
import androidx.preference.PreferenceManager
import org.mozilla.fenix.ext.components

/**
 * Service that clears tabs when the user swipes away the application from Android Recents,
 * if enabled in user preferences.
 */
class AppCloseCleanupService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        try {
            val oldPolicy = StrictMode.allowThreadDiskReads()
            val shouldClean = try {
                PreferenceManager.getDefaultSharedPreferences(applicationContext)
                    .getBoolean("pref_key_close_tabs_on_task_removed", true)
            } finally {
                StrictMode.setThreadPolicy(oldPolicy)
            }

            if (shouldClean) {
                // Synchronously delete persisted session state from disk
                components.core.sessionStorage.clear()
                // Clear in-memory tabs
                components.useCases.tabsUseCases.removeNormalTabs()
                components.useCases.tabsUseCases.removePrivateTabs()
            }
        } catch (_: Throwable) {
            // Ignore during process termination
        }
        stopSelf()
    }
}
