# Iradex

Iradex is an Android commitment-alarm prototype. A user chooses one small, meaningful task and a time. When the alarm starts, they capture fresh camera proof of progress to complete the commitment.

## What the alpha includes

- One active commitment at a time
- Exact-time Android alarm scheduling
- Full-screen alarm with sound and vibration
- A visible Emergency stop for safety
- Fresh camera proof (gallery uploads are disabled)
- Local-only commitment and progress history
- No account, cloud storage, analytics, or advertising

## Tester build

Open the repository's **Releases** section and download `Iradex-alpha.apk`. Android 8.0 or later is required. This prototype is distributed for private testing and is not yet a Play Store release.

## Cloud build

GitHub Actions builds the APK, so contributors do not need Android Studio:

1. Open **Actions** → **Build Android APK**.
2. Choose **Run workflow**.
3. Download the `Iradex-alpha` artifact after the build passes.

To publish a downloadable prerelease, run **Publish tester APK**.

## Privacy and safety

The alpha stores commitments and history in Android private local storage. Proof photos are created in the app's private cache and are not uploaded. Every alarm provides an Emergency stop because encouragement must never become entrapment.

## Prototype limitations

- Photo proof confirms a fresh camera capture, not whether the submitted work is correct.
- Samsung and other Android devices may require users to allow exact alarms, notifications, full-screen notifications, and background activity.
- The alpha is unsigned with a production key and should not be treated as a production release.

## Technology

Native Kotlin, Jetpack Compose, Android AlarmManager, SharedPreferences, and GitHub Actions.

## License

Copyright © 2026 Ali Hamza. Source-available for evaluation; no redistribution or commercial use without permission.

