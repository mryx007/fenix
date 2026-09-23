/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.custom

import android.content.Context
import android.content.SharedPreferences
import android.os.StrictMode
import androidx.preference.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import mozilla.components.compose.browser.toolbar.concept.Action
import mozilla.components.compose.browser.toolbar.concept.Action.ActionButtonRes
import mozilla.components.compose.browser.toolbar.concept.Action.TabCounterAction
import mozilla.components.ui.icons.R as iconsR

/**
 * Manages configuration and presentation transformations for the bottom navigation bar.
 * Caches preference states in memory to prevent StrictMode disk read violations in Compose.
 */
object CustomNavbarManager {
    const val PREF_KEY_BACK = "pref_key_navbar_back"
    const val PREF_KEY_FORWARD = "pref_key_navbar_forward"
    const val PREF_KEY_HOME = "pref_key_navbar_home"
    const val PREF_KEY_NEW_TAB = "pref_key_navbar_new_tab"
    const val PREF_KEY_TAB_COUNTER = "pref_key_navbar_tab_counter"
    const val PREF_KEY_MENU = "pref_key_navbar_menu"
    const val PREF_KEY_SHARE = "pref_key_navbar_share"
    const val PREF_KEY_BOOKMARK = "pref_key_navbar_bookmark"

    const val ID_BACK = "back"
    const val ID_FORWARD = "forward"
    const val ID_HOME = "home"
    const val ID_NEW_TAB = "new_tab"
    const val ID_BOOKMARK = "bookmark"
    const val ID_SHARE = "share"
    const val ID_TAB_COUNTER = "tab_counter"
    const val ID_MENU = "menu"

    @Volatile private var isBackEnabled: Boolean = true
    @Volatile private var isForwardEnabled: Boolean = true
    @Volatile private var isHomeEnabled: Boolean = true
    @Volatile private var isNewTabEnabled: Boolean = true
    @Volatile private var isTabCounterEnabled: Boolean = true
    @Volatile private var isMenuEnabled: Boolean = true
    @Volatile private var isShareEnabled: Boolean = false
    @Volatile private var isBookmarkEnabled: Boolean = false

    private val _version = MutableStateFlow(0)
    val version: StateFlow<Int> = _version.asStateFlow()

    @Volatile private var initialized = false

    private val prefListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        if (key != null && key.startsWith("pref_key_navbar_")) {
            val oldPolicy = StrictMode.allowThreadDiskReads()
            try {
                when (key) {
                    PREF_KEY_BACK -> isBackEnabled = prefs.getBoolean(PREF_KEY_BACK, true)
                    PREF_KEY_FORWARD -> isForwardEnabled = prefs.getBoolean(PREF_KEY_FORWARD, true)
                    PREF_KEY_HOME -> isHomeEnabled = prefs.getBoolean(PREF_KEY_HOME, true)
                    PREF_KEY_NEW_TAB -> isNewTabEnabled = prefs.getBoolean(PREF_KEY_NEW_TAB, true)
                    PREF_KEY_TAB_COUNTER -> isTabCounterEnabled = prefs.getBoolean(PREF_KEY_TAB_COUNTER, true)
                    PREF_KEY_MENU -> isMenuEnabled = prefs.getBoolean(PREF_KEY_MENU, true)
                    PREF_KEY_SHARE -> isShareEnabled = prefs.getBoolean(PREF_KEY_SHARE, false)
                    PREF_KEY_BOOKMARK -> isBookmarkEnabled = prefs.getBoolean(PREF_KEY_BOOKMARK, false)
                }
                _version.value += 1
            } finally {
                StrictMode.setThreadPolicy(oldPolicy)
            }
        }
    }

    @Synchronized
    fun ensureInitialized(context: Context) {
        if (!initialized) {
            val oldPolicy = StrictMode.allowThreadDiskReads()
            try {
                val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
                isBackEnabled = prefs.getBoolean(PREF_KEY_BACK, true)
                isForwardEnabled = prefs.getBoolean(PREF_KEY_FORWARD, true)
                isHomeEnabled = prefs.getBoolean(PREF_KEY_HOME, true)
                isNewTabEnabled = prefs.getBoolean(PREF_KEY_NEW_TAB, true)
                isTabCounterEnabled = prefs.getBoolean(PREF_KEY_TAB_COUNTER, true)
                isMenuEnabled = prefs.getBoolean(PREF_KEY_MENU, true)
                isShareEnabled = prefs.getBoolean(PREF_KEY_SHARE, false)
                isBookmarkEnabled = prefs.getBoolean(PREF_KEY_BOOKMARK, false)

                prefs.registerOnSharedPreferenceChangeListener(prefListener)
                initialized = true
            } finally {
                StrictMode.setThreadPolicy(oldPolicy)
            }
        }
    }

    fun isActionEnabled(id: String): Boolean {
        return when (id) {
            ID_BACK -> isBackEnabled
            ID_FORWARD -> isForwardEnabled
            ID_HOME -> isHomeEnabled
            ID_NEW_TAB -> isNewTabEnabled
            ID_TAB_COUNTER -> isTabCounterEnabled
            ID_MENU -> isMenuEnabled
            ID_SHARE -> isShareEnabled
            ID_BOOKMARK -> isBookmarkEnabled
            else -> true
        }
    }

    fun identifyAction(action: Action): String {
        return when (action) {
            is TabCounterAction -> ID_TAB_COUNTER
            is ActionButtonRes -> {
                when (action.drawableResId) {
                    iconsR.drawable.mozac_ic_back_24, iconsR.drawable.mozac_ic_chevron_left_24 -> ID_BACK
                    iconsR.drawable.mozac_ic_forward_24, iconsR.drawable.mozac_ic_chevron_right_24 -> ID_FORWARD
                    iconsR.drawable.mozac_ic_home_24 -> ID_HOME
                    iconsR.drawable.mozac_ic_plus_24 -> ID_NEW_TAB
                    iconsR.drawable.mozac_ic_bookmark_24,
                    iconsR.drawable.mozac_ic_bookmark_tray_24,
                    iconsR.drawable.mozac_ic_bookmark_fill_24 -> ID_BOOKMARK
                    iconsR.drawable.mozac_ic_share_android_24 -> ID_SHARE
                    iconsR.drawable.mozac_ic_ellipsis_vertical_24, iconsR.drawable.mozac_ic_app_menu_24 -> ID_MENU
                    else -> "other"
                }
            }
            else -> "other"
        }
    }

    /**
     * Filters navigation actions according to in-memory cached user preferences in standard browser order:
     * Back, Forward, Home, New Tab, Bookmark, Share, Tab Counter, Menu.
     */
    fun applyCustomizations(context: Context, actions: List<Action>): List<Action> {
        if (actions.isEmpty()) return actions
        ensureInitialized(context)

        val actionsById = mutableMapOf<String, Action>()
        val others = mutableListOf<Action>()

        for (action in actions) {
            val id = identifyAction(action)
            if (id != "other") {
                actionsById[id] = action
            } else {
                others.add(action)
            }
        }

        val preferredOrder = listOf(
            ID_BACK,
            ID_FORWARD,
            ID_HOME,
            ID_NEW_TAB,
            ID_BOOKMARK,
            ID_SHARE,
            ID_TAB_COUNTER,
            ID_MENU,
        )

        val result = mutableListOf<Action>()
        for (id in preferredOrder) {
            val action = actionsById[id]
            if (action != null && isActionEnabled(id)) {
                result.add(action)
            }
        }

        for (other in others) {
            result.add(other)
        }

        return result
    }
}
