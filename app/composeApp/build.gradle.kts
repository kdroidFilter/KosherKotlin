import dev.nucleusframework.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.metro)
    alias(libs.plugins.nucleus)
}

kotlin {
    jvmToolchain(17)

    android {
        namespace = "app.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.appMinSdk.get().toInt()
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
        androidResources {
            enable = true
        }
    }
    jvm()
    js {
        outputModuleName.set("composeApp")
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName.set("composeApp")
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        // Every Skiko backend (desktop, web, iOS) ships Compose scrollbars; Android does not.
        val skikoMain by creating { dependsOn(commonMain.get()) }
        listOf(jvmMain, jsMain, wasmJsMain).forEach { it.get().dependsOn(skikoMain) }

        // Both iOS targets share one source set: CoreMotion is written once, not twice.
        val iosMain = maybeCreate("iosMain").apply { dependsOn(skikoMain) }
        listOf(iosArm64Main, iosSimulatorArm64Main).forEach { it.get().dependsOn(iosMain) }

        // Everything that has no motion sensor. iOS is a Skiko target too, but it does have
        // one, so this cannot simply hang off skikoMain.
        val sensorlessMain by creating { dependsOn(commonMain.get()) }
        listOf(jvmMain, jsMain, wasmJsMain).forEach { it.get().dependsOn(sensorlessMain) }

        // Browser history integration is the only web-only code; both web targets share it.
        val webMain = maybeCreate("webMain").apply { dependsOn(commonMain.get()) }
        listOf(jsMain, wasmJsMain).forEach { it.get().dependsOn(webMain) }
        webMain.dependencies { implementation(libs.nav3.browser) }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.compose.material3)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.lifecycle.runtime.compose)
            implementation(libs.nav3.runtime)
            implementation(libs.nav3.ui)
            implementation(libs.multiplatform.settings)
            implementation(project(":zmanim"))
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.multiplatform.settings.test)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            // Nucleus picks its backend off the classpath; tao is the only one shipped here.
            implementation(libs.nucleus.application)
            implementation(libs.nucleus.decorated.window.tao)
            implementation(libs.nucleus.decorated.window.material3)
        }
    }
}

composeCompiler {
    // Types we can truthfully promise are immutable but cannot annotate ourselves.
    stabilityConfigurationFiles.add(
        rootProject.layout.projectDirectory.file("compose_stability.conf")
    )
}

// Stability analyzer: ./gradlew :app:composeApp:compileKotlinJvm -PcomposeReports=true
// Reports land in app/composeApp/build/compose_compiler/ (classes.txt, composables.txt, module.json).
if (providers.gradleProperty("composeReports").orNull == "true") {
    composeCompiler {
        reportsDestination = layout.buildDirectory.dir("compose_compiler")
        metricsDestination = layout.buildDirectory.dir("compose_compiler")
    }
}

// Nucleus packages the desktop app: compose.desktop.application must stay unconfigured,
// the plugin fails the build if both are set.
nucleus.application {
    mainClass = "app.MainKt"

    nativeDistributions {
        targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
        packageName = "Luach"
        packageVersion = "1.0.0"
    }
}
