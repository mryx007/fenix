# Fenix

A customized, lightweight Android browser based on Firefox for Android, tailored for speed, enhanced navigation, and effortless updates.

---

## Features

- **Prioritize Firefox Suggestions:** Firefox suggestions (history, bookmarks, open tabs) are placed above search engine suggestions (e.g. Google Search) by default in the address bar. Configurable in *Settings → Search* (Default: enabled).
- **Home Button Long-Press:** Long-pressing the Home icon in the navigation toolbar on web pages instantly focuses the address bar and opens the keyboard.
- **Configurable Download Badge:** The notification dot on the download and menu icons after completed downloads can be toggled on/off in settings (Default: disabled).
- **Added Configurable In-App Updater:** Background update checks can be toggled on/off with customizable check intervals (1–24 hours) and effortless one-tap installation prompt.
- **Added Shortcut Customization:** Preference sliders for shortcut icon size (32–72 dp), spacing (0–40 dp), and font size (8–16 sp) with dynamic width adaptation to prevent unwanted word wrapping.
- **Expanded Homepage Shortcuts:** Displays up to 16 shortcuts on the homepage (4 per row) with left-aligned incomplete rows and intelligent "Show all" visibility.
- **Added Expanded Navigation Toolbar:** Quick-access actions placed directly in the bottom toolbar (Back, Forward, Home, New Tab, Bookmarks, Share, and Tab Switcher).
- **Added Auto-Close Tabs on Exit:** Cleans up open tabs automatically when leaving the app for a clean start every time.
- **Added Custom Shortcut Icons:** Select custom images from device gallery or storage with offline persistence.
- **Added Direct Shortcut Reordering:** Reorder shortcuts effortlessly via the long-press menu (`◀ Move left` / `Move right ▶`).
- **Added Borderless Shortcuts:** Shortcuts no longer have a white border.

---

## Changelog

### Latest Changes
- **Show Firefox Suggestions First:**
  - Added a "Show Firefox suggestions first" switch in *Settings → Search* (enabled by default).
  - Displays local history, bookmarks, open tabs, and FxSuggest above web search suggestions (e.g., Google Search) in the awesomebar.
- **Home Button Quick Access:**
  - Long-pressing the Home icon in the navigation bar on web pages immediately focuses the address bar and opens the keyboard.
- **Download Badge Setting:**
  - Added a "Show download badge" toggle in *Settings → Downloads* (disabled by default).
  - Suppresses the notification dot on the download icon in the menu and on the 3-dot toolbar menu.
- **Update Checking & GitHub Actions Fix:**
  - Synchronized `versionName` and release tags in the build workflow to prevent endless false-positive update notifications.
- **Shortcuts Customization & Grid Layout:**
  - Added preference sliders in *Settings → Homepage → Shortcuts* for Icon Size, Spacing, and Font Size.
  - Dynamically scaled label containers and row layouts so multi-word shortcut titles don't wrap awkwardly.
  - Raised homescreen shortcut capacity to 16 (4 columns per row).
  - Incomplete rows now cleanly align to the left under preceding columns.
  - Conditioned "Show all" button so it only appears when shortcuts actually exceed homepage capacity.
- **In-App Updater Settings:**
  - Added background check toggle and interval slider (1 to 24 hours) in *Settings → About Fenix*.
  - StrictMode optimization to prevent main-thread disk read violations.

---

## Download

Get the latest signed APK directly from the [GitHub Releases](https://github.com/mryx007/fenix/releases/latest) page.
