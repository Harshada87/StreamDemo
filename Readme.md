# StreamDemo

[(https://github.com/Harshada87/StreamDemo/actions/workflows/ci.yml/badge.svg)](https://github.com/Harshada87/StreamDemo/actions/workflows/ci.yml)

A small Android streaming app built to practice production-style media playback: adaptive streaming (HLS/DASH), live playback, Widevine DRM, biometric login, playback observability, testable architecture, and an automated, protected delivery workflow.

Kotlin · Jetpack Compose · Media3 ExoPlayer · Hilt · Retrofit · kotlinx.serialization · Coroutines/Flow · BiometricPrompt · GitHub Actions

---

## What it does

1. **Fingerprint login** unlocks the app and starts a session token.
2. **Video catalog** is downloaded from a remote JSON file and shown as a list (HLS, DASH, DRM, Live).
3. **Player** plays the selected stream with Media3 ExoPlayer, including a Widevine-protected stream whose license request carries the session token.
4. **Playback stats overlay** shows time to first frame, current quality, rebuffer count, dropped frames and the last error code.

---

## CI and engineering workflow

Every change goes through a pull request; nothing reaches `main` unless CI is green.

```
Branch → Pull request → CI (build, lint, unit tests) → Merge to protected main
```

- **GitHub Actions** (`.github/workflows/ci.yml`) runs on every pull request and every push to `main`, on a fresh machine each time:
  `assembleDebug` → `lintDebug` → `testDebugUnitTest`, with Gradle caching. Build reports are uploaded when a step fails.
- **Protected `main`** (GitHub ruleset): pull request required, `build-and-test` status check required, force pushes and deletions blocked.
- **Why it matters:** on its first runs, CI caught files that had never been committed and files emptied during a refactor. The project built on my machine but not from a fresh checkout. CI only trusts what's in the repo.

---

## Design doc (short)

**Problem.** Viewers watch live and on-demand video on Android phones and set-top boxes, often on unstable networks. The app keeps playback going by **lowering quality during bad network moments instead of stopping**.

**Non-functional goals**
- Video starts in under 2 s on WiFi (measure the baseline first; track median and p95).
- Rebuffer ratio under 1% of watch time.
- Playback failures under 0.5% of plays; playback survives rotation without restarting.
- Token never logged; DRM keys never reach app code.

**Non-goals (deliberately out of scope)**
- Offline downloads, ads, user profiles: not core to playback.
- Real auth and license servers: Google's Widevine test server is used.

---

## Architecture

### Modules

```
:app  ──▶  :core:data  ──api──▶  :core:model
```

| Module | Type | Contains |
|---|---|---|
| `:core:model` | Pure Kotlin | `Video`, `VideoType` (sealed) |
| `:core:data` | Pure Kotlin | `VideoApi`, `VideoDto`, `VideoMapper`, `VideoRepository`, `RemoteVideoRepository` |
| `:app` | Android | Hilt wiring (`di/`), auth, screens, ViewModels, player, navigation |

**Module rules**
- Dependencies point one way: the core can never see the app, enforced at compile time.
- Core modules are **pure Kotlin** (no Android), so they build and test faster.
- **Hilt wiring stays in the app**: modules expose plain classes; the app decides how they're connected.
- `:core:data` exposes `:core:model` with `api`, because its public functions return `Video`; Retrofit and serialization stay private (`implementation`).

### Layers

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

---

## Key decisions

**1. Model data so invalid states can't exist.** `VideoType` is a sealed interface; only `Drm` carries a `licenseUrl`. A DRM video without a license URL cannot be constructed.

**2. Validate at the network boundary.** The server returns `VideoDto` (untrusted). `toVideoOrNull()` maps it to the domain `Video` and returns `null` for unknown types or DRM items without a license URL; `mapNotNull` skips them. `ignoreUnknownKeys = true` ignores new fields. Old app versions keep working when the server evolves (forward compatibility).

**3. The player lives in the ViewModel.** It survives rotation and is released in `onCleared()`, freeing the hardware decoder. On set-top boxes with a single decoder, a leaked player makes the next playback fail.

**4. Lifecycle-aware playback.** `ON_STOP` remembers the user's play intent and pauses; `ON_START` resumes only if the user was playing.

**5. Authentication proves identity; DRM uses it.** The session token is sent as an `Authorization` header in the Widevine license request. A real license server would check entitlement before returning a key.

**6. Stats in their own class.** `PlaybackStatsTracker` implements Media3's `AnalyticsListener` (unstable API kept in one place). Rebuffers count only buffering *after* the first frame.

**7. Modularize one module per pull request.** Small, reviewable, easy to revert; files moved with `git mv` so content and history are preserved.

---

## Testing strategy

| Layer | How | Runs in CI |
|---|---|---|
| ViewModel logic | JVM unit tests with fakes, `runTest`, `MainDispatcherRule` | ✅ |
| UI | Compose UI tests on hoisted state | Planned (Gradle Managed Devices) |
| Player / DRM | Manual on real devices (hardware decoders, Widevine level) | — |

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

## Roadmap

**Done**
- [x] CI on every pull request; protected `main`
- [x] `:core:model` and `:core:data` extracted as pure Kotlin modules

**Next**
- [ ] Convention plugins (`build-logic`) for shared Android module setup
- [ ] `:core:player` (Android library) and `:core:auth`
- [ ] Feature modules: login, catalog, player
- [ ] `BrandConfig` + thin brand apps, so one codebase serves multiple products
- [ ] Compose UI tests in CI; AI-assisted first-pass review on pull requests
- [ ] Scheduled two-week release train with staged rollout gated by telemetry
- [ ] Room as single source of truth; WorkManager refresh and batched telemetry upload
- [ ] Android TV launcher entry and D-pad focus; device pairing instead of fingerprint on TV

---

## Test streams

Public test content from Apple, Google (Widevine test assets) and ireplay.tv. Stream URLs are served from `app/videos.json`.
