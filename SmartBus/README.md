# SmartBus

Beginner-friendly Android app built with Java + XML (no backend).

## Configuration
- App Name: SmartBus
- Package: `com.smartbus.app`
- Min SDK: 24
- Target SDK: 35

## Implemented
- Splash screen with simple animation and 2.5s delay
- Register + Login flow using SQLite (`users` table)
- Session management with `SharedPreferences`
- Bottom navigation with 4 tabs: Home, Track, Pass, Profile
- Logout from Profile screen
- Smart Bus Pass system using SQLite (`passes` and `history` tables)
- Create Pass, View Pass list, Pass Details, Delete Pass, and travel history logging
- Auto-expiry status (`Valid` / `Expired`) based on pass validity date

## Authentication Flow
1. `SplashActivity` starts first.
2. If user session exists -> opens `MainActivity`.
3. If no session -> opens `LoginActivity`.
4. New users register in `RegisterActivity` and then login.

## Pass Flow
1. Open `PassActivity` from bottom navigation.
2. Tap `Create Pass` to open `CreatePassActivity`.
3. Fill Name, Route, Validity date and tap `Generate Pass`.
4. View all passes in RecyclerView cards.
5. Tap any pass to open `PassDetailsActivity`.
6. From details, use pass (saves travel history) or delete pass.

## Run
1. Open this project in Android Studio.
2. Sync Gradle.
3. Run app on emulator/device (API 24+).

## Notes
- Data is stored locally in SQLite for demo purpose.
- Passwords are stored as plain text in this beginner setup.

