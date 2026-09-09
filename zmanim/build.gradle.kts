plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.dokka)
    alias(libs.plugins.vanniktech.maven.publish)
}

group = "io.github.kdroidfilter.kosherkotlin"
val ref = System.getenv("GITHUB_REF") ?: ""
val version = if (ref.startsWith("refs/tags/")) {
    val tag = ref.removePrefix("refs/tags/")
    if (tag.startsWith("v")) tag.substring(1) else tag
} else "dev"

dokka {
    moduleName.set("Kosher Kotlin - K-droid Fork")
    dokkaPublications.html {
        offlineMode.set(true)
    }
}

kotlin {
    jvmToolchain(17)

    android {
        namespace = "io.github.kdroidfilter.kosherkotlin"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    jvm()
    js { browser() }
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs { browser() }
    iosArm64()
    iosSimulatorArm64()
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
            api(npm("@js-joda/timezone", libs.versions.js.joda.timezone.get()))
        }
        wasmJsMain.dependencies {
            api(npm("@js-joda/timezone", libs.versions.js.joda.timezone.get()))
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

mavenPublishing {
    coordinates(
        groupId = "io.github.kdroidfilter",
        artifactId = "kosherkotlin",
        version = version.toString()
    )

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

        developers {
            developer {
                id.set("kdroidfilter")
                name.set("Elyahou Hadass")
                email.set("elyahou.hadass@gmail.com")
            }
        }

        scm {
            connection.set("scm:git:https://github.com/kdroidFilter/KosherKotlin.git")
            developerConnection.set("scm:git:https://github.com/kdroidFilter/KosherKotlin.git")
            url.set("https://github.com/kdroidFilter/KosherKotlin")
        }
    }

    publishToMavenCentral()
    signAllPublications()
}
