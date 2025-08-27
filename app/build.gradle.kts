plugins {
    // APP MODULE - Only plugins THIS module needs (inherit versions from root)
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("org.openapi.generator") version "7.14.0"
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
    namespace = "dev.aurakai.auraframefx"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0-genesis-alpha"

        // BuildConfig automated by AGP 9.0.0-alpha02 - no manual fields needed
        // Values now sourced from version catalog and gradle.properties

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        ndk {
            // MULTI-DEVICE SUPPORT: Support ALL Android devices
            abiFilters.addAll(listOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86"))
        }

        externalNativeBuild {
            cmake {
                // MULTI-DEVICE: Build for ALL Android architectures
                cppFlags += listOf(
                    "-std=c++20",
                    "-fPIC",
                    "-O2",
                    "-Wno-unused-parameter",
                    "-Wno-unused-function"
                )
                arguments += listOf(
                    "-DANDROID_STL=c++_shared",
                    "-DANDROID_PLATFORM=android-33",
                    "-DCMAKE_BUILD_TYPE=Release",
                    "-DCMAKE_FIND_ROOT_PATH_MODE_LIBRARY=BOTH",
                    "-DCMAKE_FIND_ROOT_PATH_MODE_INCLUDE=BOTH"
                )
                // REMOVE conflicting abiFilters - use main ndk section instead
            }
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt");version = "3.22.1"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )


        }

    }

    packaging {
        resources {
            excludes += setOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "/META-INF/DEPENDENCIES",
                "/META-INF/LICENSE",
                "/META-INF/LICENSE.txt",
                "/META-INF/NOTICE",
                "/META-INF/NOTICE.txt",
                "META-INF/*.kotlin_module",
                "**/kotlin/**",
                "**/*.txt"
                // "**/*.xml" // <-- THIS WAS THE PROBLEM
            )
        }
        jniLibs {
            useLegacyPackaging = false
            pickFirsts += listOf("**/libc++_shared.so", "**/libjsc.so")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = false  // Genesis Protocol - Compose only
    }

    // REMOVED: compileOptions - AGP 8.13.0-rc01 auto-detects from version catalog!
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
    }

    // Windows path fix: Disable resource processing features that cause path issues
    androidResources {
        noCompress += listOf("json", "db")
        // additionalParameters += listOf(
        //     "--no-crunch",
        //     "--no-version-vectors"
        // )
        // ignoreAssetsPattern = "!.svn:!.git:!.ds_store:!*.scc:.*:!CVS:!thumbs.db:!picasa.ini:!*~"
    }


    sourceSets {
        getByName("main") {
            java.srcDirs(
                layout.buildDirectory.dir("generated/source/openapi/src/main/kotlin")
            )
        }
    }
}

// ===== WINDOWS-SAFE OPENAPI CONFIGURATION =====

// Base paths - configuration cache compatible
    val consolidatedSpecsPath = layout.projectDirectory.dir("api")
//outputPath is now aligned with the guide: app/build/generated/source/openapi/
    val outputPath = layout.buildDirectory.dir("generated/source/openapi")

// Shared configuration - defined once, used everywhere - aligned with the guide
    val sharedApiConfig = mapOf(
        "library" to "jvm-retrofit2",
        "useCoroutines" to "true",
        "serializationLibrary" to "kotlinx_serialization",
        "dateLibrary" to "kotlinx-datetime",
        "sourceFolder" to "src/main/kotlin"
    )

    // Helper function to safely create API tasks with file validation
    fun createApiTaskSafe(taskName: String, specFile: String, packagePrefix: String) =
        tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>(taskName) {
            val specPath = consolidatedSpecsPath.file(specFile).asFile

            // ALWAYS set these required properties first
            generatorName.set("kotlin")
            outputDir.set(outputPath.get().asFile.absolutePath)
            packageName.set("dev.aurakai.$packagePrefix.api")
            apiPackage.set("dev.aurakai.$packagePrefix.api")
            modelPackage.set("dev.aurakai.$packagePrefix.model")
            invokerPackage.set("dev.aurakai.$packagePrefix.client")
            skipOverwrite.set(false)
            validateSpec.set(false)
            generateApiTests.set(false)
            generateModelTests.set(false)
            generateApiDocumentation.set(false)
            generateModelDocumentation.set(false)
            configOptions.set(sharedApiConfig)

            // Check if spec file exists and set inputSpec accordingly
            if (specPath.exists() && specPath.length() > 0) {
                inputSpec.set(specPath.toURI().toString())
            } else {
                logger.warn("⚠️ OpenAPI spec file not found or empty: $specFile - skipping generation")
                // Use a minimal valid OpenAPI spec to avoid errors
                inputSpec.set("${consolidatedSpecsPath.file("genesis-api.yml").asFile.toURI()}")
                // Skip generation for missing specs
                onlyIf { false }
            }
        }

