# Utility Module (util)

A practical, user-friendly guide to the utility types used throughout the KosherKotlin Zmanim library. These utilities are multiplatform (Kotlin/JVM, Android, iOS, JS, WASM, etc.) and provide geo/time helpers and astronomical calculations that underpin Zmanim computations.

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


## What this module provides

The package io.github.kdroidfilter.kosherkotlin.util includes:

- GeoLocation: A rich location object with latitude, longitude, elevation, TimeZone, and a friendly name. Includes distance/bearing utilities.
- Location: A lightweight coordinate holder (optionally with elevation, accuracy, timestamp, TimeZone, and name) for platforms that can’t always provide full metadata.
- AstronomicalCalculator: Base astronomical helpers (UTC sunrise, sunset, and solar noon; zenith and elevation adjustments; rad/deg conversion).
- NOAACalculator: A concrete calculator based on NOAA/Jean Meeus algorithms; also provides solar position helpers.
- SunTimesCalculator: A USNO-style implementation for sunrise/sunset/noon using a simplified model.
- DateUtils: Helpers such as getJulianDay(LocalDate) and a LocalDate.now() convenience.
- Time: A numeric time container for hours/minutes/seconds/millis (useful for durations like shaah zmanis).
- WeekFormat: Minimal day-of-week formatters for LocalDate (short and long names).


## Concepts in a nutshell

- IANA time zones: Always use a real TimeZone (e.g., "America/New_York") for accurate local times.
- Elevation: Some astronomical algorithms can adjust for elevation, impacting sunrise/sunset by a small margin.
- Zenith: Different twilight definitions use different zenith angles; adjusting zenith changes sunrise/sunset thresholds.
- Geo distance/bearing: For mapping and UX, utilities are provided for geodesic and rhumb line calculations.


## Quick start

- Build a GeoLocation and use it in higher-level calendars:

```kotlin
val tz = kotlinx.datetime.TimeZone.of("America/New_York")
val location = io.github.kdroidfilter.kosherkotlin.util.GeoLocation(
    name = "New York, NY",
    latitude = 40.7128,
    longitude = -74.0060,
    elevation = 10.0,
    timeZone = tz
)
```

- Compute UTC sunrise/sunset with a chosen calculator (advanced/low-level use):

```kotlin
val date = kotlinx.datetime.LocalDate(2025, 5, 1)
val calc = io.github.kdroidfilter.kosherkotlin.util.NOAACalculator()

// Typical civil sunrise/sunset uses a zenith around 90.833 degrees.
val zenith = 90.833
val sunriseUtcMinutes = calc.getUTCSunrise(date, location, zenith, adjustForElevation = true)
val sunsetUtcMinutes  = calc.getUTCSunset(date, location, zenith, adjustForElevation = true)
```

- Format day-of-week with WeekFormat:

```kotlin
val today = kotlinx.datetime.Clock.System.todayIn(tz)
val longName  = io.github.kdroidfilter.kosherkotlin.util.WeekFormat.long.format(today)
val shortName = io.github.kdroidfilter.kosherkotlin.util.WeekFormat.short.format(today)
```

- Get a Julian Day for a LocalDate:

```kotlin
val jd = io.github.kdroidfilter.kosherkotlin.util.DateUtils.getJulianDay(date)
```


## The main types and how to use them

### GeoLocation

A data class modeling a physical place and its time zone.

Key properties and invariants:
- timeZone: kotlinx.datetime.TimeZone tied to the location.
- locationName: display-friendly name.
- latitude: degrees in [-90, +90]; negative values south of the equator.
- longitude: degrees in [-180, +180]; negative values east of the Prime Meridian (note this historical convention in code comments). Always verify your sign.
- elevation: meters above sea level. NaN and Infinity rejected.

Constructors:
- GeoLocation(name: String, latitude: Double, longitude: Double, timeZone: TimeZone)
- GeoLocation(name: String, latitude: Double, longitude: Double, elevation: Double, timeZone: TimeZone)
- GeoLocation(location: Location): convenience to build from a lightweight Location.

Helpers and utilities:
- setLatitude/Longitude with DMS and compass direction strings (N/S/E/W) to set coordinates precisely.
- getGeodesicInitialBearing(to: GeoLocation): initial great-circle bearing in degrees.
- getGeodesicFinalBearing(to: GeoLocation): final great-circle bearing.
- getGeodesicDistance(to: GeoLocation): geodesic distance (Vincenty formula), meters.
- vincentyFormula(to: GeoLocation, formula: Int): internal advanced usage.
- getRhumbLineBearing(to: GeoLocation): constant-bearing route initial bearing.
- getRhumbLineDistance(to: GeoLocation): rhumb line distance.
- toXML(): a simple XML representation.

Tip: If you change the timeZone after a calendar was created with this GeoLocation, update the calendar’s internal timezone-aware date/time to keep results consistent (see comments in code).


### Location

A lightweight alternative when you may not have a TimeZone or name.

Fields:
- latitude, longitude
- elevation: Double?
- accuracy: Double? (meters)
- timestamp: Long? (epoch millis)
- tz: TimeZone?
- locationName: String?

Use Location when collecting raw coordinates (e.g., from GPS or platform location APIs). Convert to GeoLocation when you want to compute zmanim.


### AstronomicalCalculator (base)

Low-level computational helpers used by higher-level calendars. Exposes UTC-based sunrise, sunset, and noon calculations.

