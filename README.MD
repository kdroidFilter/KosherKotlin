KosherKotlin Zmanim API
=====================

The KosherKotlin Zmanim library is a Kotlin Multiplatform API for a specialized calendar that can calculate different astronomical
times including sunrise and sunset (default uses NOAA's algorithm) and Jewish _zmanim_ or religious times for prayers and other
Jewish religious duties.

It is based on Eliyahu Hershfeld's [KosherJava](https://github.com/KosherJava/zmanim).

The `ZmanimCalendar` class contains the most common zmanim or religious time calculations. 
For a much more extensive list of _zmanim_ use the `ComplexZmanimCalendar`.
This class contains the main functionality of the Zmanim library.

For a basic set of instructions on the use of the API, see [How to Use the Zmanim API](https://kosherjava.com/zmanim-project/how-to-use-the-zmanim-api/), [zmanim code samples](https://kosherjava.com/tag/code-sample/) and the [KosherJava FAQ](https://kosherjava.com/tag/faq/). See the <a href="https://kosherjava.com">KosherJava Zmanim site</a> for additional information.

## Usage

The library was designed to be as idiomatic as possible, both in its implementation and its API surface. Every client-facing zero-argument function is accessible as a computed property (e.g. `ComplexZmanimCalender().alos72`).

Every zman has a lazily computed generic property called `value`.

`Zman.DateBased` objects have a `momentOfOccurrence` property which lazily computes the `Instant` of the zman's occurrence. 

`Zman.ValueBased` objects have a `duration` property which lazily computes the `Duration` of how long the zman lasts.

A lazy list of all zmanim can be accessed using the `allZmanim` property of `ComplexZmanimCalendar` or `ZmanimCalendar`.

Every `Zman` object has a `definition` property which contains the metadata of the rules used to calculate the zman.

```kotlin
val location = GeoLocation("New York", 40.7, -74.0, 0.0, TimeZone.of("America/New_York"))
val calendar = ComplexZmanimCalendar(location)
println(calendar.allZmanim.filter { it.definition.type == ZmanType.ALOS }.map { it.value })
println(calendar.sunrise)
println(calendar.alos72.momentOfOccurrence)
println(calendar.shaahZmanis72Minutes.duration)
```


Hosted documentation coming soon!

# Future
- [x] ~~There are plans to port the library to `kotlinx-datetime` so that it is multi-platform friendly. This is a major overhaul - PRs are welcomed.~~
    - [ ] ZmanimFormatter needs to be re-written once kotlinx-datetime implements formatting
- [ ] Library distribution needs to be improved (e.g. publishing to mavenCentral, etc.), especially in light of the kotlinx-datetime port (not sure it builds properly)
    - [ ] May need to implement multi-project gradle - not sure how to do that yet.
- [ ] There are still a significant amount of javadocs/kdocs that were written for specific Java getters, setters, or properties, which were combined into a public property, and all of the javadocs/kdocs were copied to the property. They must be combined. There is often overlap or complete duplication between doc strings, but sometimes there are notes only relevant to the setter or getter.
- [ ] Unit tests need to be ported from upstream. They are (slowly) being translated into Java from the Python port.

# License & Attribution
The library is released under the [LGPL 2.1 license](https://kosherjava.com/2011/05/09/kosherjava-zmanim-api-released-under-the-lgpl-license/).
If you made a publicly-available program that uses this library, I want to know! Just send me a heads-up email at "software at sternbach.org".

# Disclaimer
While I did my best to get accurate results, please double check before relying on these zmanim for *halacha lemaaseh*.