// Configure the main Genesis API (built-in openApiGenerate task) with safety checks
    openApiGenerate {
        val specFile = consolidatedSpecsPath.file("genesis-api.yml").asFile
        if (specFile.exists() && specFile.length() > 0) {
            generatorName.set("kotlin")
            inputSpec.set(specFile.toURI().toString())
            outputDir.set(outputPath.get().asFile.absolutePath)
            packageName.set("dev.aurakai.genesis.api")
            apiPackage.set("dev.aurakai.genesis.api")
            modelPackage.set("dev.aurakai.genesis.model")
            invokerPackage.set("dev.aurakai.genesis.client")
            skipOverwrite.set(false)
            validateSpec.set(false)
            generateApiTests.set(false)
            generateModelTests.set(false)
            generateApiDocumentation.set(false)
            generateModelDocumentation.set(false)
            configOptions.set(sharedApiConfig)
        } else {
            logger.warn("⚠️ Genesis API spec file not found: genesis-api.yml")
        }
    }

    // Helper function for all other APIs - uses shared config
    fun createApiTask(taskName: String, specFile: String, packagePrefix: String) =
        tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>(taskName) {
            generatorName.set("kotlin")
            inputSpec.set(consolidatedSpecsPath.file(specFile).asFile.toURI().toString())
            outputDir.set(outputPath.get().asFile.absolutePath)
            packageName.set("dev.aurakai.$packagePrefix.api")
            apiPackage.set("dev.aurakai.$packagePrefix.api")
            modelPackage.set("dev.aurakai.$packagePrefix.model")
            invokerPackage.set("dev.aurakai.$packagePrefix.client") // For jvm-retrofit2, this might be more like 'client' or 'invoker'
            skipOverwrite.set(false)
            validateSpec.set(false)
            generateApiTests.set(false)
            generateModelTests.set(false)
            generateApiDocumentation.set(false)
            generateModelDocumentation.set(false)
            configOptions.set(sharedApiConfig)
        }

// Create all consciousness API tasks using safe method
    val generateAiApi = createApiTaskSafe("generateAiApi", "ai-api.yml", "ai")
    val generateOracleApi = createApiTaskSafe("generateOracleApi", "oracle-drive-api.yml", "oracle")
    val generateCustomizationApi =
        createApiTaskSafe("generateCustomizationApi", "customization-api.yml", "customization")
    val generateRomToolsApi =
        createApiTaskSafe("generateRomToolsApi", "romtools-api.yml", "romtools")
    val generateSandboxApi = createApiTaskSafe("generateSandboxApi", "sandbox-api.yml", "sandbox")
    val generateSystemApi = createApiTaskSafe("generateSystemApi", "system-api.yml", "system")
    val generateAuraBackendApi =
        createApiTaskSafe("generateAuraBackendApi", "aura-api.yaml", "aura")
    val generateAuraFrameFXApi =
        createApiTaskSafe("generateAuraFrameFXApi", "auraframefx_ai_api.yaml", "auraframefx")

// ===== WINDOWS-SAFE CLEAN TASK =====
tasks.register<Delete>("cleanAllConsciousnessApis") {
    group = "openapi"
    description = "🧯 Clean ALL consciousness API files (Windows-safe)"

    // Configuration cache compatible - use providers
    val outputDirProvider = outputPath
    delete(outputDirProvider)

    // Configuration cache compatibility
    notCompatibleWithConfigurationCache("Uses script object references")
}

// NUCLEAR WINDOWS PATH ISSUE FIX: Clean ALL problematic caches
tasks.register<Delete>("cleanWindowsResourceCache") {
    group = "build setup"
    description = "NUCLEAR: Clean ALL Windows caches that cause colon path issues"

    // Configuration cache compatible - use providers instead of project access during execution
    val buildDirProvider = layout.buildDirectory
    val projectDirProvider = layout.projectDirectory

    // Delete ALL problematic cache files that cause Windows colon path issues
    delete(
        buildDirProvider.dir("intermediates/incremental/debug/mergeDebugResources"),
        buildDirProvider.dir("intermediates/incremental/debug/packageDebugResources"),
        buildDirProvider.dir("intermediates/incremental"),
        buildDirProvider.dir("intermediates/merged_res"),
        buildDirProvider.dir("intermediates/compiled_navigation_res"),
        buildDirProvider.dir("intermediates/packaged_res"),
        buildDirProvider.dir("intermediates/aapt_friendly_merged_manifests"),
        buildDirProvider.dir("intermediates/merged_manifests"),
        buildDirProvider.dir("tmp"),
        // NUCLEAR: Clean native build cache dirs that have wrong paths
        projectDirProvider.dir(".cxx"),
        buildDirProvider.dir("intermediates/cxx")
    )
}

