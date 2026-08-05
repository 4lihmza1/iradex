# Iradex

**One intention at a time.**

Iradex is a native Android commitment-alarm prototype. A person chooses one small, meaningful task and a time. When the moment arrives, Iradex rings until they submit fresh photo proof, write a quick progress note, or use the always-visible Emergency stop.

[Download the v0.3.1 closed alpha](https://github.com/4lihmza1/iradex/releases/download/v0.3.1-alpha/Iradex-alpha.apk) · [Read the tester guide](docs/TESTER_GUIDE.md) · [Privacy](docs/PRIVACY.md) · [Share beta feedback](https://github.com/4lihmza1/iradex/issues/new?template=beta-feedback.yml)

## Closed-alpha capabilities

- One active commitment at a time
- Guided alarm-permission setup
- Exact Android alarm scheduling
- Sound, vibration and lock-screen full-screen alert
- Reliable alarm after Iradex is removed from Recent Apps
- Alarm restoration after a phone restart
- Fresh camera proof or a quick progress note
- Local completion history, streak and follow-through score
- Emergency stop on every alarm
- No account, cloud storage, advertising or analytics

## Tester installation

1. Download `Iradex-alpha.apk` from the [v0.3.1 release](https://github.com/4lihmza1/iradex/releases/tag/v0.3.1-alpha).
2. On Android, allow installation from the browser or Files app if requested.
3. Open Iradex and complete Notifications, Exact alarm and Full-screen alert setup.
4. Create a commitment 3–5 minutes ahead and lock the phone.

Android 8.0 or newer is required. This APK is for controlled testing and is not a Play Store release.

## Cloud builds

GitHub Actions builds the Android app so contributors do not need Android Studio:

- **Build Android APK** creates a downloadable workflow artifact on every push.
- **Publish tester APK** creates the versioned GitHub prerelease when a `v*` tag is pushed.

## Privacy and safety

The alpha stores commitments, history and proof locally on the Android device. It has no account or cloud database. Every ringing alarm provides an Emergency stop because safety takes priority over completion and streaks.

Iradex is a productivity prototype—not medical treatment, an ADHD diagnostic tool or an emergency alarm. See the [privacy policy](docs/PRIVACY.md).

## Known limitations

- Photo capture is recorded, but the image is not displayed in progress history.
- Android **Force stop** disables alarms until Iradex is opened again.
- Manufacturer battery management may affect alarm behavior.
- No cloud sync, backup, account recovery or automatic updates.
- Proof confirms a fresh action, not that the work is correct or complete.

## Technology

Kotlin · Jetpack Compose · Android AlarmManager · Direct Boot · SharedPreferences · GitHub Actions · Vercel

## Repository layout

- `app/` — Android application
- `website/` — static beta landing page for Vercel
- `docs/` — tester and privacy documentation
- `.github/workflows/` — cloud APK build and prerelease automation

## License

Copyright © 2026 Ali Hamza. Source-available for evaluation; no redistribution or commercial use without permission.
