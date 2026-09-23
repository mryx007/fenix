package org.mozilla.fenix.custom

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.mozilla.fenix.BuildConfig
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object FenixUpdater {

    private const val GITHUB_REPO = "mryx007/fenix"
    private const val RELEASES_API_URL = "https://api.github.com/repos/$GITHUB_REPO/releases/latest"
    private const val PREFS_NAME = "fenix_updater_prefs"
    private const val KEY_LAST_CHECK = "last_check_timestamp"
    private const val CHECK_INTERVAL_MS = 4 * 60 * 60 * 1000L // alle 4 Stunden im Hintergrund prüfen

    fun checkForUpdatesSilently(activity: Activity) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val lastCheck = prefs.getLong(KEY_LAST_CHECK, 0L)
                val now = System.currentTimeMillis()
                if (now - lastCheck < CHECK_INTERVAL_MS) {
                    return@launch
                }
                prefs.edit().putLong(KEY_LAST_CHECK, now).apply()

                val releaseInfo = fetchLatestRelease() ?: return@launch

                val currentVersion = BuildConfig.VERSION_NAME
                if (!isVersionNewer(releaseInfo.version, currentVersion)) {
                    return@launch
                }

                val downloadUrl = releaseInfo.apkDownloadUrl ?: return@launch

                // APK im Hintergrund laden (falls noch nicht vorhanden)
                val updatesDir = File(activity.cacheDir, "updates")
                if (!updatesDir.exists()) updatesDir.mkdirs()
                val targetFile = File(updatesDir, "fenix-${releaseInfo.tagName}.apk")
                val apkFile = if (targetFile.exists() && targetFile.length() > 5 * 1024 * 1024) {
                    targetFile
                } else {
                    downloadApk(activity, downloadUrl, releaseInfo.tagName) { /* silent */ }
                }

                withContext(Dispatchers.Main) {
                    if (!activity.isFinishing && !activity.isDestroyed) {
                        promptInstall(activity, apkFile)
                    }
                }
            } catch (_: Throwable) {
                // Im Hintergrund still ignorieren
            }
        }
    }

    fun checkForUpdates(activity: Activity) {
        Toast.makeText(activity, "Suche nach Updates...", Toast.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val releaseInfo = withContext(Dispatchers.IO) {
                    fetchLatestRelease()
                }

                if (releaseInfo == null) {
                    Toast.makeText(
                        activity,
                        "Keine Releases auf GitHub gefunden.",
                        Toast.LENGTH_LONG,
                    ).show()
                    return@launch
                }

                val currentVersion = BuildConfig.VERSION_NAME
                val isNewer = isVersionNewer(releaseInfo.version, currentVersion)

                if (isNewer) {
                    showUpdateDialog(activity, releaseInfo)
                } else {
                    Toast.makeText(
                        activity,
                        "Fenix ist aktuell ($currentVersion).",
                        Toast.LENGTH_LONG,
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    activity,
                    "Fehler beim Update-Check: ${e.localizedMessage ?: "Verbindungsfehler"}",
                    Toast.LENGTH_LONG,
                ).show()
            }
        }
    }

    private fun showUpdateDialog(activity: Activity, release: ReleaseInfo) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Update verfügbar: ${release.tagName}")
        val changelog = if (release.body.isNotBlank()) "\n\n${release.body.take(300)}" else ""
        builder.setMessage("Eine neue Version von Fenix ist verfügbar.$changelog\n\nMöchtest du sie herunterladen und installieren?")

        if (release.apkDownloadUrl != null) {
            builder.setPositiveButton("Aktualisieren") { _, _ ->
                downloadAndInstall(activity, release.apkDownloadUrl, release.tagName)
            }
        }

        builder.setNeutralButton("GitHub") { _, _ ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(release.htmlUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            activity.startActivity(intent)
        }

        builder.setNegativeButton("Später", null)
        builder.show()
    }

    @Suppress("DEPRECATION")
    private fun downloadAndInstall(activity: Activity, downloadUrl: String, tagName: String) {
        val progressDialog = ProgressDialog(activity).apply {
            setTitle("Fenix Update herunterladen")
            setMessage("Wird geladen...")
            setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            isIndeterminate = false
            max = 100
            setCancelable(false)
            show()
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val apkFile = withContext(Dispatchers.IO) {
                    downloadApk(activity, downloadUrl, tagName) { progress ->
                        CoroutineScope(Dispatchers.Main).launch {
                            progressDialog.progress = progress
                        }
                    }
                }

                progressDialog.dismiss()
                promptInstall(activity, apkFile)
            } catch (e: Exception) {
                progressDialog.dismiss()
                Toast.makeText(
                    activity,
                    "Download fehlgeschlagen: ${e.localizedMessage}",
                    Toast.LENGTH_LONG,
                ).show()
            }
        }
    }

    private fun downloadApk(
        context: Context,
        downloadUrl: String,
        tagName: String,
        onProgress: (Int) -> Unit,
    ): File {
        val updatesDir = File(context.cacheDir, "updates")
        if (!updatesDir.exists()) {
            updatesDir.mkdirs()
        }

        val destinationFile = File(updatesDir, "fenix-$tagName.apk")
        var currentUrl = downloadUrl

        // Follow HTTP redirects (GitHub releases redirect to AWS S3)
        var connection: HttpURLConnection
        var redirectCount = 0
        while (true) {
            val url = URL(currentUrl)
            connection = url.openConnection() as HttpURLConnection
            connection.instanceFollowRedirects = false
            connection.setRequestProperty("User-Agent", "Fenix-Updater")
            connection.connect()

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                responseCode == 307 || responseCode == 308
            ) {
                val location = connection.getHeaderField("Location")
                connection.disconnect()
                if (location != null && redirectCount < 5) {
                    currentUrl = location
                    redirectCount++
                    continue
                }
            }
            break
        }

        val totalLength = connection.contentLength
        connection.inputStream.use { input ->
            FileOutputStream(destinationFile).use { output ->
                val buffer = ByteArray(8 * 1024)
                var bytesRead: Int
                var totalBytesRead = 0L

                while (input.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                    totalBytesRead += bytesRead
                    if (totalLength > 0) {
                        val progress = ((totalBytesRead * 100) / totalLength).toInt()
                        onProgress(progress)
                    }
                }
                output.flush()
            }
        }
        connection.disconnect()

        return destinationFile
    }

    private fun promptInstall(activity: Activity, apkFile: File) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!activity.packageManager.canRequestPackageInstalls()) {
                val manageIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = Uri.parse("package:${activity.packageName}")
                }
                activity.startActivity(manageIntent)
                Toast.makeText(
                    activity,
                    "Bitte Installation aus dieser Quelle erlauben und erneut tippen.",
                    Toast.LENGTH_LONG,
                ).show()
                return
            }
        }

        val authority = "${activity.packageName}.fileprovider"
        val apkUri = FileProvider.getUriForFile(activity, authority, apkFile)

        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        activity.startActivity(installIntent)
    }

    private fun fetchLatestRelease(): ReleaseInfo? {
        val url = URL(RELEASES_API_URL)
        val conn = url.openConnection() as HttpURLConnection
        conn.setRequestProperty("User-Agent", "Fenix-Updater")
        conn.setRequestProperty("Accept", "application/vnd.github.v3+json")
        conn.connectTimeout = 10000
        conn.readTimeout = 10000

        try {
            if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                return null
            }

            val text = conn.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(text)

            val tagName = json.optString("tag_name", "")
            val htmlUrl = json.optString("html_url", "")
            val body = json.optString("body", "")

            var apkUrl: String? = null
            val assets = json.optJSONArray("assets")
            if (assets != null) {
                for (i in 0 until assets.length()) {
                    val asset = assets.getJSONObject(i)
                    val name = asset.optString("name", "")
                    if (name.endsWith(".apk", ignoreCase = true)) {
                        apkUrl = asset.optString("browser_download_url")
                        // Prefer arm64-v8a or universal apk if available
                        if (name.contains("arm64", ignoreCase = true)) {
                            break
                        }
                    }
                }
            }

            val cleanVersion = tagName.removePrefix("v").removePrefix("fenix-").trim()
            return ReleaseInfo(
                tagName = tagName,
                version = cleanVersion,
                htmlUrl = htmlUrl,
                body = body,
                apkDownloadUrl = apkUrl,
            )
        } finally {
            conn.disconnect()
        }
    }

    internal fun isVersionNewer(remoteVersion: String, currentVersion: String): Boolean {
        if (remoteVersion.isBlank() || currentVersion.isBlank()) return false
        val remoteParts = remoteVersion.split(".", "-").mapNotNull { it.toIntOrNull() }
        val currentParts = currentVersion.split(".", "-").mapNotNull { it.toIntOrNull() }

        val maxLen = maxOf(remoteParts.size, currentParts.size)
        for (i in 0 until maxLen) {
            val r = remoteParts.getOrElse(i) { 0 }
            val c = currentParts.getOrElse(i) { 0 }
            if (r > c) return true
            if (r < c) return false
        }
        return false
    }

    data class ReleaseInfo(
        val tagName: String,
        val version: String,
        val htmlUrl: String,
        val body: String,
        val apkDownloadUrl: String?,
    )
}
