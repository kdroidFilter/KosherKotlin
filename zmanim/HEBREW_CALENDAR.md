# Hebrew Calendar Module (hebrewcalendar)

A practical, user-friendly guide to the Hebrew calendar utilities bundled with the KosherKotlin Zmanim library. This module is multiplatform (Kotlin/JVM, Android, iOS, JS, WASM, etc.) and lets you work with Hebrew dates, convert to/from Gregorian, compute Yom Tov, parsha, Omer, Daf Yomi, and apply a variety of prayer (tefila) rules.

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

The package io.github.kdroidfilter.kosherkotlin.hebrewcalendar includes:

- JewishDate: Low-level representation of a Hebrew date tied to a Gregorian date; supports conversions and calendar arithmetic.
- HebrewLocalDate: Lightweight immutable Hebrew date type, similar in spirit to LocalDate, with plusDays and conversion helpers.
- HebrewMonth: Enum of Hebrew months (including Adar/Adar II in leap years) with helper methods.
- JewishCalendar: High-level calendar that extends JewishDate and adds yom tov, parsha, omer, rosh chodesh, and many more calendar rules. Also supports “in Israel” toggles and modern holidays hooks.
- HebrewDateFormatter: Human-friendly formatting of Hebrew dates, months, parsha, Yom Tov names, Omer, and molad.
- TefilaRules: Utility methods to determine if certain tefila insertions (e.g., Vesein Tal Umatar, Mashiv Haruach, Hallel, Al Hanissim) apply on a given day.
- YomiCalculator and YerushalmiYomiCalculator: Calculate the daily Daf Yomi (Bavli or Yerushalmi) for a given date.
- Daf: Simple value class holding the tractate number and page (daf) index for Daf Yomi.


## Concepts in a nutshell

- Hebrew leap years: 7 of every 19 years are leap; leap years have Adar I and Adar II (Adar II is the halachic Adar for many rules).
- New year counting: Civil year begins Tishrei, religious counting often starts Nissan; some methods let you choose the basis when relevant.
- Dechiyos (postponements) and molad (mean lunar conjunction) are used under the hood for Hebrew date computation—exposed via JewishDate for advanced needs.
- Israel vs Chutz La’aretz: Many Yom Tov and parsha rules differ; pass inIsrael = true when appropriate.


## Quick start

Basic examples are multiplatform and use kotlinx.datetime.

- Hebrew date for today, print formatted date and parsha:

```kotlin
val today = kotlinx.datetime.LocalDate(2025, 8, 18)
val jc = io.github.kdroidfilter.kosherkotlin.hebrewcalendar.JewishCalendar(today, isInIsrael = true)
val formatter = io.github.kdroidfilter.kosherkotlin.hebrewcalendar.HebrewDateFormatter()

println(formatter.format(jc))       // Hebrew date
println(formatter.formatParsha(jc)) // Weekly parsha if applicable
println(formatter.formatYomTov(jc)) // Yom Tov name if applicable
```

- Convert between Gregorian and Hebrew:

```kotlin
// Gregorian -> Hebrew
val greg = LocalDate(2025, 5, 7)
val hLocal: HebrewLocalDate = HebrewLocalDate(5785, HebrewMonth.IYAR, 9) // example Hebrew date

val jDateFromGreg = JewishDate(greg)           // computes Hebrew
val jDateFromHeb = JewishDate(hLocal)          // or start from Hebrew

println(jDateFromGreg.hebrewLocalDate)         // HebrewLocalDate(year, month, day)
println(jDateFromHeb.gregorianLocalDate)       // LocalDate
```

- Compute Omer and Rosh Chodesh text:

```kotlin
val omer = formatter.formatOmer(jc)            // "Day X of the Omer" during Sefira, else empty
val roshChodesh = formatter.formatRoshChodesh(jc) // e.g., "Rosh Chodesh Kislev"
```

- Daf Yomi (Bavli and Yerushalmi):

```kotlin
val dafBavli: Daf? = YomiCalculator.getDafYomiBavli(jc)
val dafYerushalmi: Daf? = YerushalmiYomiCalculator.getDafYomiYerushalmi(jc) // null on Yom Kippur or Tisha B’Av
```


## The main types and how to use them

### HebrewMonth

Enum values: NISSAN, IYAR, SIVAN, TAMMUZ, AV, ELUL, TISHREI, CHESHVAN, KISLEV, TEVES, SHEVAT, ADAR, ADAR_II.

Key helpers:
- isLastMonthInYear(jewishYear, tishreiBased = true)
- isFirstMonthInYear(tishreiBased = true)
- getNextMonthInYear(jewishYear, tishreiBased = true)
- getPreviousMonthInYear(tishreiBased = true)
- getNumDaysInMonthForYear(jewishYear)
- getTishreiBasedValue(...) and conversions between Nissan-based and Tishrei-based numbering

When in a leap year, ADAR_II is the halachic “Adar.” In non-leap years, ADAR fulfills that role.


### HebrewLocalDate

