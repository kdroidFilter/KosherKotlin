# Zmanim Calendar Guide (ZmanimCalendar, Zman, ZmanDescriptionFormatter)

A practical, user-friendly guide to the high-level Zmanim APIs in KosherKotlin:
- ZmanimCalendar: core halachic day computations (sunrise, sunset, sof zman shma/tfila, chatzos, etc.).
- Zman: the sealed type returned for each item (either a moment in time or a duration).
- ZmanDescriptionFormatter: human-friendly textual descriptions of how a zman was calculated.

This guide complements the lower-level AstronomicalCalendar and the extended ComplexZmanimCalendar. It is multiplatform (Kotlin/JVM, Android, iOS, JS, WASM, etc.).

If you only need a quick start, jump to Quick start and Common recipes.


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


## What these types provide

- io.github.kdroidfilter.kosherkotlin.ZmanimCalendar
  - Extends AstronomicalCalendar and exposes commonly used halachic times.
  - Elevation toggle for broader application beyond sunrise/sunset where appropriate.
  - Candle lighting offset configuration.
  - Provides convenience accessors for core zmanim and lists: allZmanim, allShaosZmaniyos.

- io.github.kdroidfilter.kosherkotlin.Zman
  - Sealed class modeling either a DateBased moment (Instant?) or a ValueBased duration (Duration).
  - Carries a ZmanDefinition metadata that details the type and calculation method.
  - Comparable within their subtype and includes a simple .formatted(TimeZone) helper.

- io.github.kdroidfilter.kosherkotlin.ZmanDescriptionFormatter
  - Produces short and long human-readable descriptions from a Zman or ZmanDefinition.
  - Indicates main opinion/calculation used and optionally whether elevation affects the result.

Also relevant:
- io.github.kdroidfilter.kosherkotlin.ComplexZmanimCalendar — an extension with many additional opinions and degree/offset variants.
- io.github.kdroidfilter.kosherkotlin.metadata.* — metadata types attached to Zman: ZmanType, ZmanDefinition, ZmanCalculationMethod, ZmanAuthority, UsesElevation.


## Concepts in a nutshell

- Date-based vs value-based
  - DateBased: things that happen at a moment (e.g., sunrise, sof zman shma). Nullable when not applicable or not computable.
  - ValueBased: durations like shaah zmanis.
- Elevation usage
  - ZmanimCalendar.isUseElevation controls whether to use elevation beyond sunrise/sunset for eligible zmanim. Default is false. See code comments for halachic discussion and when it applies.
- Candle lighting offset
  - ZmanimCalendar.candleLightingOffset defaults to 18 minutes before sea-level sunset. Customizable (e.g., 40 for Jerusalem).
- Metadata for transparency
  - Every Zman has a ZmanDefinition describing the method and authorities. ZmanDescriptionFormatter renders these as clear strings.


## Quick start

- Build a GeoLocation and ZmanimCalendar and query core zmanim:

```kotlin
val tz = kotlinx.datetime.TimeZone.of("America/New_York")
val location = io.github.kdroidfilter.kosherkotlin.util.GeoLocation(
    name = "New York, NY",
    latitude = 40.7128,
    longitude = -74.0060,
    elevation = 10.0,
    timeZone = tz
)

val zc = io.github.kdroidfilter.kosherkotlin.ZmanimCalendar(location)

// Set the date (time component can be any value)
val today = kotlinx.datetime.Clock.System.todayIn(tz)
zc.localDateTime = kotlinx.datetime.LocalDateTime(today, kotlinx.datetime.LocalTime(12, 0))

val sunrise = zc.sunrise.momentOfOccurrence
val sunset  = zc.sunset.momentOfOccurrence

println("Sunrise: ${sunrise?.toLocalDateTime(tz)?.time}")
println("Sunset:  ${sunset?.toLocalDateTime(tz)?.time}")
```

- Get a list of all standard zmanim and format them:

```kotlin
val formatter = io.github.kdroidfilter.kosherkotlin.ZmanDescriptionFormatter()
for (z in zc.allZmanim) {
    val label = formatter.formatShortDescription(z, includeElevationDescription = false)
    println("$label -> ${z.formatted(tz)}")
}
```

- Toggle elevation usage and adjust candle-lighting offset:

```kotlin
zc.isUseElevation = true
zc.candleLightingOffset = 18.0 // minutes before sea-level sunset
```

- Need more opinions and variants? Consider ComplexZmanimCalendar:

```kotlin
val czc = io.github.kdroidfilter.kosherkotlin.ComplexZmanimCalendar(location)
czc.localDateTime = kotlinx.datetime.LocalDateTime(today, kotlinx.datetime.LocalTime(12, 0))
val allExtended = czc.allZmanim // plus many more
```


## API highlights

ZmanimCalendar (selected properties returning Zman):
- alosHashachar, alos72
- sunrise (DateBased)
- chatzos (Sun transit)
- sofZmanShmaGRA, sofZmanShmaMGA
- sofZmanTfilaGRA, sofZmanTfilaMGA
- minchaGedola, minchaKetana, plagHamincha
- candleLighting (DateBased)
- tzais, tzais72
- shaahZmanisGra (ValueBased), shaahZmanisMGA (ValueBased)
- allZmanim: List<Zman.DateBased>
- allShaosZmaniyos: List<Zman.ValueBased>

