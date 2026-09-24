/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.custom

import android.content.Context
import android.os.StrictMode
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

/**
 * Storage and reactive state for user-customized top site shortcut size, spacing, and font size.
 * Protected against StrictMode DiskRead/Write violations on the main thread.
 */
object CustomTopSitesSize {
    private const val PREFS_NAME = "custom_top_sites_size_prefs"

    const val KEY_SHORTCUT_SIZE = "custom_top_sites_icon_size"
    const val MIN_SIZE = 32
    const val MAX_SIZE = 72
    const val DEFAULT_SIZE = 60

    const val KEY_SHORTCUT_SPACING = "custom_top_sites_spacing"
    const val MIN_SPACING = 0
    const val MAX_SPACING = 40
    const val DEFAULT_SPACING = 12

    const val KEY_SHORTCUT_FONT_SIZE = "custom_top_sites_font_size"
    const val MIN_FONT_SIZE = 8
    const val MAX_FONT_SIZE = 16
    const val DEFAULT_FONT_SIZE = 12

    var currentSize by mutableIntStateOf(DEFAULT_SIZE)
        private set

    var currentSpacing by mutableIntStateOf(DEFAULT_SPACING)
        private set

    var currentFontSize by mutableIntStateOf(DEFAULT_FONT_SIZE)
        private set

    @Volatile
    private var isInitialized = false

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

    private fun ensureInitialized(context: Context) {
        if (!isInitialized) {
            allowDiskReads {
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                currentSize = prefs.getInt(KEY_SHORTCUT_SIZE, DEFAULT_SIZE).coerceIn(MIN_SIZE, MAX_SIZE)
                currentSpacing = prefs.getInt(KEY_SHORTCUT_SPACING, DEFAULT_SPACING).coerceIn(MIN_SPACING, MAX_SPACING)
                currentFontSize = prefs.getInt(KEY_SHORTCUT_FONT_SIZE, DEFAULT_FONT_SIZE).coerceIn(MIN_FONT_SIZE, MAX_FONT_SIZE)
            }
            isInitialized = true
        }
    }

    fun getSize(context: Context): Int {
        ensureInitialized(context)
        return currentSize
    }

    fun setSize(context: Context, size: Int) {
        val clamped = size.coerceIn(MIN_SIZE, MAX_SIZE)
        currentSize = clamped
        isInitialized = true
        allowDiskWrites {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putInt(KEY_SHORTCUT_SIZE, clamped).apply()
        }
    }

    fun getSpacing(context: Context): Int {
        ensureInitialized(context)
        return currentSpacing
    }

    fun setSpacing(context: Context, spacing: Int) {
        val clamped = spacing.coerceIn(MIN_SPACING, MAX_SPACING)
        currentSpacing = clamped
        isInitialized = true
        allowDiskWrites {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putInt(KEY_SHORTCUT_SPACING, clamped).apply()
        }
    }

    fun getFontSize(context: Context): Int {
        ensureInitialized(context)
        return currentFontSize
    }

    fun setFontSize(context: Context, fontSize: Int) {
        val clamped = fontSize.coerceIn(MIN_FONT_SIZE, MAX_FONT_SIZE)
        currentFontSize = clamped
        isInitialized = true
        allowDiskWrites {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putInt(KEY_SHORTCUT_FONT_SIZE, clamped).apply()
        }
    }
}
