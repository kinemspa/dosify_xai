# Dosify

Dosify is an Android app for medication scheduling, inventory tracking, and reconstitution calculations. Built with Kotlin in Android Studio, using a modular structure for maintainability: shared logic in `core/`, feature-specific code in `feature/`. Targets min SDK 24, with MVVM architecture, Hilt DI, and Jetpack Compose UI.

## Features
- **Medication Management**: Add/edit meds with type (tablet/injection/drops/other), strength, stock, low threshold.
- **Scheduling**: Dose schedules with frequency, times, start/end dates, active status.
- **Inventory**: Track stock decrements on dose confirm, low stock notifications.
- **Logs**: Dose logs with notes/reactions.
- **Supplies**: Linked supplies tracking.
- **Reconstitution**: Calculations for injections/peptides (powder/solvent to concentration/volume).
- **Graphs**: Dose history visualization using MPAndroidChart.
- **Auth/Security**: Firebase auth (email/Google/Apple), biometrics, encrypted Room DB (SQLCipher).
- **Sync/Notifs**: Firebase Firestore sync, AlarmManager/WorkManager for notifications/reminders.
- **IAP Upgrades**: Multi-meds, personas, cycling, titration (gated via Google Billing).
- **Diagnostics**: Test DB integrity, locale-based Firebase disable (e.g., China).

## Setup/Installation
1. **Prerequisites**: Android Studio (latest stable), Java 11+.
2. **Clone Repo**: `git clone https://github.com/yourusername/dosify.git`
3. **Open Project**: In Android Studio, open the Dosify folder.
4. **Sync Gradle**: Click Sync button; deps fetch from Maven Central/JitPack/Google.
5. **Firebase Setup**: Create Firebase project, add google-services.json to app/ (auth/firestore/messaging).
6. **SQLCipher**: DB encrypted—passphrase from biometrics/utils.
7. **Run**: Build/run on emulator/device (API 24+).

## Usage
- Launch: Shows greeting (placeholder)—extend to NavHost with Compose screens (meds list, add form, schedule calendar).
- Add Med: Feature/meds screen form, save to Room.
- Schedule: Select med, set doses, auto-notifs.
- Log Dose: Confirm → stock--, log insert.
- Graphs: View history in reports screen.
- IAP: Billing check for advanced (cycling/titration).

## Contributing
- Fork repo.
- Branch per feature/phase (e.g., feature/supplies).
- PR with changes, tests.
- Follow modular: Core shared, features isolated.

## License
MIT License—free to use/modify.

## Credits
Built with guidance from Grok 4 by xAI.