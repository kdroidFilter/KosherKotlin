# Astronomical Calendar (AstronomicalCalendar)

A practical, user-friendly guide to the AstronomicalCalendar class that powers sunrise, sunset, twilight, solar noon, and temporal hour calculations in KosherKotlin. It is multiplatform (JVM/Android/iOS/JS/WASM) and works with the util calculators (NOAACalculator by default, SunTimesCalculator optional).

If you only need a quick start, jump to Quick start and Common recipes.


Note: All code examples in this guide are available (or have equivalents) in sample/terminalApp/src/commonMain/kotlin/Main.kt.

## Installation

Gradle (Kotlin Multiplatform):

```kotlin
commonMain {
    dependencies {
        implementation("io.github.kdroidfilter:kosherkotlin:<version>")
    }
}
```

API docs: https://kdroidfilter.github.io/KosherKotlin/


## What this class provides

`io.github.kdroidfilter.kosherkotlin.AstronomicalCalendar` exposes high‑level sun-related events for a given date and location:

- Sunrise and Sunset
  - Elevation-adjusted: `sunrise`, `sunset`
  - Sea-level: `seaLevelSunrise`, `seaLevelSunset`
- Twilight
  - Begin/End Civil Twilight (96° zenith)
  - Begin/End Nautical Twilight (102°)
  - Begin/End Astronomical Twilight (108°)
- Solar Noon (Sun Transit): `sunTransit`
- Temporal Hour (shaah zmanis based on sunrise→sunset): `temporalHour`
- Degree-based helpers
  - `getSunriseOffsetByDegrees(zenith)` and `getSunsetOffsetByDegrees(zenith)`
  - `getSunriseSolarDipFromOffset(minutes)` and `getSunsetSolarDipFromOffset(minutes)`
- UTC primitives (minutes from 00:00 UTC)
  - `getUTCSunrise(zenith)`, `getUTCSeaLevelSunrise(zenith)`
  - `getUTCSunset(zenith)`, `getUTCSeaLevelSunset(zenith)`
- Utilities
  - `getSunTransit(startOfDay, endOfDay)` and `getTemporalHour(startOfDay, endOfDay)`
  - `getTimeOffset(time, offsetMillis)` to shift an Instant

Constants for common zeniths are provided: `GEOMETRIC_ZENITH = 90.0`, `CIVIL_ZENITH = 96.0`, `NAUTICAL_ZENITH = 102.0`, `ASTRONOMICAL_ZENITH = 108.0`.


## Concepts in a nutshell

- Zenith angles: Sunrise/sunset are computed around a geometric zenith of ~90°, with refraction and solar radius adjustment bringing typical civil sunrise/sunset to ~90.833°.
- Sea-level vs elevation: Use sea-level times when the intended phenomenon is about light levels (e.g., twilight). Elevation-adjusted times apply to visual sunrise/sunset.
- UTC vs local time: Low-level methods return minutes from 00:00 UTC; high-level properties return Instants. Convert to local with your GeoLocation’s TimeZone.
- Antimeridian: For locations crossing the antimeridian, the class internally adjusts the date (`adjustedLocalDate`) to keep calculations consistent.
- Calculator choice: `astronomicalCalculator` can be swapped. Default is NOAA-based; SunTimes offers a USNO-style model.


## Quick start

- Build a GeoLocation and calendar and read sunrise/sunset:

```kotlin
val tz = kotlinx.datetime.TimeZone.of("America/New_York")
val geo = io.github.kdroidfilter.kosherkotlin.util.GeoLocation(
    name = "New York, NY",
    latitude = 40.7128,
    longitude = -74.0060,
    elevation = 10.0,
    timeZone = tz
)

val ac = io.github.kdroidfilter.kosherkotlin.AstronomicalCalendar(geo)

// For a specific date (time component can be any value)
val date = kotlinx.datetime.LocalDate(2025, 8, 18)
ac.localDateTime = kotlinx.datetime.LocalDateTime(date, kotlinx.datetime.LocalTime(12, 0))

val sunrise = ac.sunrise
val sunset  = ac.sunset

println("Sunrise: ${sunrise?.toLocalDateTime(tz)?.time}")
println("Sunset:  ${sunset?.toLocalDateTime(tz)?.time}")
```

- Choose a different calculator:

```kotlin
ac.astronomicalCalculator = io.github.kdroidfilter.kosherkotlin.util.SunTimesCalculator()
```

- Compute civil/nautical/astronomical twilight:

```kotlin
val beginCivil  = ac.beginCivilTwilight
val endCivil    = ac.endCivilTwilight
val beginNaut   = ac.beginNauticalTwilight
val endNaut     = ac.endNauticalTwilight
val beginAstro  = ac.beginAstronomicalTwilight
val endAstro    = ac.endAstronomicalTwilight
```


## API highlights

Construction and configuration:
- `AstronomicalCalendar(geoLocation: GeoLocation)` — provide location and time zone
- `localDateTime: LocalDateTime` — set the date (time component arbitrary)
- `geoLocation: GeoLocation` — includes latitude, longitude, elevation, and TimeZone
- `astronomicalCalculator: AstronomicalCalculator` — swap algorithms (NOAA default)

