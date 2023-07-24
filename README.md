KosherKotlin Zmanim API
=====================

The Zmanim library is an API for a specialized calendar that can calculate different astronomical
times including sunrise and sunset and Jewish _zmanim_ or religious times for prayers and other
Jewish religious duties.

It is based on Eliyahu Hershfeld's [KosherJava](https://github.com/KosherJava/zmanim).

These classes extend GregorianCalendar and can therefore
use the standard Calendar functionality to change dates etc. For non religious astronomical / solar
calculations use the [AstronomicalCalendar](./src/main/java/com/kosherjava/zmanim/AstronomicalCalendar.kt).

The ZmanimCalendar contains the most common zmanim or religious time calculations. For a much more
extensive list of _zmanim_ use the ComplexZmanimCalendar.
This class contains the main functionality of the Zmanim library.

For a basic set of instructions on the use of the API, see [How to Use the Zmanim API](https://kosherjava.com/zmanim-project/how-to-use-the-zmanim-api/), [zmanim code samples](https://kosherjava.com/tag/code-sample/) and the [KosherJava FAQ](https://kosherjava.com/tag/faq/). See the <a href="https://kosherjava.com">KosherJava Zmanim site</a> for additional information.

# Get Started
To add KosherKotlin as a dependency to your project, add the following dependency:

## Dependency

In newer Gradle versions, insert this into `settings.gradle`:

```
dependencyResolutionManagement {
    ...
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
And then download the library as usual:

```
dependencies {
    implementation 'com.github.Sternbach-Software:KosherKotlin:-SNAPSHOT'
}
```
## Usage

The library was designed to be as idiomatic as possible, both in its implementation and its API surface. Every client-facing zero-argument function is accessible as a computed property (e.g. `calendar.alos72`).

# Future
 - [ ] There are plans to port the library to `kotlinx-datetime` so that it is multi-platform friendly. This is a major overhaul - PRs are welcomed.
 - [ ] There are still a significant amount of javadocs/kdocs that were written for specific getters, setters, or properties, which were combined into a public property, and all of the javadocs/kdocs were copied to the property. They must be combined. There is often overlap or complete duplication between doc strings, but sometimes there are notes only relevant to the setter or getter.
 - [ ] Unit tests need to be ported from upstream. They are (slowly) being translated into Java from the Python port. 

License
-------
The library is released under the [LGPL 2.1 license](https://kosherjava.com/2011/05/09/kosherjava-zmanim-api-released-under-the-lgpl-license/).

Disclaimer:
-----------
__While I did my best to get accurate results, please double check before relying on these zmanim for <em>halacha lemaaseh</em>__.
