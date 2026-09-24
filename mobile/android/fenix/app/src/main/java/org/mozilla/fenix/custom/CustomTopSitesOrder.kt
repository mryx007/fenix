/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.custom

import android.content.Context
import android.os.StrictMode
import mozilla.components.feature.top.sites.TopSite

/**
 * Manages user-defined shortcut order in SharedPreferences without modifying Room database.
 * Completely protected against StrictMode disk read/write violations on the main thread.
 */
object CustomTopSitesOrder {
    private const val PREFS_NAME = "custom_top_sites_order_prefs"
    private const val KEY_ORDER = "saved_urls_order"

    private inline fun <T> allowDiskReads(block: () -> T): T {
        val oldPolicy = StrictMode.allowThreadDiskReads()
        return try {
            block()
        } finally {
            StrictMode.setThreadPolicy(oldPolicy)
        }
    }

    private inline fun <T> allowDiskWrites(block: () -> T): T {
        val oldPolicy = StrictMode.allowThreadDiskWrites()
        return try {
            block()
        } finally {
            StrictMode.setThreadPolicy(oldPolicy)
        }
    }

    fun getSavedOrder(context: Context): List<String> = allowDiskReads {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_ORDER, null) ?: return@allowDiskReads emptyList()
        raw.split("\n").filter { it.isNotBlank() }
    }

    fun saveOrder(context: Context, urls: List<String>) {
        allowDiskWrites {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_ORDER, urls.joinToString("\n")).apply()
        }
    }

    fun applyOrder(context: Context, topSites: List<TopSite>): List<TopSite> {
        val savedUrls = getSavedOrder(context)
        if (savedUrls.isEmpty()) return topSites

        return topSites.sortedBy { site ->
            val idx = savedUrls.indexOf(site.url)
            if (idx != -1) idx else Int.MAX_VALUE
        }
    }
}