Important helpers:
- getSofZmanShma(startOfDay: Instant?, endOfDay: Instant?): Instant?
- getSofZmanTfila(startOfDay: Instant?, endOfDay: Instant?): Instant?
- getMinchaGedola/MinchaKetana/PlagHamincha(startOfDay, endOfDay)
- getShaahZmanisBasedZman(startOfDay, endOfDay, hours)
- isAssurBemlacha(currentTime, tzais, inIsrael): Boolean — integrates JewishCalendar holiday logic.

Zman:
- DateBased: holds momentOfOccurrence: Instant? and definition: ZmanDefinition
- ValueBased: holds duration: Duration and definition: ZmanDefinition
- formatted(TimeZone): String — quick-and-simple representation

ZmanDescriptionFormatter:
- formatShortDescription(zman: Zman<*>, includeElevationDescription: Boolean): String
- formatLongDescription(zman: Zman<*>): String
- Overloads accept ZmanDefinition as well


## Common recipes

- Show a table-like listing of core zmanim with labels:

```kotlin
val tz = location.timeZone
val f = io.github.kdroidfilter.kosherkotlin.ZmanDescriptionFormatter()
zc.allZmanim.forEach { z ->
    val label = f.formatShortDescription(z, includeElevationDescription = true)
    println("$label => ${z.formatted(tz)}")
}
zc.allShaosZmaniyos.forEach { z ->
    val label = f.formatShortDescription(z, includeElevationDescription = false)
    println("$label => ${z.formatted(tz)}")
}
```

- Compute sof zman shma and tfila using a different day definition:

```kotlin
// Example: using dawn-to-dusk from alos72 to tzais72
val szs = zc.getSofZmanShma(zc.alos72.momentOfOccurrence, zc.tzais72.momentOfOccurrence)
val szt = zc.getSofZmanTfila(zc.alos72.momentOfOccurrence, zc.tzais72.momentOfOccurrence)
println("SZSh: ${szs?.toLocalDateTime(tz)?.time} | SZTf: ${szt?.toLocalDateTime(tz)?.time}")
```

- Check melacha prohibition now (requires tzais for the day):

```kotlin
val now = kotlinx.datetime.Clock.System.now()
val tzais = zc.tzais.momentOfOccurrence ?: return@run
val assur = zc.isAssurBemlacha(now, tzais, inIsrael = true)
println("Assur b'melacha now? $assur")
```

- Sort zmanim by occurrence time, safely handling nulls:

```kotlin
val sorted = zc.allZmanim.sorted() // DateBased compareTo handles nulls (nulls first)
```


## Time zones, platforms, and precision notes

- Always construct your GeoLocation with a real IANA TimeZone. Zman.DateBased.momentOfOccurrence is an Instant you convert via toLocalDateTime(tz) for display.
- Extreme latitudes: certain DateBased values may be null (no sunrise/sunset/offset). Handle N/A gracefully.
- isUseElevation: default false. Set to true only with proper halachic guidance; it affects sunrise/sunset-based zmanim in this class.
- Candle lighting uses sea-level sunset plus the configured offset unless you choose to alter that logic in your app.


## Edge cases and correctness

- Some days/locations yield no sunrise/sunset; expect null instants. ValueBased durations may return Long.MIN_VALUE when underlying sunrise/sunset are missing (handled within the class when computing Duration).
- Comparing Zman across types: DateBased vs ValueBased sorting is only meaningful within the same subtype; mixing is undefined semantically for ordering.
- ZmanDescriptionFormatter focuses on human-friendly text; for strict labeling in your own language, consider mapping ZmanDefinition to your localized strings.


## Performance tips

- Reuse a single ZmanimCalendar instance and update only localDateTime for new dates.
- Reuse ZmanDescriptionFormatter across many calls rather than re-creating it.
- Avoid repeated Instant→LocalDateTime conversions when rendering large lists; cache formatted strings if needed.


## Troubleshooting FAQ

- Why is a zman N/A? It likely did not occur due to latitude/date constraints. Confirm your location, time zone, and that you are accessing the correct property.
- My displayed time looks wrong. Ensure you used the proper TimeZone when converting Instant to local time.
- Which opinions does a result reflect? Inspect z.definition and use ZmanDescriptionFormatter.formatLongDescription(z) for details.
- How do I get more variants (degrees, offsets, Israel-specific norms)? Use ComplexZmanimCalendar, which builds on ZmanimCalendar and adds many opinions.


## Where to go next

- Explore API docs for io.github.kdroidfilter.kosherkotlin.ZmanimCalendar, Zman, and ZmanDescriptionFormatter.
- See also: ComplexZmanimCalendar for extended options.
- Pair with the Hebrew calendar utilities to display daily Jewish info: parsha, omer, yom tov names.
- For the underlying sun computations, read the AstronomicalCalendar guide: zmanim/ASTRONOMICAL_CALENDAR.md
