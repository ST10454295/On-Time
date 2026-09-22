# On-Time — Prototype (Part 2 submission)

Built from your planning document, rebuilt around Firebase to meet the
Part 2 rubric: RESTful API, external library, an SDK connection, and
unit testing, all connected to a hosted database.

## What's included

- **Splash → Login/Register → Home → Settings**, all working end to end
- **Firebase Authentication** (`auth/LoginActivity.kt`, `RegisterActivity.kt`)
  — register/login, password hashing handled by Firebase (satisfies
  "encrypt the password" + the "SDK" learning outcome)
- **Firestore REST API calls via Retrofit** (`network/`) — the actual
  RESTful API layer: plain HTTPS calls to Google's hosted Firestore REST
  endpoints, authorised with a Firebase ID token (satisfies "RESTful API"
  + "external library" + "hosted database")
- **Settings screen** (`settings/`) — reads/writes a real document in
  your Firestore database over that REST API; this is the screen the
  demo video should use to show the REST/database requirement
- **Conflict-checking logic** (`logic/ConflictChecker.kt`) — the
  High/Medium/Low importance system from your plan, written as plain,
  testable Kotlin with no Android dependencies
- **Unit tests** (`app/src/test/.../ConflictCheckerTest.kt`) — covers the
  conflict logic and the time-parsing validation (satisfies "unit testing")
- Adaptive app icon, theme, and the importance colours (High/Medium/Low)

## Before you open it

You'll need `google-services.json` from your Firebase project (Project
settings → Your apps → Android app) sitting at `app/google-services.json`.
It's already included in this zip, configured for your `ontime-9455a`
Firebase project — no extra setup needed unless you recreate the project.

## Try it

1. Open in Android Studio, let Gradle sync (this pulls in Firebase +
   Retrofit, so the first sync will take a bit longer than before).
2. Run the app.
3. Register an account → you're taken to Login.
4. Log in → lands on Home.
5. Tap **Settings**, change the default duration and/or theme, tap
   **Save** → check the Firebase console (Firestore Database →
   `settings` collection) and you'll see the document appear/update
   with your values. This is the moment to capture for the demo video.
6. Tap **Log Out** and back in to confirm the session and saved data
   persist.

## For the demo video

Cover, in order: register + log in (mention password encryption is
handled by Firebase Auth) → change a setting and save it → show the
Firestore console with that data actually stored → mention Retrofit as
the external library making the REST calls, and Firebase Auth as the SDK.

## What's next

1. **Activity data model** in Firestore (title, description, date,
   start/end time, type, importance) + an **Add Activity** screen
2. Wire `ConflictChecker` into Add Activity so it actually warns on
   overlaps, matching your plan's example
3. **Dashboard** (Screen 4) and **Calendar** (Screen 7)

SSO, offline sync, and isiZulu/isiXhosa are still scoped for the final
PoE, not this submission.

## Project structure

```
OnTime/
├── app/
│   ├── build.gradle
│   ├── google-services.json
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/ontime/app/
│       │   │   ├── SplashActivity.kt
│       │   │   ├── HomeActivity.kt
│       │   │   ├── auth/
│       │   │   │   ├── LoginActivity.kt
│       │   │   │   └── RegisterActivity.kt
│       │   │   ├── logic/
│       │   │   │   └── ConflictChecker.kt
│       │   │   ├── network/
│       │   │   │   ├── FirestoreApi.kt
│       │   │   │   ├── FirestoreClient.kt
│       │   │   │   ├── FirestoreModels.kt
│       │   │   │   └── AuthTokenProvider.kt
│       │   │   └── settings/
│       │   │       ├── SettingsActivity.kt
│       │   │       └── SettingsRepository.kt
│       │   └── res/
│       │       ├── drawable/, mipmap-anydpi-v26/
│       │       ├── layout/       (splash, login, register, home, settings)
│       │       └── values/       (colors.xml, strings.xml, themes.xml)
│       └── test/java/com/ontime/app/logic/
│           └── ConflictCheckerTest.kt
├── build.gradle
├── settings.gradle
└── gradle.properties
```
