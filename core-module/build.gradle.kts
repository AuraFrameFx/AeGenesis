plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.dokka)
    alias(libs.plugins.spotless)
    alias(libs.plugins.kover)
    alias(libs.plugins.openapi.generator)
}

// Added to specify Java version for this subproject
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(24))
    }
}

// REMOVED: jvmToolchain(24) - Using system Java via JAVA_HOME
// This eliminates toolchain auto-provisioning errors

android {
    namespace = "dev.aurakai.auraframefx.coremodule"
    compileSdk = 36

    defaultConfig {
        minSdk = 33
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        // NDK configuration for native code (if any)
        ndk {
            abiFilters.addAll(listOf("arm64-v8a", "armeabi-v7a", "x86_64"))
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = false // Compose only - Genesis Protocol
        prefab = false
        prefabPublishing = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
    }

    packaging {
        resources {
            excludes +=
                listOf(
                    "/META-INF/{AL2.0,LGPL2.1}",
                    "/META-INF/DEPENDENCIES",
                    "/META-INF/LICENSE",
                    "/META-INF/LICENSE.txt",
                    "/META-INF/NOTICE",
                    "/META-INF/NOTICE.txt",
                    "META-INF/*.kotlin_module",
                )
        }
        jniLibs {
            useLegacyPackaging = false
            pickFirsts += listOf("**/libc++_shared.so", "**/libjsc.so")
        }
    }
}

dependencies {
    // Core Android bundles
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.androidx.core)
    implementation(libs.bundles.compose)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.bundles.coroutines)
    implementation(libs.bundles.network)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Room Database
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    // Core library desugaring
    coreLibraryDesugaring(libs.coreLibraryDesugaring)

    // Xposed Framework - Complete Integration
    implementation(libs.bundles.xposed)
    ksp(libs.yuki.ksp.xposed)
    implementation(files("${project.rootDir}/Libs/api-82.jar"))
    implementation(files("${project.rootDir}/Libs/api-82-sources.jar"))

    // Utilities
    implementation(libs.bundles.utilities)

    // Testing
    testImplementation(libs.bundles.testing)
    testRuntimeOnly(libs.junit.engine)
    androidTestImplementation(libs.bundles.testing)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)

    // Debug implementations
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

// ===== AUTOMATED QUALITY CHECKS =====
spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**/*.kt")
        ktlint(libs.versions.ktlint.get())
            .editorConfigOverride(
                mapOf(
                    "indent_size" to "4",
                    "android" to "true",
                    "max_line_length" to "120",
                ),
            )
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint(libs.versions.ktlint.get())
        trimTrailingWhitespace()
        endWithNewline()
    }
}

// ===== AUTOMATED TESTING =====
kover {
    reports {
        total {
            html {
                onCheck = true
            }
            xml {
                onCheck = true
            }
        }
    }
}

// ===== DOCUMENTATION =====
tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
    dokkaSourceSets {
        named("main") {
            moduleName.set("Genesis Core Module")
            moduleVersion.set("1.0.0")
            includes.from("Module.md")

            sourceLink {
                localDirectory.set(file("src/main/kotlin"))
                // remoteUrl.set(uri("https://github.com/aurakai/Genesis-Os/tree/main/core-module/src/main/kotlin").toURL())
                remoteLineSuffix.set("#L")
            }

            perPackageOption {
                matchingRegex.set(".*\\.internal.*")
                suppress.set(true)
            }
        }
    }
}

// ===== BUILD AUTOMATION TASKS =====
tasks.register("checkCodeQuality") {
    group = "verification"
    description = "Run all code quality checks for core module"
    dependsOn("spotlessCheck", "lint")
}

tasks.register("runAllTests") {
    group = "verification"
    description = "Run all tests with coverage for core module"
    dependsOn("test", "connectedAndroidTest", "koverHtmlReport")
}

tasks.register("buildAndTest") {
    group = "build"

    description = "Complete build and test cycle for core module"
    dependsOn("checkCodeQuality", "build", "runAllTests")
}

tasks.named<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiGenerate") {
    generatorName.set("kotlin")
    inputSpec.set("${project.rootDir}/app/api/genesis-api.yml")
    outputDir.set("$buildDir/generated")
}
