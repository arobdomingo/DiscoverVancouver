# Discover Vancouver Android app

This module is the native Java client for the Spring Boot backend in the
repository root.

## What this checkpoint contains

- A discovery form for category, budget, atmosphere, walking range, indoor
  preference, and open-now filtering.
- Optional coarse device location, with downtown Vancouver as a safe default.
- An HTTP client that calls `GET /api/recommendations` and parses its JSON.
- Ranked recommendation cards with loading, empty, and error states.
- A details screen showing the address, tags, and every recommendation reason.

The UI does not calculate recommendations. It gathers preferences and displays
the backend's response. This preserves the architecture from the original plan:

```text
Android UI -> HTTP/JSON -> Spring Boot -> MongoDB/weather
```

## Running it locally

1. Install Android Studio and its Android 37 SDK.
2. Open the `android-app` directory in Android Studio and let Gradle sync.
3. From the repository root, start MongoDB with `docker compose up -d`.
4. Start the backend with `mvn spring-boot:run`.
5. Run the Android app on an emulator.

The debug build uses `http://10.0.2.2:8080/`. Android emulators reserve
`10.0.2.2` as an alias for the development computer's localhost. A physical
phone instead needs the Mac's LAN IP in the debug `API_BASE_URL` inside
`app/build.gradle`.

## Code tour

- `MainActivity` reads the form, requests location permission, and controls UI
  state.
- `RecommendationQuery` translates the form into backend query parameters.
- `RecommendationApiClient` performs network work off the main thread.
- `Recommendation` translates each JSON object into a Java object.
- `RecommendationAdapter` binds those objects to scrolling result cards.
- `PlaceDetailActivity` displays the selected result without another request.

## Next Android checkpoint

Replace last-known location with a fresh location request, add a map/directions
action, and persist the most recent preferences locally.
