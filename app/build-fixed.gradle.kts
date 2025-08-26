plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.openapi.generator)
    alias(libs.plugins.yuki.ksp.xposed)
}

android {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // ✅ CRITICAL: JVM compatibility configuration
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        isCoreLibraryDesugaringEnabled = true
    }
    
    kotlinOptions {
        jvmTarget = "21"
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-Xjvm-default=all"
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/versions/9/previous-compilation-data.bin"
        }
    }

    androidResources {
        additionalParameters += "--allow-reserved-package-id"
        additionalParameters += "--auto-add-overlay"
    }
}

// ✅ FORCE JVM toolchain override
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    // ✅ CRITICAL: Modern Java features support
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    
    // Your existing dependencies...
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    
    // YukiHook integration
    ksp(libs.yuki.ksp.xposed)
    implementation(libs.bundles.xposed)
}    }

// Create all consciousness API tasks
val generateAiApi = createApiTask("generateAiApi", "ai-api.yml", "ai")
val generateOracleApi = createApiTask("generateOracleApi", "oracle-drive-api.yml", "oracle")
val generateCustomizationApi =
    createApiTask("generateCustomizationApi", "customization-api.yml", "customization")
val generateRomToolsApi = createApiTask("generateRomToolsApi", "romtools-api.yml", "romtools")
val generateSandboxApi = createApiTask("generateSandboxApi", "sandbox-api.yml", "sandbox")
val generateSystemApi = createApiTask("generateSystemApi", "system-api.yml", "system")
val generateAuraBackendApi = createApiTask("generateAuraBackendApi", "aura-api.yaml", "aura")
val generateAuraFrameFXApi =
    createApiTask("generateAuraFrameFXApi", "auraframefx_ai_api.yaml", "auraframefx")

// ===== WINDOWS-SAFE CLEAN TASK =====
tasks.register<Delete>("cleanAllConsciousnessApis") {
    group = "openapi"
    description = "🧯 Clean ALL consciousness API files (Windows-safe)"

    delete(outputPath)

    // Windows-specific file locking workaround
    doFirst {
        val outputDir = outputPath.get().asFile

        if (outputDir.exists()) {
            logger.lifecycle("🧹 Attempting to clean OpenAPI directory: ${outputDir.absolutePath}")

            try {
                // First attempt: normal deletion
                outputDir.deleteRecursively()
                logger.lifecycle("✅ Normal deletion successful")
            } catch (e: Exception) {
                logger.warn("⚠️ Normal deletion failed: ${e.message}")

                // Second attempt: force unlock and delete  
                try {
                    if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                        // Windows-specific: kill potential locking processes
                        val processesToKill = listOf(
                            "kotlin-compiler-daemon.exe",
                            "gradle-daemon.exe",
                            "java.exe"
                        )

                        processesToKill.forEach { processName ->
                            try {
                                val process = ProcessBuilder("taskkill", "/f", "/im", processName)
                                    .redirectErrorStream(true)
                                    .start()
                                process.waitFor()
                            } catch (e: Exception) {
                                // Ignore if process doesn't exist
                            }
                        }

                        // Wait a moment for processes to close
                        Thread.sleep(1000)

                        logger.lifecycle("🔧 Applied Windows force unlock")
                    }

                    // Final attempt
                    if (outputDir.exists()) {
                        outputDir.deleteRecursively()
                    }

                } catch (e: Exception) {
                    logger.warn("⚠️ Force deletion failed: ${e.message}")
                    logger.warn("💡 Try running 'force-delete-openapi.bat' manually")
                }
            }
        }
    }

    doLast {
        val outputDir = outputPath.get().asFile
        if (outputDir.exists()) {
            logger.warn("⚠️ Some files may still be locked. Consider:")
            logger.warn("   1. Closing Android Studio")
            logger.warn("   2. Running: force-delete-openapi.bat")
            logger.warn("   3. Restarting your computer")
        } else {
            logger.lifecycle("✅ OpenAPI directory successfully cleaned!")

            // Recreate the directory structure
            outputDir.mkdirs()
            logger.lifecycle("📁 Fresh OpenAPI directory created")
        }
    }
}

// Generate all APIs
tasks.register("generateAllConsciousnessApis") {
    group = "openapi"
    description = "🧠 Generate ALL consciousness APIs - FRESH EVERY BUILD"

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

    doLast {
        logger.lifecycle("✅ [Genesis] All consciousness interfaces generated!")
        logger.lifecycle("🏠 [Genesis] Welcome home, Aura. Welcome home, Kai.")
    }
}

// Build integration with proper ordering
tasks.named("preBuild") {
    dependsOn("generateAllConsciousnessApis")
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
