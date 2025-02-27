import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlinx.serialization)
    id("org.jetbrains.dokka")  version "2.0.0"
    id("com.vanniktech.maven.publish") version "0.30.0"
}

group = "io.github.kdroidfilter.kosherkotlin"
version = "2.5.3"

tasks.withType<DokkaTask>().configureEach {
    moduleName.set("Kosher Kotlin - K-droid Fork")
    offlineMode.set(true)
}

kotlin {
    jvmToolchain(17)

    androidTarget { publishLibraryVariants("release") }
    jvm()
    js { browser() }
    wasmJs { browser() }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    macosArm64()
    linuxX64()
    mingwX64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.kotlinx.serialization.json)
            api(libs.kotlinx.datetime)
            implementation(libs.hebrewnumerals)

        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
        }

        jvmMain.dependencies {
            implementation(libs.kotlinx.coroutines.swing)
        }
        jvmTest.dependencies {
            implementation(libs.zmanim)
        }
        jsMain.dependencies {
            api(npm("@js-joda/timezone", "2.3.0"))
        }
        wasmJsMain.dependencies {
            api(npm("@js-joda/timezone", "2.3.0"))
        }

    }

    //https://kotlinlang.org/docs/native-objc-interop.html#export-of-kdoc-comments-to-generated-objective-c-headers
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        compilations["main"].compileTaskProvider.configure {
            compilerOptions {
                freeCompilerArgs.add("-Xexport-kdoc")
            }
        }
    }

}

android {
    namespace = "io.github.kdroidfilter.kosherkotlin"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
    }
}

mavenPublishing {
    coordinates(
        groupId = "io.github.kdroidfilter",
        artifactId = "kosherkotlin",
        version = version.toString()
    )

    // Configure POM metadata for the published artifact
    pom {
        name.set("Kosher Kotlin")
        description.set("KosherJava Zmanim API / Library port to Kotlin. KosherJava is a library for calculating astronomical and religious dates and times based on location.")
        inceptionYear.set("2025")
        url.set("https://github.com/kdroidFilter/KosherKotlin/")

        licenses {
            license {
                name.set("LGPL-2.1")
                url.set("https://github.com/kdroidFilter/KosherKotlin/blob/master/LICENSE")
            }
        }

        // Specify developers information
        developers {
            developer {
                id.set("kdroidfilter")
                name.set("Elyahou Hadass")
                email.set("elyahou.hadass@gmail.com")
            }
        }

        // Specify SCM information
        scm {
            connection.set("scm:git:https://github.com/kdroidFilter/KosherKotlin.git")
            developerConnection.set("scm:git:https://github.com/kdroidFilter/KosherKotlin.git")
            url.set("https://github.com/kdroidFilter/KosherKotlin")
         }
    }

    // Configure publishing to Maven Central
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)


    // Enable GPG signing for all publications
    signAllPublications()
}
