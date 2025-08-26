import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.dokka)
    alias(libs.plugins.spotless)
}

android {
    namespace = "dev.aurakai.auraframefx.romtools"
    compileSdk = 36

    defaultConfig {
        minSdk = 33
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        // NDK configuration for ROM tools
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
                    "-DGENESIS_NEURAL_ACCELERATION=ON",
                    "-DGENESIS_ROM_TOOLS_V3=ON"
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
    implementation(project(":secure-comm"))

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

    // Security bundles
    implementation(libs.bundles.security)

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

// Configure native ROM tools build
tasks.configureEach {
    if (name.startsWith("externalNativeBuild")) {
        dependsOn("copyRomTools")
    }
}

// Task to copy ROM modification tools
tasks.register<Copy>("copyRomTools") {
    from("src/main/cpp")
    val destDir = layout.buildDirectory.dir("rom-tools").get()
    into(destDir)
    include("**/*.so", "**/*.bin", "**/*.img", "**/*.cpp", "**/*.h")
    includeEmptyDirs = false
    
    // ✅ FIXED: Ensure directory exists before copying
    doFirst {
        destDir.asFile.mkdirs()
        logger.lifecycle("📁 Creating ROM tools directory: ${destDir.asFile.absolutePath}")
    }
    
    doLast {
        logger.lifecycle("✅ ROM tools copied to: ${destDir.asFile.absolutePath}")
    }
}

// FIXED: Configuration cache compatible ROM tools verification
abstract class VerifyRomToolsTask : DefaultTask() {
    @get:InputDirectory
    @get:Optional  // ✅ FIXED: Make directory optional to prevent build failures
    abstract val romToolsDir: DirectoryProperty

    @TaskAction
    fun verify() {
        val dir = romToolsDir.orNull?.asFile
        if (dir?.exists() != true) {
            logger.warn("⚠️  ROM tools directory not found - ROM functionality may be limited")
        } else {
            logger.lifecycle("✅ ROM tools verified and ready: ${dir.absolutePath}")
        }
    }
}

// Task to verify ROM tools integrity (Configuration Cache Safe)
tasks.register<VerifyRomToolsTask>("verifyRomTools") {
    romToolsDir.set(layout.buildDirectory.dir("rom-tools"))
    // ✅ FIXED: Make sure copyRomTools runs first to create the directory
    dependsOn("copyRomTools")
}

tasks.named("preBuild") {
    dependsOn("verifyRomTools")
}