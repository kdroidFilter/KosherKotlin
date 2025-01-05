# KosherKotlin Zmanim API ✨

KosherKotlin Zmanim is a Kotlin Multiplatform library for calculating various astronomical and Jewish religious times (_zmanim_), such as sunrise, sunset, and prayer times. This library is a fork of [Sternbach-Software/KosherKotlin](https://github.com/Sternbach-Software/KosherKotlin), which itself is a fork of Eliyahu Hershfeld's [KosherJava Zmanim API](https://github.com/KosherJava/zmanim). It is tailored to work on modern platforms and adapted to meet evolving needs.

## Status 🚀
This fork is actively maintained and has already undergone significant updates, including:

- Published to Maven for easier integration.
- Enhanced compatibility with Kotlin/JS and Kotlin/WASM.
- Reorganized code to ensure it functions as a fully Kotlin Multiplatform (KMP) library.
- Initial steps toward refactoring the code for improved readability and maintainability.
- Efforts underway to refactor and streamline documentation.

The library can be imported into your project using:
```kotlin
implementation("io.github.kdroidfilter:kosherkotlin:2.5.2")
```
It is compatible with all platforms supported by Kotlin Multiplatform.

Generated documentation is available at: [KosherKotlin Documentation](https://kdroidfilter.github.io/KosherKotlin/) 📄

An example of the library in use with Kotlin/WASM and Compose can be found here: [WASM Sample](https://kdroidfilter.github.io/KosherKotlin/sample/) 🚀

While most features are functional, **not all tests pass at the moment**. Please report any issues or contribute fixes where possible.

## Philosophy 🌍
Unlike the official KosherJava Zmanim library, this fork may not always stay synchronized with upstream updates. While I aim to track significant new features and improvements, this fork will be adapted to suit specific requirements and priorities.

## Usage ⚖️
The API retains much of the idiomatic design found in KosherJava while introducing modern Kotlin features. Examples of usage are provided below:

```kotlin
val location = GeoLocation("New York", 40.7, -74.0, 0.0, TimeZone.of("America/New_York"))
val calendar = ComplexZmanimCalendar(location)

println(calendar.allZmanim.filter { it.definition.type == ZmanType.ALOS }.map { it.value })
println(calendar.sunrise)
println(calendar.alos72.momentOfOccurrence)
println(calendar.shaahZmanis72Minutes.duration)
```

## Roadmap 🔄
Key goals for this fork include:

1. **Documentation Overhaul**
    - Consolidate and simplify documentation.
    - Update examples to reflect modern Kotlin idioms.

2. **Testing and Stability**
    - Port and adapt upstream unit tests.
    - Ensure stability across supported platforms.

3. **Feature Enhancements**
    - Integrate with `kotlinx-datetime` for improved multiplatform support.
    - Explore publishing improvements (e.g., Maven Central).

4. **Refactoring**
    - Streamline and modernize the codebase.
    - Address legacy Java documentation inherited from the upstream project.

## Contributing 📚
Contributions are welcome! Whether through code, documentation, or testing, your help will make this library more robust and versatile. Open a PR or reach out with your ideas.

## License & Attribution ✉️
This fork is released under the [LGPL 2.1 license](https://kosherjava.com/2011/05/09/kosherjava-zmanim-api-released-under-the-lgpl-license/).

## Disclaimer ⚠️
While I strive for accuracy, please double-check zmanim for _halachic_ decisions. This library is provided "as-is" without warranty of correctness for religious purposes.