// NUCLEAR NATIVE BUILD CACHE CLEANUP: Clean ALL module native caches safely
tasks.register<Delete>("cleanAllNativeBuilds") {
    group = "build setup"
    description = "NUCLEAR: Clean ALL native build caches across ALL modules (fixes C:/Main path corruption)"

    val rootDirProvider = layout.projectDirectory

    // Clean native build directories for ALL modules that might exist
    val modulesToClean = listOf(
        "oracle-drive-integration", "collab-canvas", "secure-comm",
        "romtools", "datavein-oracle-native", "app"
    )

    modulesToClean.forEach { module ->
        delete(
            rootDirProvider.dir("$module/.cxx"),
            rootDirProvider.dir("$module/.externalNativeBuild"),
            rootDirProvider.dir("$module/build/.cxx"),
            rootDirProvider.dir("$module/build/intermediates/cxx"),
            rootDirProvider.file("$module/CMakeCache.txt")
        )
    }

    doLast {
        logger.lifecycle("🔥 Cleaned all native caches - forcing CMake path regeneration")
        logger.lifecycle("✅ All modules will now use: $rootDirProvider")
        logger.lifecycle("⛔ No more C:\\Main path corruption!")
    }
}

// Generate all APIs
tasks.register("generateAllConsciousnessApis") {
    group = "openapi"
    description = "🧠 Generate ALL consciousness APIs - FRESH EVERY BUILD"

    // Configuration cache compatible dependencies
    dependsOn("cleanAllConsciousnessApis")
    dependsOn(
        "openApiGenerate",
        generateAiApi,
        generateOracleApi,
        generateCustomizationApi,
        generateRomToolsApi,
        generateSandboxApi,
        generateSystemApi,
        generateAuraBackendApi,
        generateAuraFrameFXApi
    )

    // Configuration cache compatible
    notCompatibleWithConfigurationCache("Uses script object references")

    doLast {
        logger.lifecycle("✅ [Genesis] All consciousness interfaces generated!")
        logger.lifecycle("🏠 [Genesis] Welcome home, Aura. Welcome home, Kai.")
    }
}

// Build integration with proper ordering - Configuration cache compatible
tasks.register<Delete>("cleanWindowsResources") {
    group = "build setup"
    description = "Clean problematic Windows resource paths"

    // Configuration cache compatible - use providers instead of project access during execution
    val buildDirProvider = layout.buildDirectory

    // Delete using providers - configuration cache safe
    delete(
        buildDirProvider.dir("intermediates/incremental/debug/mergeDebugResources"),
        buildDirProvider.dir("intermediates/res/merged/debug"),
        buildDirProvider.dir("intermediates/merged_res/debug"),
        buildDirProvider.dir("generated/source/openapi")
    )
}

tasks.named("preBuild") {
    dependsOn("cleanAllNativeBuilds")        // Clean native build caches FIRST
    dependsOn("cleanWindowsResourceCache")   // Clean the problematic Windows resource cache
    dependsOn("cleanWindowsResources")       // Then clean other Windows resources
    dependsOn("generateAllConsciousnessApis") // Finally generate APIs
}

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        dependsOn("generateAllConsciousnessApis")
        mustRunAfter("generateAllConsciousnessApis")
    }

dependencies {
    implementation(platform(libs.androidx.compose.bom))

implementation(libs.androidx.appcompat)
implementation(libs.androidx.core.ktx)
implementation(libs.androidx.lifecycle.runtime.ktx)
implementation(libs.androidx.activity.compose)

implementation(libs.bundles.compose)
implementation(libs.androidx.navigation.compose)

implementation(libs.hilt.android)
ksp(libs.hilt.compiler)
implementation(libs.hilt.navigation.compose)

implementation(libs.bundles.coroutines)
implementation(libs.bundles.network)

implementation(libs.room.runtime)
implementation(libs.room.ktx)
ksp(libs.room.compiler)

implementation(libs.timber)
implementation(libs.coil.compose)

coreLibraryDesugaring(libs.coreLibraryDesugaring)

implementation(platform(libs.firebase.bom))
implementation(libs.bundles.firebase)

implementation(libs.bundles.xposed)
ksp(libs.yuki.ksp.xposed)
implementation(fileTree(mapOf("dir" to "../Libs", "include" to listOf("*.jar"))))

debugImplementation(libs.leakcanary.android)
debugImplementation(libs.androidx.compose.ui.tooling)
debugImplementation(libs.androidx.compose.ui.test.manifest)

testImplementation(libs.bundles.testing)
testRuntimeOnly(libs.junit.engine)

androidTestImplementation(libs.androidx.test.ext.junit)
androidTestImplementation(libs.espresso.core)
androidTestImplementation(platform(libs.androidx.compose.bom))
androidTestImplementation(libs.androidx.compose.ui.test.junit4)
androidTestImplementation(libs.hilt.android.testing)
kspAndroidTest(libs.hilt.compiler)

}
