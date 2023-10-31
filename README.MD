KosherKotlin Zmanim API
=====================

The Zmanim library is an API for a specialized calendar that can calculate different astronomical
times including sunrise and sunset (default uses NOAA's algorithm) and Jewish _zmanim_ or religious times for prayers and other
Jewish religious duties.

It is based on Eliyahu Hershfeld's [KosherJava](https://github.com/KosherJava/zmanim).

These classes extend GregorianCalendar and can therefore
use the standard Calendar functionality to change dates etc. For non religious astronomical / solar
calculations use the [AstronomicalCalendar](./composeApp/src/commonMain/sternbach/software/kosherkotlin/AstronomicalCalendar.kt).

The ZmanimCalendar contains the most common zmanim or religious time calculations. For a much more
extensive list of _zmanim_ use the ComplexZmanimCalendar.
This class contains the main functionality of the Zmanim library.

For a basic set of instructions on the use of the API, see [How to Use the Zmanim API](https://kosherjava.com/zmanim-project/how-to-use-the-zmanim-api/), [zmanim code samples](https://kosherjava.com/tag/code-sample/) and the [KosherJava FAQ](https://kosherjava.com/tag/faq/). See the <a href="https://kosherjava.com">KosherJava Zmanim site</a> for additional information.

# Get Started

## Dependency

This library uses Kotlinx-DateTime and is KMP-friendly. There are plans to add it to mavenCentral in the near future. Until then, you can copy the `kosherkotlin` folder into your project.

If you only need Kotlin/JVM support, you can checkout [the commit before the transition](https://github.com/Sternbach-Software/KosherKotlin/commit/177ab9314e468f19fcd04ec47eb22d12eccf2210), when the library only supported Kotlin/JVM and was tested and stable, and copy the files in `zmanim`.

## Building from source

This library is currently a Compose Multiplatform app with a package for the library and a package for the app. The app is a demo of the library, and is not required to use the library.

### Before running!
- check your system with [KDoctor](https://github.com/Kotlin/kdoctor)
- install JDK 17 on your machine
- add `local.properties` file to the project root and set a path to Android SDK there

#### Android
To run the application on android device/emulator:
- open project in Android Studio and run imported android run configuration

To build the application bundle:
- run `./gradlew :composeApp:assembleDebug`
- find `.apk` file in `composeApp/build/outputs/apk/debug/composeApp-debug.apk`

#### Desktop
Run the desktop application: `./gradlew :composeApp:run`

#### iOS
To run the application on iPhone device/simulator:
- Open `iosApp/iosApp.xcproject` in Xcode and run standard configuration
- Or use [Kotlin Multiplatform Mobile plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile) for Android Studio

#### Browser
Run the browser application: `./gradlew :composeApp:jsBrowserDevelopmentRun`

## Usage

The library was designed to be as idiomatic as possible, both in its implementation and its API surface. Every client-facing zero-argument function is accessible as a computed property (e.g. `ComplexZmanimCalender().alos72`).
Every zman has a lazily computed generic property called `value`. `Zman.DateBased` objects have a `momentOfOccurrence` property which lazily computes the `Instant` of the zman's occurrence. `Zman.ValueBased` objects have a `duration` property which lazily computes the `Duration` of how long the zman lasts.

Hosted documentation coming soon!

# Future
- [x] ~~There are plans to port the library to `kotlinx-datetime` so that it is multi-platform friendly. This is a major overhaul - PRs are welcomed.~~
    - [ ] ZmanimFormatter needs to be re-written once kotlinx-datetime implements formatting
- [ ] Library distribution needs to be improved (e.g. publishing to mavenCentral, etc.), especially in light of the kotlinx-datetime port (not sure it builds properly)
    - [ ] May need to implement multi-project gradle - not sure how to do that yet.
- [ ] There are still a significant amount of javadocs/kdocs that were written for specific Java getters, setters, or properties, which were combined into a public property, and all of the javadocs/kdocs were copied to the property. They must be combined. There is often overlap or complete duplication between doc strings, but sometimes there are notes only relevant to the setter or getter.
- [ ] Unit tests need to be ported from upstream. They are (slowly) being translated into Java from the Python port.

# Projects using this library
- [BeautifulZmanim](https://github.com/Sternbach-Software/BeautifulZmanim): a multi-platform zmanim app for desktop, web, and mobile. Written using Jetpack Compose.

# License & Attribution
The library is released under the [LGPL 2.1 license](https://kosherjava.com/2011/05/09/kosherjava-zmanim-api-released-under-the-lgpl-license/).
If you made a publicly-available program that uses this library, I want to know! Just send me a heads-up email at "software at sternbach.org".

# Disclaimer:
__While I did my best to get accurate results, please double check before relying on these zmanim for *halacha lemaaseh*.