- getUTCSunrise(date, geoLocation, zenith, adjustForElevation): Double
- getUTCSunset(date, geoLocation, zenith, adjustForElevation): Double
- getUTCNoon(date, geoLocation): Double
- getElevationAdjustment(elevation): Double
- adjustZenith(zenith, elevation): Double
- toDegrees(rad): Double, toRadians(deg): Double

Return values for sunrise/sunset/noon are minutes from 00:00 UTC on that date.


### NOAACalculator

A concrete implementation based on NOAA/Jean Meeus. In addition to sunrise/sunset/noon, it includes functions for:
- Solar declination, equation of time
- Hour angles at sunrise/sunset
- Solar elevation/azimuth for a given LocalDateTime

Use this for accurate civil sunrise/sunset or when solar position is needed.


### SunTimesCalculator

A USNO-style model that computes sunrise/sunset/noon with a simpler approach. Suitable for comparative purposes and environments where the NOAA approach is not required.


### DateUtils

- getJulianDay(date: LocalDate): Double — Julian day number at start of day (fractional portion can be added by you later).
- LocalDate.now(): LocalDate — convenience for the system default time zone.


### Time

A numeric time container used in parts of the API to represent durations like a temporal hour.

- Constructors from millis (Int or Double) convert to hours/minutes/seconds/millis and preserve negativity.
- time: Double — returns total milliseconds (negative if isNegative).


### WeekFormat

Minimal day-of-week formatting helpers for LocalDate:
- WeekFormat.long: e.g., "MONDAY"
- WeekFormat.short: e.g., "MON"


## Common recipes

- Convert raw coordinates to GeoLocation, adding a best-guess time zone:

```kotlin
fun toGeoLocation(raw: io.github.kdroidfilter.kosherkotlin.util.Location): io.github.kdroidfilter.kosherkotlin.util.GeoLocation? {
    val tz = raw.tz ?: return null // you must supply a TimeZone for astronomical times
    val name = raw.locationName ?: "Unnamed"
    val elevation = raw.elevation ?: 0.0
    return io.github.kdroidfilter.kosherkotlin.util.GeoLocation(
        name = name,
        latitude = raw.latitude,
        longitude = raw.longitude,
        elevation = elevation,
        timeZone = tz
    )
}
```

- Compute civil sunrise/sunset in local time (manual approach):

```kotlin
val utcMinutes = calc.getUTCSunrise(date, location, zenith, adjustForElevation = true)
val midnightUtc = kotlinx.datetime.LocalDateTime(date, kotlinx.datetime.LocalTime(0,0)).toInstant(kotlinx.datetime.TimeZone.UTC)
val sunriseInstant = midnightUtc + kotlin.time.Duration.parse("${utcMinutes}m")
val localSunrise = sunriseInstant.toLocalDateTime(location.timeZone)
```

- Distance and bearing between two points:

```kotlin
val jerusalem = io.github.kdroidfilter.kosherkotlin.util.GeoLocation(
    name = "Jerusalem",
    latitude = 31.778,
    longitude = 35.235,
    elevation = 800.0,
    timeZone = kotlinx.datetime.TimeZone.of("Asia/Jerusalem")
)
val nyc = location
val distanceMeters = jerusalem.getGeodesicDistance(nyc)
val initialBearing = jerusalem.getGeodesicInitialBearing(nyc)
val finalBearing = jerusalem.getGeodesicFinalBearing(nyc)
```

- Adjust a zenith for elevation when computing sunrise/sunset:

```kotlin
val adjustedZenith = io.github.kdroidfilter.kosherkotlin.util.AstronomicalCalculator().adjustZenith(90.833, location.elevation)
```


## Time zones, platforms, and precision notes

- Always provide a real IANA TimeZone for GeoLocation. Without it, convert your times explicitly from UTC.
- JS/WASM: ensure time zone data is available (see project README note about @js-joda/timezone) when converting Instant to local times.
- Elevation: modest impact on times; only some algorithms use it. When in doubt, use adjustForElevation = true.
- Longitude sign convention: The legacy comment in GeoLocation mentions negative values east of Greenwich — double-check your data source and ensure consistency when constructing locations.


## Edge cases and caveats

- getUTCSunrise/getUTCSunset may return NaN for locations/dates where the sun does not rise or set.
- High latitudes may yield extreme or undefined results for certain dates; handle missing results gracefully.
- Changing a GeoLocation’s timeZone after use in a calendar requires updating the calendar’s internal date-time to maintain consistent outputs.


## Performance tips

- Reuse calculator instances if calling repeatedly in tight loops.
- Prefer NOAA for accuracy; USNO can be faster in some cases but is a simplified model.
- Avoid unnecessary conversions between UTC and local time; perform local conversion once at the end.


## Troubleshooting FAQ

- Why do I get NaN from getUTCSunrise? The sun might not rise or set on that date at that latitude (e.g., near the poles), or the zenith is incompatible with that date/location.
- My local time looks wrong. Ensure you used the correct TimeZone, and remember that the raw results are minutes from 00:00 UTC.
- Do I need elevation? It refines sunrise/sunset a bit. For sea-level calculations, set elevation to 0 and/or adjustForElevation = false.
- What zenith should I use? Civil sunrise/sunset typically use 90°50′ (≈90.833). Astronomical twilight uses ~108°, nautical twilight ~102°, civil twilight ~96°.


## Where to go next

- Explore the API docs for io.github.kdroidfilter.kosherkotlin.util.*
- See how GeoLocation is used by ZmanimCalendar and ComplexZmanimCalendar in this repository.
- Combine these utilities with the hebrewcalendar package to build full daily Jewish information for your app.
