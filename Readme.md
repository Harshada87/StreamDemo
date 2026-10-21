# StreamDemo

A small Android streaming app built to practice production-style media playback: adaptive streaming (HLS/DASH), live playback, Widevine DRM, biometric login, playback observability, and testable architecture.

Kotlin · Jetpack Compose · Media3 ExoPlayer · Hilt · Retrofit · kotlinx.serialization · Coroutines/Flow · BiometricPrompt

---

## What it does

1. **Fingerprint login** unlocks the app and starts a session token.
2. **Video catalog** is downloaded from a remote JSON file and shown as a list (HLS, DASH, DRM, Live).
3. **Player** plays the selected stream with Media3 ExoPlayer, including a Widevine-protected stream whose license request carries the session token.
4. **Playback stats overlay** shows time to first frame, current quality, rebuffer count, dropped frames and the last error code.

---

## Design doc (short)

**Problem.** Viewers watch live and on-demand video on Android phones and set-top boxes, often on unstable networks. The app keeps playback going by **lowering quality during bad network moments instead of stopping**.

**Non-functional goals**
- Video starts in under 2 s on WiFi (measure the baseline first).
- Rebuffer ratio under 1% of watch time.
- Playback failures under 0.5% of plays; playback survives rotation without restarting.
- Token never logged; DRM keys never reach app code.

**Non-goals (deliberately out of scope)**
- Offline downloads, ads, user profiles: not core to playback.
- Real auth and license servers: Google's Widevine test server is used.

---

## Architecture

```
UI (Compose screens)            Login · VideoList · Player (+ stats overlay)
   ↓ events      ↑ StateFlow
ViewModels                       LoginViewModel · VideoListViewModel · PlayerViewModel
   ↓
Repositories (interfaces)        AuthRepository · VideoRepository
   ↓
Data sources                     InMemoryAuthRepository · RemoteVideoRepository → VideoApi (Retrofit)
```

- **MVVM with unidirectional data flow**: state flows down as `StateFlow`, events flow up as callbacks.
- **Hilt** provides repositories and network objects; ViewModels depend only on interfaces.
- **Navigation Compose**: Login → List → Player(videoId). The player reads `videoId` from `SavedStateHandle`, so it survives process death.

### Package structure
```
data/
  model/        Video, VideoType (sealed)
  auth/         AuthRepository, InMemoryAuthRepository
  remote/       VideoApi, VideoDto, VideoMapper
  repository/   VideoRepository, RemoteVideoRepository
di/             NetworkModule, RepositoryModule
ui/
  login/        LoginScreen, LoginViewModel, BiometricAuthenticator, BiometricResult
  videolist/    VideoListScreen, VideoListContent, VideoListViewModel
  player/       PlayerScreen, PlayerViewModel, MediaItemFactory, PlaybackStatsTracker, PlaybackStatsOverlay
  navigation/   StreamDemoNavHost, Routes
```

---

## Key decisions

**1. Model data so invalid states can't exist.**
`VideoType` is a sealed interface; only `Drm` carries a `licenseUrl`. A DRM video without a license URL cannot be constructed.

**2. Validate at the network boundary.**
The server returns `VideoDto` (untrusted). `toVideoOrNull()` maps it to the domain `Video` and returns `null` for unknown types or DRM items without a license URL; `mapNotNull` skips them. `ignoreUnknownKeys = true` ignores new fields. Old app versions keep working when the server evolves (forward compatibility).

**3. The player lives in the ViewModel.**
It survives rotation and is released in `onCleared()`, freeing the hardware decoder. On set-top boxes with a single decoder, a leaked player makes the next playback fail.

**4. Lifecycle-aware playback.**
`ON_STOP` remembers the user's play intent and pauses; `ON_START` resumes only if the user was playing. Rotation causes a brief pause, then playback continues from the same position.

**5. Authentication proves identity; DRM uses it.**
The session token is sent as an `Authorization` header in the Widevine license request. A real license server would check entitlement before returning a key. The fingerprint prompt lives in a helper that holds the Activity; the ViewModel only receives the result.

**6. Stats in their own class.**
`PlaybackStatsTracker` implements Media3's `AnalyticsListener` (unstable API kept in one place). Rebuffers count only buffering *after* the first frame.

---

## Testing strategy

| Layer | How | Examples |
|---|---|---|
| ViewModel logic | JVM unit tests with fakes, `runTest`, `MainDispatcherRule` | VideoList: Success, Error, Retry. Login: Success starts session, Cancel shows no error, Error message, no fingerprint |
| UI | Compose UI tests on hoisted state | List shows titles, click sends the id, Retry calls back |
| Player / DRM | Manual on real devices | Depends on hardware decoders and Widevine level |

`FakeVideoRepository` (test source set) can simulate a network failure with `shouldFail = true`.

---

## Observability

The overlay shows the same signals a device fleet would report as telemetry:

| Metric | Question it answers |
|---|---|
| Time to first frame | How long did the viewer wait? |
| Resolution / bitrate | What quality is ABR delivering? |
| Rebuffer count | Did playback freeze? |
| Dropped frames | Is the device struggling to decode? |
| Last error code | Which layer failed: network, decoder or DRM? |

---

## Next steps

1. **Room as the single source of truth** for the catalog, so it shows instantly and offline.
2. **WorkManager** periodic catalog refresh with a network constraint and backoff.
3. **Telemetry upload**: store playback events locally, upload in batches via WorkManager with jitter, slice by device model and app version.
4. **Real device authentication**: Keystore-backed key unlocked by biometrics; on TV, device pairing instead of fingerprint.
5. **Android TV**: leanback launcher entry and D-pad focus handling.
6. **Modularization**: `:core`, `:feature-player`, `:feature-login` with convention plugins.
7. **CI**: GitHub Actions running lint, unit tests and Compose tests on every pull request.
8. **Player factory injection** so `PlayerViewModel` can be unit tested with a fake player.

---

## Test streams

Public test content from Apple, Google (Widevine test assets) and ireplay.tv. Stream URLs are served from `app/videos.json`.