- Immutable Hebrew date holding: year: Long, month: HebrewMonth, dayOfMonth: Int.
- toLocalDateGregorian(): Convert to Gregorian LocalDate.
- plusDays(days: Long): Hebrew date arithmetic across months/years.
- toPairOfHebrewAndGregorianLocalDate(...): Efficient conversion utilities that return both dates.

Use HebrewLocalDate for simple immutable computations and to avoid mutability of JewishDate.


### JewishDate

A mutable, feature-rich date class that understands both Gregorian and Hebrew dates. Key features:

- Multiple constructors: from molad (advanced), from Hebrew year/month/day, from LocalDate (Gregorian), from HebrewLocalDate, or “now.”
- Conversion helpers:
  - setGregorianDate(year, month, dayOfMonth): Set Gregorian and compute Hebrew.
  - setJewishDate(...): Set Hebrew and compute Gregorian; variants include setting molad time (hours, minutes, chalakim).
  - gregorianDateToAbsDate(...) and absDateToDate(...): Absolute day conversions.
- Calendar computations:
  - getDaysInJewishMonth(month, year)
  - isJewishLeapYear(year) [via companion]
  - getLastMonthOfJewishYear(year)
  - getJewishCalendarElapsedDays(year), moladToAbsDate(chalakim)
  - getDaysSinceStartOfJewishYear(year, month, day)
- Arithmetic: forward(field, amount), forwardJewishMonth(amount), back() for stepping days/months.

Prefer JewishDate when you need fine control, advanced calculations, or mutation-based workflows.


### JewishCalendar

Extends JewishDate and adds liturgical and communal calendar logic.

Construction:
- JewishCalendar(LocalDate) or JewishCalendar(HebrewLocalDate)
- Flags:
  - isInIsrael: toggles Israel vs Diaspora rules
  - shouldUseModernHolidays: enables modern Israeli holidays handling (when applicable in your version)

Capabilities include:
- Yom Tov detection: Use properties such as yomTovIndex and helpers exposed via HebrewDateFormatter to produce display strings.
- Rosh Chodesh logic and month lengths.
- Omer counting.
- Weekly parsha selection (single/double parshiyos, seasonal variations) via Parsha enum.
- Day-of-week handling and toJewishDayOfWeek().
- Copy methods to clone with tweaks.

Parsha enum includes all standard weekly portions and compound parshiyos (e.g., VAYAKHEL_PEKUDEI, TAZRIA_METZORA) plus special Shabbos designations (e.g., SHKALIM, ZACHOR, PARA, HACHODESH) and seasonal special names (e.g., SHIRA, CHAZON, NACHAMU, HAGADOL). A value of NONE indicates no standard weekly parsha (e.g., Yom Tov Shabbos).


### HebrewDateFormatter

Human-friendly string output. Notable methods:
- format(jewishDate): full Hebrew date string.
- formatMonth(jewishDate): month name.
- formatDayOfWeek(jewishDate)
- formatParsha(jewishCalendar): weekly parsha name or null if none.
- formatSpecialParsha(jewishCalendar): returns a special parsha name if relevant.
- formatYomTov(jewishCalendar): a Yom Tov label or empty if none.
- formatRoshChodesh(jewishCalendar)
- formatOmer(jewishCalendar)
- getFormattedKviah(jewishYear): kviah code of the year.
- formatMolad(moladChalakim): molad textual rendering.
- formatDafYomiBavli(daf), formatDafYomiYerushalmi(daf)

Tip: You can integrate with your own localization by mapping or wrapping these outputs. The formatter uses Hebrew numerals by default and can leverage a gematria converter.


### TefilaRules

Answers common liturgical questions for the date provided by a JewishCalendar:

- isTachanunRecitedShacharis(...), isTachanunRecitedMincha(...)
- isHallelRecited(...), isHallelShalemRecited(...)
- isAlHanissimRecited(...), isYaalehVeyavoRecited(...)
- isVeseinTalUmatarStartDate(...), isVeseinTalUmatarStartingTonight(...), isVeseinTalUmatarRecited(...)
- isVeseinBerachaRecited(...)
- isMashivHaruachStartDate(...), isMashivHaruachEndDate(...), isMashivHaruachRecited(...)

Edge notes:
- Some practices vary by community; this module encodes commonly used rules. For application-facing UX, consider explaining local customizations.


### YomiCalculator and YerushalmiYomiCalculator

- YomiCalculator.getDafYomiBavli(jewishCalendar): Returns the Daf for Bavli. Throws if date precedes Sep 11, 1923 (first cycle). Handles Shekalim pagination change in 1975.
- YerushalmiYomiCalculator.getDafYomiYerushalmi(jewishCalendar): Returns the Daf for Yerushalmi or null on Tisha B’Av and Yom Kippur. Throws if date precedes Feb 2, 1980 (first cycle). Accounts for cycle length and non-learning days.

Returned value is a Daf(masechtaNumber, daf) where masechtaNumber indexes a standard sequence (see source for mapping). Use HebrewDateFormatter helpers to format for users.


## Common recipes

