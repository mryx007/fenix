/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.StrictMode
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

/**
 * Storage for user-customized top site icons using local app storage and SharedPreferences.
 * Protected against StrictMode DiskRead/Write violations on the main thread.
 */
object CustomTopSitesIcons {
    private const val PREFS_NAME = "custom_top_sites_icons_prefs"
    private const val ICONS_DIR = "custom_top_site_icons"

    var iconVersion by mutableStateOf(0)

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

    fun getIconsFolder(context: Context): File {
        return allowDiskWrites {
            val folder = File(context.filesDir, ICONS_DIR)
            if (!folder.exists()) folder.mkdirs()
            folder
        }
    }

    fun getFileForUrl(context: Context, url: String): File {
        val hash = runCatching {
            val md = MessageDigest.getInstance("MD5")
            md.update(url.toByteArray())
            md.digest().joinToString("") { "%02x".format(it) }
        }.getOrDefault(url.hashCode().toString())
        return File(getIconsFolder(context), "icon_$hash.png")
    }

    fun hasCustomIcon(context: Context, url: String): Boolean = allowDiskReads {
        val file = getFileForUrl(context, url)
        if (file.exists() && file.length() > 0) return@allowDiskReads true

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        !prefs.getString(url, null).isNullOrBlank()
    }

    fun getCustomIconBitmap(context: Context, url: String): Bitmap? = allowDiskReads {
        val file = getFileForUrl(context, url)
        if (file.exists() && file.length() > 0) {
            runCatching { BitmapFactory.decodeFile(file.absolutePath) }.getOrNull()
        } else {
            null
        }
    }

    fun getIconUrl(context: Context, url: String): String? = allowDiskReads {
        val file = getFileForUrl(context, url)
        if (file.exists() && file.length() > 0) {
            return@allowDiskReads Uri.fromFile(file).toString()
        }

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val exact = prefs.getString(url, null)
        if (!exact.isNullOrBlank()) return@allowDiskReads exact

        val host = runCatching { Uri.parse(url).host }.getOrNull()
        if (!host.isNullOrBlank()) {
            val byHost = prefs.getString(host, null)
            if (!byHost.isNullOrBlank()) return@allowDiskReads byHost
        }
        null
    }

    fun setIconUrl(context: Context, url: String, iconUrl: String?) {
        allowDiskWrites {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            if (iconUrl.isNullOrBlank()) {
                editor.remove(url)
                removeLocalIcon(context, url)
            } else {
                editor.putString(url, iconUrl)
            }
            editor.apply()
        }
        iconVersion++
    }

    fun saveLocalIcon(context: Context, url: String, sourceUri: Uri): String? {
        val result = allowDiskWrites {
            try {
                val targetFile = getFileForUrl(context, url)
                context.contentResolver.openInputStream(sourceUri)?.use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }
                if (targetFile.exists() && targetFile.length() > 0) {
                    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    prefs.edit().putString(url, Uri.fromFile(targetFile).toString()).apply()
                    targetFile.absolutePath
                } else {
                    null
                }
            } catch (_: Throwable) {
                null
            }
        }
        if (result != null) {
            iconVersion++
        }
        return result
    }

    fun removeLocalIcon(context: Context, url: String) {
        allowDiskWrites {
            val file = getFileForUrl(context, url)
            if (file.exists()) file.delete()

            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.remove(url)
            runCatching { Uri.parse(url).host }.getOrNull()?.let { editor.remove(it) }
            editor.apply()
        }
        iconVersion++
    }
}
