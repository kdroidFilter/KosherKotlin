# KosherKotlin Zmanim (Kotlin Multiplatform)

A small, cross‑platform library to calculate sunrise, sunset, halachic times (zmanim), and Hebrew calendar information.

API docs: https://kdroidfilter.github.io/KosherKotlin/
Live WASM demo: https://kdroidfilter.github.io/KosherKotlin/app/

Install (Gradle KMP):
```kotlin
commonMain {
    dependencies {
        implementation("io.github.kdroidfilter:kosherkotlin:<version>")
    }
}
```

Note for JS/WASM: ensure IANA time zone data is available in your app (e.g., via `@js-joda/timezone`).


## Start here: the tiniest example

Print today’s sunrise in New York.
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
val date = kotlinx.datetime.LocalDate(2025, 8, 18)
ac.localDateTime = kotlinx.datetime.LocalDateTime(date, kotlinx.datetime.LocalTime(12, 0))

val sunrise = ac.sunrise
println(sunrise?.toLocalDateTime(tz)?.time)
```

Note: All code examples in this README are available as a runnable sample in app/terminalApp/src/commonMain/kotlin/Main.kt.

What you see is a local time. Behind the scenes it’s computed from an Instant so it is safe on all platforms.


## Level up: a few core zmanim

List some common times and print them nicely.
```kotlin
val tz = geo.timeZone
val f = io.github.kdroidfilter.kosherkotlin.ZmanDescriptionFormatter()

listOf(
    zc.alosHashachar,
    zc.sunrise,
    zc.chatzos,
    zc.sofZmanShmaGRA,
    zc.sofZmanTfilaGRA,
    zc.plagHamincha,
    zc.sunset,
    zc.tzais
).forEach { z ->
    val label = f.formatShortDescription(z, includeElevationDescription = false)
    println("$label -> ${z.formatted(tz)}")
}
```

Tip: Zman.DateBased.momentOfOccurrence can be null (e.g., at extreme latitudes). Always handle N/A.


## More power: ComplexZmanimCalendar

Need more opinions (degrees/offsets, alternate day definitions)? Use ComplexZmanimCalendar.
```kotlin
val czc = io.github.kdroidfilter.kosherkotlin.ComplexZmanimCalendar(geo)
czc.localDateTime = zc.localDateTime

// Example: list everything it offers
for (z in czc.allZmanim) {
    println(z.formatted(geo.timeZone))
}
```

- Elevation usage beyond sunrise/sunset is controlled by `zc.isUseElevation` (default false).
- Candle lighting offset is `zc.candleLightingOffset` (default 18 minutes before sea‑level sunset).

Learn more: the high‑level guide for ZmanimCalendar, Zman, and ZmanDescriptionFormatter lives here:
- zmanim/ZMANIM_CALENDAR.md


## Hebrew calendar in one minute

Get Hebrew date, parsha, yom tov, and omer for a specific date (same date used in the terminal sample).
```kotlin
val date = kotlinx.datetime.LocalDate(2025, 8, 18)
val jc = io.github.kdroidfilter.kosherkotlin.hebrewcalendar.JewishCalendar(date, isInIsrael = true)
val hdf = io.github.kdroidfilter.kosherkotlin.hebrewcalendar.HebrewDateFormatter()

println(hdf.format(jc))        // Hebrew date
println(hdf.formatParsha(jc))  // Weekly parsha (if any)
println(hdf.formatYomTov(jc))  // Yom Tov name (if any)
println(hdf.formatOmer(jc))    // Omer (in season)
```

Learn more: the Hebrew calendar guide explains all features and edge cases:
- zmanim/HEBREW_CALENDAR.md


## Time zones, astronomy, and precision

All sun calculations run through `AstronomicalCalendar`, with NOAA as the default calculator.
- Convert Instants to local time with your GeoLocation’s TimeZone: `instant.toLocalDateTime(tz)`.
- Sea‑level vs elevation:
  - Visual sunrise/sunset often include elevation adjustments.
  - Light‑level phenomena (twilights, degree offsets) typically use sea‑level.
- Antimeridian crossing is internally handled for dating via `GeoLocation.antimeridianAdjustment`.

Learn more: 
- Astronomical calendar guide: zmanim/ASTRONOMICAL_CALENDAR.md
- Utilities (GeoLocation, astronomical calculators, DateUtils): zmanim/UTIL.md


## From simple to advanced: a quick path

1) Minimal
```kotlin
// Sunrise today at my place
println(io.github.kdroidfilter.kosherkotlin.ZmanimCalendar(geo).sunrise.momentOfOccurrence?.toLocalDateTime(geo.timeZone)?.time)
```

2) Core day plan
```kotlin
val f = io.github.kdroidfilter.kosherkotlin.ZmanDescriptionFormatter()
for (z in io.github.kdroidfilter.kosherkotlin.ZmanimCalendar(geo).allZmanim) {
    println(f.formatShortDescription(z, false) + ": " + z.formatted(geo.timeZone))
}
```

3) Opinions and degrees
```kotlin
val czc = io.github.kdroidfilter.kosherkotlin.ComplexZmanimCalendar(geo)
val tzais161 = czc.getSunsetOffsetByDegrees(
    io.github.kdroidfilter.kosherkotlin.AstronomicalCalendar.ASTRONOMICAL_ZENITH - 16.1
)
println(tzais161?.toLocalDateTime(geo.timeZone)?.time)
```

4) Integrate Hebrew calendar
```kotlin
val today = zc.localDateTime.date
val jc = io.github.kdroidfilter.kosherkotlin.hebrewcalendar.JewishCalendar(today, isInIsrael = false)
println(io.github.kdroidfilter.kosherkotlin.hebrewcalendar.HebrewDateFormatter().format(jc))
```


## Documentation map

- High‑level Zmanim (ZmanimCalendar, Zman, ZmanDescriptionFormatter): zmanim/ZMANIM_CALENDAR.md
- Astronomical layer (sunrise/sunset/twilight/noon, zeniths, UTC helpers): zmanim/ASTRONOMICAL_CALENDAR.md
- Hebrew calendar utilities (JewishCalendar, HebrewDateFormatter, TefilaRules, Daf Yomi): zmanim/HEBREW_CALENDAR.md
- Utilities (GeoLocation, NOAACalculator, SunTimesCalculator, DateUtils, Time): zmanim/UTIL.md
- API docs (generated KDoc): https://kdroidfilter.github.io/KosherKotlin/
- WASM demo: https://kdroidfilter.github.io/KosherKotlin/app/


## Apps

- app/composeApp: Luach, a Compose Multiplatform app (Metro DI, MVVM/MVI) showing zmanim, the Hebrew month, festivals and daf yomi for a chosen location.
- app/terminalApp: a simple CLI using the commonMain APIs.


## Status and goals

This fork is actively maintained to be Kotlin Multiplatform first. It may diverge from upstream where it better serves KMP use.
- KMP readiness (JVM/Android/iOS/JS/WASM)
- Improved docs with practical recipes (see guides above)
- Stabilize tests across targets
- Continue refactoring to modern Kotlin style


## Contributing

Contributions are very welcome: features, docs, and tests. Please open issues/PRs.


## License & attribution

LGPL 2.1. Based on the KosherJava Zmanim API by Eliyahu Hershfeld; fork lineage credits Sternbach-Software/KosherKotlin.


## Disclaimer

Double‑check all times before using for halachic decisions. Provided as‑is without warranty.