- Get next upcoming Yom Tov from a starting date:

```kotlin
fun nextYomTov(start: LocalDate, tz: TimeZone, inIsrael: Boolean): Pair<LocalDate, String>? {
    val formatter = HebrewDateFormatter()
    var jc = JewishCalendar(start, inIsrael)
    repeat(370) { // search up to ~1 year ahead
        val label = formatter.formatYomTov(jc)
        if (label.isNotEmpty()) return jc.gregorianLocalDate to label
        jc = JewishCalendar(jc.hebrewLocalDate.plusDays(1), inIsrael)
    }
    return null
}
```

- Count days until next Rosh Chodesh:

```kotlin
fun daysUntilRoshChodesh(from: LocalDate, inIsrael: Boolean): Int {
    val formatter = HebrewDateFormatter()
    var jc = JewishCalendar(from, inIsrael)
    var days = 0
    while (days < 60) {
        val rc = formatter.formatRoshChodesh(jc)
        if (rc.isNotEmpty()) return days
        days++
        jc = JewishCalendar(jc.hebrewLocalDate.plusDays(1), inIsrael)
    }
    return -1 // not found within 60 days
}
```

- Show tefila inserts for today:

```kotlin
fun tefilaInsertsFor(jc: JewishCalendar): List<String> {
    val rules = TefilaRules()
    val items = mutableListOf<String>()
    if (rules.isMashivHaruachRecited(jc)) items += "Mashiv Haruach"
    if (rules.isMoridHatalRecited(jc)) items += "Morid Hatal"
    if (rules.isVeseinTalUmatarRecited(jc)) items += "Vesein Tal Umatar"
    if (rules.isVeseinBerachaRecited(jc)) items += "Vesein Beracha"
    if (rules.isHallelRecited(jc)) items += if (rules.isHallelShalemRecited(jc)) "Hallel Shalem" else "Hallel"
    if (rules.isAlHanissimRecited(jc)) items += "Al Hanissim"
    if (rules.isYaalehVeyavoRecited(jc)) items += "Yaaleh Veyavo"
    if (items.isEmpty()) items += "No special inserts today"
    return items
}
```


## Time zones, locales, and platforms

- Always provide a real IANA time zone (e.g., "America/New_York") when constructing dates used with astronomical zmanim. For purely calendar logic, the date itself is often sufficient, but consistency with your app’s tz is best.
- On JS/WASM targets, ensure time zone data is bundled (see project README for @js-joda/timezone note). hebrewcalendar itself mostly relies on dates, but your app likely mixes with zmanim.
- HebrewDateFormatter emits Hebrew text by default. If you need English transliterations or localized names, map or wrap outputs in your own layer.


## Edge cases and correctness notes

- Daf Yomi start dates are enforced; requesting a Daf before the first cycle will throw an exception.
- Yerushalmi Daf Yomi returns null on Yom Kippur and Tisha B’Av.
- Extreme historical dates: the module supports a very wide range but be mindful of proleptic calendars and halachic boundaries in your domain.
- Leap years and Adar handling: In leap years, Adar I and Adar II exist; in non-leap years, there is only Adar. Many rules consider Adar II as “Adar.”
- Israel vs Diaspora: For Yom Tov duration and certain parsha combinations, always pass the correct inIsrael flag when building JewishCalendar.


## Performance tips

- Prefer HebrewLocalDate for immutable computations and lightweight conversions.
- Reuse HebrewDateFormatter where possible instead of re-creating for every call.
- When scanning days, construct subsequent JewishCalendar from the prior hebrewLocalDate.plusDays(1) to avoid repeated Gregorian↔Hebrew recomputation overhead.


## Troubleshooting FAQ

- Why is formatParsha returning null? The given date may be a Yom Tov Shabbos or a weekday—no weekly parsha applies. Try formatSpecialParsha for special weeks.
- Why is YomiCalculator throwing? Ensure the date is on or after Sep 11, 1923. For Yerushalmi, ensure on/after Feb 2, 1980.
- Why am I getting an empty string from formatOmer? The date is outside Sefiras Ha’omer.
- How do I get English month names? HebrewDateFormatter outputs Hebrew text by default. Map to your own English names or add a thin adapter layer.
- My location uses different minhagim. Can I override TefilaRules? The built-in rules encode widely used practices; if your community differs, consider maintaining a small override table keyed by Hebrew dates.


## References and attribution

- Based on and adapted from the KosherJava Zmanim API by Eliyahu Hershfeld.
- Ported and refactored for Kotlin Multiplatform; some components inspired by community projects such as libzmanim.
- See source code for exact algorithms: molad and dechiyos (JewishDate), parsha logic and yom tov indices (JewishCalendar), and daf calculations with historical cycle adjustments.


## Where to go next

- Explore the API docs for io.github.kdroidfilter.kosherkotlin.hebrewcalendar.*
- Review sample code in the repository’s README and sample/ compose app.
- Combine hebrewcalendar with ZmanimCalendar or ComplexZmanimCalendar to present complete daily Jewish information in your app.