Common retrievals (return Instant?):
- `sunrise`, `seaLevelSunrise`
- `sunset`, `seaLevelSunset`
- `beginCivilTwilight`, `endCivilTwilight`
- `beginNauticalTwilight`, `endNauticalTwilight`
- `beginAstronomicalTwilight`, `endAstronomicalTwilight`
- `sunTransit` (solar noon)
- `temporalHour: Long` — milliseconds; `Long.MIN_VALUE` if not computable

Degree-based and UTC helpers:
- `getSunriseOffsetByDegrees(zenith: Double): Instant?`
- `getSunsetOffsetByDegrees(zenith: Double): Instant?`
- `getUTCSunrise(zenith: Double): Double` (minutes from 00:00 UTC; NaN if not computable)
- `getUTCSeaLevelSunrise(zenith: Double): Double`
- `getUTCSunset(zenith: Double): Double`
- `getUTCSeaLevelSunset(zenith: Double): Double`

Derived utilities:
- `getTemporalHour(startOfDay, endOfDay): Long`
- `getSunTransit(startOfDay, endOfDay): Instant?` — midpoint method
- `getSunriseSolarDipFromOffset(minutes: Double): Double` — iterative; slow; don’t use in loops
- `getSunsetSolarDipFromOffset(minutes: Double): Double` — iterative; slow; don’t use in loops
- `getTimeOffset(time: Instant?, offsetMillis: Long): Instant?`


## Common recipes

- Solar noon (chatzos) and shaah zmanis in local time:

```kotlin
val chatzos = ac.sunTransit?.toLocalDateTime(tz)
val shaahMillis = ac.temporalHour
if (shaahMillis != Long.MIN_VALUE) {
    val shaah = kotlin.time.Duration.milliseconds(shaahMillis)
    println("Shaah zmanis: $shaah")
}
```

- 16.1° after sunset (approx. 72 min around Jerusalem equinox):

```kotlin
val zenith = io.github.kdroidfilter.kosherkotlin.AstronomicalCalendar.ASTRONOMICAL_ZENITH - 16.1
val tzeis = ac.getSunsetOffsetByDegrees(zenith)
```

- Convert UTC minutes to local Instant manually (if using UTC primitives):

```kotlin
val utcMinutes = ac.getUTCSunrise(io.github.kdroidfilter.kosherkotlin.AstronomicalCalendar.GEOMETRIC_ZENITH)
if (!utcMinutes.isNaN()) {
    val midnightUtc = kotlinx.datetime.LocalDateTime(date, kotlinx.datetime.LocalTime(0,0)).toInstant(kotlinx.datetime.TimeZone.UTC)
    val instant = midnightUtc + kotlin.time.Duration.parse("${utcMinutes}m")
    val local = instant.toLocalDateTime(tz)
}
```

- Find the zenith that corresponds to an offset before sunrise (slow, iterative):

```kotlin
val dip = ac.getSunriseSolarDipFromOffset(72.0) // degrees below horizon for ~72 minutes before sunrise
```


## Time zones, platforms, and precision notes

- Always set a real IANA TimeZone in your GeoLocation. High-level properties return Instants that you should convert to local using that zone.
- Elevation has modest impact on visual sunrise/sunset. Twilight calculations generally use sea-level times (light level phenomenon).
- Extreme latitudes and certain dates can yield no sunrise/sunset; properties will be null and UTC helpers will return NaN.
- The class adjusts dates for antimeridian crossings based on `GeoLocation.antimeridianAdjustment` to keep event dating consistent.
- Choosing calculators: NOAA is accurate and includes solar noon; SunTimes approximates noon as midpoint between sea-level sunrise/sunset.


## Edge cases and correctness

- At high latitudes and specific seasons, sunrise or sunset may not occur. Expect `null` (for Instants) or `NaN` (for UTC minutes) and handle gracefully.
- `temporalHour` returns `Long.MIN_VALUE` when sunrise/sunset are not available.
- `getSunriseSolarDipFromOffset` and `getSunsetSolarDipFromOffset` are intentionally slow; avoid calling in loops.
- Using a non-local time zone for the location can shift results across midnight; the implementation accounts for date transitions where applicable.


## Performance tips

- Reuse the same AstronomicalCalendar and only update `localDateTime` when scanning multiple days.
- Reuse the chosen `astronomicalCalculator` instead of re-creating it frequently.
- Convert to local time only once per value (Instant → LocalDateTime) when formatting.


## Troubleshooting FAQ

- Sunrise or sunset is null. Why? Possibly because the sun does not rise/set on that date at that latitude, or the zenith you used is incompatible.
- My local time looks wrong. Verify your GeoLocation’s TimeZone and that you convert the Instant via `toLocalDateTime(geo.timeZone)`.
- Should I use sea-level or elevation-adjusted times? Use elevation-adjusted for visual sunrise/sunset; sea-level for light-level-based calculations (twilights, offsets).
- Why is solar noon slightly off from my expectations with SunTimesCalculator? That calculator estimates noon as midpoint, which can differ slightly from astronomical noon.


## Where to go next

- Explore the API docs for `io.github.kdroidfilter.kosherkotlin.AstronomicalCalendar` and `io.github.kdroidfilter.kosherkotlin.util.*` calculators.
- For higher-level halachic zmanim, use `ZmanimCalendar` or `ComplexZmanimCalendar` built on top of AstronomicalCalendar.
- See the util guide (zmanim/UTIL.md) and the hebrewcalendar guide (zmanim/HEBREW_CALENDAR.md) for complementary features.
