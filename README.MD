# KosherKotlin Zmanim API ✨

KosherKotlin Zmanim is a Kotlin Multiplatform library for calculating various astronomical and Jewish religious times (zmanim), such as sunrise, sunset, and prayer times. This library is a fork of Sternbach-Software/KosherKotlin, which itself is a fork of Eliyahu Hershfeld's KosherJava Zmanim. It is tailored to work on modern platforms and adapted to meet evolving needs.

## Status 🚀
This fork is actively maintained and has already undergone significant updates, including:

- Published to Maven for easier integration.
- Enhanced compatibility with Kotlin/JS and Kotlin/WASM.
- Reorganized code to ensure it functions as a fully Kotlin Multiplatform (KMP) library.
- Initial steps toward refactoring the code for improved readability and maintainability.
- Efforts underway to refactor and streamline documentation.

Add the dependency:
```kotlin
implementation("io.github.kdroidfilter:kosherkotlin:2.5.3")
```
It is compatible with all platforms supported by Kotlin Multiplatform.

Generated API docs: https://kdroidfilter.github.io/KosherKotlin/

Live WASM demo: https://kdroidfilter.github.io/KosherKotlin/sample/

While most features are functional, not all tests pass at the moment. Please report issues or contribute fixes.

## Philosophy 🌍
Unlike the official KosherJava Zmanim library, this fork may not always stay synchronized with upstream updates. While I aim to track significant new features and improvements, this fork will be adapted to suit specific requirements and priorities.

---

# How to use this project (focus: commonMain)

This guide explains the core APIs exposed from commonMain and how to use them across KMP targets (JVM, Android, iOS, JS, WASM, etc.). If you only read one section, read Quick start and Concepts.

## 1) Installation and targets

- Gradle dependency (KMP):
  ```kotlin
  commonMain {
      dependencies {
          implementation("io.github.kdroidfilter:kosherkotlin:2.5.3")
      }
  }
  ```
- Kotlin/JS and WASM note: the library references the IANA time zone database. To ensure time zones work in JS/WASM, make sure the npm package `@js-joda/timezone` is available in your build. If you use Kotlin Gradle with JS/WASM, declare it in the appropriate NPM dependencies (your plugin may add it transitively; if not, add it yourself).

## 2) Quick start

Calculate basic zmanim for a location and date.

```kotlin
val tz = kotlinx.datetime.TimeZone.of("America/New_York")
val location = io.github.kdroidfilter.kosherkotlin.util.GeoLocation(
    name = "New York, NY",
    latitude = 40.7128,
    longitude = -74.0060,
    elevation = 10.0, // meters, optional
    timeZone = tz
)

val czc = io.github.kdroidfilter.kosherkotlin.ComplexZmanimCalendar(location)

// For today in the location's time zone
val today = kotlinx.datetime.Clock.System.todayIn(tz)
czc.localDateTime = kotlinx.datetime.LocalDateTime(today, kotlinx.datetime.LocalTime(12, 0)) // time component can be any value

val sunrise = czc.sunrise.momentOfOccurrence
val sunset = czc.sunset.momentOfOccurrence

// All available zmanim (moments and values) exposed by ComplexZmanimCalendar
val all = czc.allZmanim

// Print a few
println("Sunrise: ${sunrise?.toLocalDateTime(tz)?.time}")
println("Sunset:  ${sunset?.toLocalDateTime(tz)?.time}")

// Filter by type (e.g., all Alos times)
val allAlos = all.filter { it.definition.type == io.github.kdroidfilter.kosherkotlin.metadata.ZmanType.ALOS }
println(allAlos.map { it.formatted(tz) })
```

## 3) Concepts you need to know

- GeoLocation: latitude, longitude, elevation (meters), and a kotlinx.datetime TimeZone.
- ZmanimCalendar vs ComplexZmanimCalendar:
  - ZmanimCalendar: core, commonly used zmanim (sunrise, sunset, sof zman shma, etc.).
  - ComplexZmanimCalendar: extends ZmanimCalendar and exposes many additional opinions and computations (e.g., degrees-based alos/tzais, Ateret Torah, many shaos zmaniyos variations).
- Zman<T> sealed class:
  - Zman.DateBased: has a momentOfOccurrence: Instant? (null if it doesn’t occur for that date/location).
  - Zman.ValueBased: has a duration: Duration (e.g., a shaah zmanis).
  - Zman.formatted(TimeZone): helper to produce a quick human-readable value.
- Metadata: every zman carries a ZmanDefinition describing how it was computed (type, calculation method, if elevation is used, supporting authorities). ZmanDescriptionFormatter can produce short/long textual descriptions.
- Elevation: czc.isUseElevation controls whether elevation is applied beyond sunrise/sunset where halachically appropriate; defaults to false. Candle lighting offset can be customized via czc.candleLightingOffset.

## 4) Working with time zones (multiplatform)

- Always provide a proper TimeZone for your GeoLocation. Use kotlinx.datetime:
  - JVM/Android/iOS: TimeZone.of("America/New_York") works.
  - JS/WASM: ensure tz data is present. This project references `@js-joda/timezone` to bundle tz data. If your app doesn’t include it, add it to NPM dependencies.
- When displaying times, convert Instant to local using toLocalDateTime(tz):
  ```kotlin
  val local = czc.sunrise.momentOfOccurrence?.toLocalDateTime(tz)
  println(local?.time)
  ```

