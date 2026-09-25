# Fenix

A customized, lightweight Android browser based on Firefox for Android, tailored for speed, enhanced navigation, and effortless updates.

---

## Features

- **Single-Tab Navigation & Home-Integration:**
  - Shortcuts und Suchen auf der Startseite nutzen standardmäßig den aktiven Tab weiter, anstatt für jeden Klick einen neuen Tab zu erzeugen. Neue Tabs entstehen wie bei Chrome gezielt über das `+` in der Tab-Übersicht.
  - Zurücktaste und Home-Button navigieren den Tab zurück auf `about:home`, wodurch Webseiten im Hintergrund sauber entladen werden und kein visuelles Aufblitzen beim Laden anderer Shortcuts entsteht.
  - **Dynamischer Vorwärts-Button auf dem Homescreen:** Kehrt man von einer Seite zur Startseite zurück, wird der Vorwärts-Button aktiv und bringt einen ohne Neuladen (aus dem BFCache) sofort wieder auf die Seite.
  - **Home-Button auf dem Homescreen:** Ein Klick auf das Home-Symbol auf der Startseite aktiviert direkt die Adressleiste und öffnet die Tastatur.
- **Firefox-Vorschläge Priorisierung:** Firefox-Vorschläge (Chronik, Lesezeichen, offene Tabs) werden standardmäßig vor den Suchmaschinen-Vorschlägen (z. B. Google-Suche) in der Adressleiste platziert. Anpassbar über *Einstellungen → Suche* (Standard: aktiviert).
- **Home-Button Long-Press:** Längerer Druck auf das Home-Symbol in der Navigationsleiste aktiviert sofort die Adressleiste und öffnet die Tastatur.
- **Download-Badge Deaktivierbar:** Der Benachrichtigungspunkt am Download- und Menü-Symbol nach Downloads kann in den Einstellungen flexibel ein- und ausgeschaltet werden (Standard: aus).
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
- **Single-Tab Navigation & Nahtlose Startseiten-Integration:**
  - Shortcuts und Suchen auf der Startseite nutzen automatisch den aktiven Tab weiter, anstatt unnötig neue Tabs zu öffnen (wie bei Chrome entstehen neue Tabs nur noch über `+` im Tab-Manager).
  - Beim Klick auf das Home-Symbol sowie bei der Zurücktaste auf der letzten Webseite navigiert der Tab zu `about:home`. Webseiten werden im Hintergrund entladen, was das Aufblitzen alter Inhalte verhindert.
  - **Vorwärts-Button auf dem Homescreen:** Erkennt den Seitenverlauf und bringt den Nutzer per Klick sofort und ohne Neuladen (BFCache) wieder auf die zuvor geöffnete Webseite.
  - **Home-Button auf dem Homescreen:** Aktiviert direkt die Adressleiste und öffnet die Bildschirmtastatur.
- **Firefox-Vorschläge zuerst anzeigen:**
  - Schalter „Firefox-Vorschläge zuerst anzeigen“ in *Einstellungen → Suche* integriert (standardmäßig aktiviert).
  - Zeigt lokale Chronik, Lesezeichen, offene Tabs und FxSuggest oberhalb der Websuche-Vorschläge (z. B. Google-Suche) in der Adressleiste an.
- **Home-Button Schnellzugriff:**
  - Längerer Druck auf das Home-Symbol in der Navigationsleiste auf Webseiten aktiviert direkt die Adresszeile und öffnet die Tastatur.
- **Download-Badge Einstellung:**
  - Schalter „Download-Badge anzeigen“ in *Einstellungen → Downloads* ergänzt (standardmäßig deaktiviert).
  - Unterdrückt den Benachrichtigungspunkt am Download-Symbol im Menü sowie am 3-Punkte-Menü in der Symbolleiste.
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
