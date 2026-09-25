# Fenix

A customized, lightweight Android browser based on Firefox for Android, tailored for speed, enhanced navigation, and effortless updates.

---

## Features

- **Download-Badge Deaktivierbar:** Der Benachrichtigungspunkt am Download- und Menü-Symbol nach Downloads kann in den Einstellungen flexibel ein- und ausgeschaltet werden (Standard: aus).
- **Startseiten-Navigation im selben Tab:** URLs und Verknüpfungen von der Startseite werden standardmäßig im aktuellen Tab geöffnet statt jedes Mal einen neuen Tab anzulegen (in den Einstellungen anpassbar).
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
- **Download-Badge Einstellung:**
  - Schalter „Download-Badge anzeigen“ in *Einstellungen → Downloads* ergänzt (standardmäßig deaktiviert).
  - Unterdrückt den Benachrichtigungspunkt am Download-Symbol im Menü sowie am 3-Punkte-Menü in der Symbolleiste.
- **Startseiten-Navigation im selben Tab:**
  - Schalter „Links und Verknüpfungen im selben Tab öffnen“ in *Einstellungen → Startseite* ergänzt (standardmäßig aktiviert).
  - Verhindert das unabsichtliche Öffnen unzähliger neuer Tabs beim Aufrufen von URLs oder Verknüpfungen von der Startseite.
- **Update-Prüfung & GitHub Actions Fix:**
  - `versionName` und Release-Tag im Build-Workflow synchronisiert, sodass keine fehlerhaften Update-Meldungen mehr in Endlosschleife erscheinen.
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