## 5) Picking the right calendar

- Start simple:
  - ZmanimCalendar(location) if you need core zmanim.
  - ComplexZmanimCalendar(location) if you need many variants/opinions.
- ComplexZmanimCalendar convenience constructor:
  ```kotlin
  val czc = ComplexZmanimCalendar(
      location = location,
      date = Clock.System.todayIn(location.timeZone),
      useElevation = false,
      ateretTorahSunsetOffset = 40.0, // minutes after sunset
      candleLightingOffset = 18.0
  )
  ```

## 6) Accessing results safely

- Many astronomical events may not occur at extreme latitudes. Always treat Zman.DateBased.momentOfOccurrence as nullable.
- For durations (shaos zmaniyos), ensure duration.isFinite().
- Example:
  ```kotlin
  val chatzos = czc.chatzos.momentOfOccurrence
  if (chatzos == null) {
      // handle gracefully (e.g., hide row, show N/A)
  }
  ```

## 7) Describing how a zman was calculated

Use ZmanDescriptionFormatter to explain a value to users.
```kotlin
val formatter = ZmanDescriptionFormatter()
val zman = czc.sofZmanShmaGRA
println(formatter.formatShortDescription(zman, includeElevationDescription = true))
println(formatter.formatLongDescription(zman))
```
This yields clear strings like "Fixed Local Chatzos" or degree/opinion-based explanations, and whether elevation affects the result.

## 8) Hebrew calendar utilities (commonMain)

The hebrewcalendar package provides Hebrew date logic independent of platform:

- JewishCalendar: calculates parsha, yom tov, omer, rosh chodesh, etc.
- HebrewDateFormatter: renders Hebrew dates, parsha names, and other strings.

Example:
```kotlin
val tz = kotlinx.datetime.TimeZone.of("Asia/Jerusalem")
val todayIL = kotlinx.datetime.Clock.System.now().toLocalDateTime(tz).date
val jc = io.github.kdroidfilter.kosherkotlin.hebrewcalendar.JewishCalendar(todayIL, isInIsrael = true)

val hdf = io.github.kdroidfilter.kosherkotlin.hebrewcalendar.HebrewDateFormatter()
println(hdf.format(jc))              // Full Hebrew date
println(hdf.formatParsha(jc))        // Weekly parsha (if any)
println(hdf.formatYomTov(jc))        // Yom Tov name (if any)
println(hdf.formatOmer(jc))          // Omer count (in season)
```

## 9) Practical examples

- List all zmanim with local times and brief descriptions:
  ```kotlin
  val tz = location.timeZone
  val formatter = ZmanDescriptionFormatter()
  for (z in czc.allZmanim) {
      val label = formatter.formatShortDescription(z, includeElevationDescription = false)
      println("$label -> ${z.formatted(tz)}")
  }
  ```
- Get all sunrise-related zmanim:
  ```kotlin
  val sunRelated = czc.allZmanim.filter { it.definition.type in setOf(
      io.github.kdroidfilter.kosherkotlin.metadata.ZmanType.HANAITZ,
      io.github.kdroidfilter.kosherkotlin.metadata.ZmanType.SHKIAH,
      io.github.kdroidfilter.kosherkotlin.metadata.ZmanType.ALOS,
      io.github.kdroidfilter.kosherkotlin.metadata.ZmanType.TZAIS,
    )
  }
  ```

## 10) Tips, defaults, and edge cases

- Elevation defaults: czc.isUseElevation = false. Toggle to true only with proper halachic guidance.
- Candle lighting: default is 18 minutes before sea-level sunset; customize with czc.candleLightingOffset.
- Ateret Torah: czc.ateretTorahSunsetOffset defaults to 40 minutes globally; in Israel many use 25.
- Antimeridian and unusual time zones: GeoLocation handles local mean time and antimeridian adjustments. Always supply a real IANA time zone ID.
- Extreme latitudes: some Instant results will be null; handle N/A gracefully.

## 11) Where to look in the code (commonMain)

- io.github.kdroidfilter.kosherkotlin.ZmanimCalendar: core zmanim.
- io.github.kdroidfilter.kosherkotlin.ComplexZmanimCalendar: extended opinions and many additional zmanim and shaos zmaniyos.
- io.github.kdroidfilter.kosherkotlin.Zman and ZmanDescriptionFormatter: values and human-readable descriptions.
- io.github.kdroidfilter.kosherkotlin.util.GeoLocation: location, time zone, and geo utilities.
- io.github.kdroidfilter.kosherkotlin.hebrewcalendar.*: JewishCalendar, HebrewDateFormatter, and related types.

## 12) Samples

- sample/composeApp: a Compose Multiplatform UI that lists zmanim for a chosen location.
- sample/terminalApp: a simple terminal program using the commonMain APIs.

## Roadmap 🔄
- Documentation: consolidate and modernize examples.
- Testing and stability across platforms.
- Potential integration improvements and publishing refinements.
- Continued refactoring to modern Kotlin style.

## Contributing 📚
Contributions are welcome! Whether through code, documentation, or testing, your help will make this library more robust and versatile. Open a PR or reach out with your ideas.

## License & Attribution ✉️
This fork is released under the LGPL 2.1 license.

## Disclaimer ⚠️
While I strive for accuracy, please double-check zmanim for halachic decisions. This library is provided "as-is" without warranty of correctness for religious purposes.

