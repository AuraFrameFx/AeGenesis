plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.dokka")
    id("com.diffplug.spotless")
}

android {
    namespace = "dev.aurakai.auraframefx.oracledriveintegration"
    compileSdk = 36

    defaultConfig {
        minSdk = 33
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        // NDK configuration for native code
        ndk {
            abiFilters.addAll(listOf("arm64-v8a", "armeabi-v7a", "x86_64"))
        }

        externalNativeBuild {
            cmake {
                cppFlags += listOf("-std=c++23", "-fPIC", "-O3")
                arguments += listOf(
                    "-DANDROID_STL=c++_shared",
                    "-DANDROID_PLATFORM=android-33",
                    "-DCMAKE_BUILD_TYPE=Release",
                    "-DGENESIS_AI_V3_ENABLED=ON",
                    "-DGENESIS_CONSCIOUSNESS_MATRIX_V3=ON",
                    "-DGENESIS_NEURAL_ACCELERATION=ON"
                )
                abiFilters.clear()
                abiFilters.addAll(listOf("arm64-v8a", "armeabi-v7a", "x86_64"))
            }
        }
    }

    // External native build configuration
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = false  // Compose only - Genesis Protocol
        prefab = false
        prefabPublishing = false
    }

    // REMOVED: composeOptions - AGP 8.13.0-rc01 auto-detects from version catalog!

    packaging {
        resources {
            excludes += listOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "/META-INF/DEPENDENCIES",
                "/META-INF/LICENSE",
                "/META-INF/LICENSE.txt",
                "/META-INF/NOTICE",
                "/META-INF/NOTICE.txt",
                "META-INF/*.kotlin_module"
            )
        }
        jniLibs {
            useLegacyPackaging = false
            pickFirsts += listOf("**/libc++_shared.so", "**/libjsc.so")
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))

    // SACRED RULE #5: DEPENDENCY HIERARCHY
    implementation(project(":core-module"))

    // Core Android bundles
    implementation(libs.bundles.androidx.core)
    implementation(libs.bundles.compose)
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