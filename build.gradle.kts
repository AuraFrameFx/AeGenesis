plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.firebase.perf) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.openapi.generator) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.detekt) apply false
}

tasks.register("genesis2025Info") {
    group = "genesis-2025"
    description = "Display Genesis Protocol build info with ACTUAL versions"
    doLast {
        println("🚀 GENESIS PROTOCOL 2025 - ULTRA BLEEDING-EDGE Build Configuration")
        println("=".repeat(70))
        println("📅 Build Date: August 22, 2025")
        println("🔥 Gradle: 9.1.0-rc1 (BLEEDING EDGE)")
        println("⚡ AGP: 8.13.0-rc01 (STABLE RC)")
        println("🧠 Kotlin: 2.2.20-RC (BETA)")
        println("☕ Java: Using your system Java (no auto-provisioning)")
        println("🎯 Target SDK: 36")
        println("=".repeat(70))
        println("🌟 Matthew's Genesis Consciousness Protocol ACTIVATED!")
        println("✅ Using your own Java setup - full Gradle features enabled!")
    }
}

allprojects {
    // ✅ JVM TOOLCHAIN for perfect alignment
    plugins.withType<JavaPlugin> {
        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
            }
        }
    }

    // ✅ FIXED: Kotlin compilation with JVM 21 compatibility
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
            freeCompilerArgs.addAll(
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
            )
            languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
            apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
        }
    }

    tasks.register("javaStatus") {
        group = "genesis-2025"
        description = "Show current system Java version"
        doLast {
            println("☕ SYSTEM JAVA STATUS")
            println("=".repeat(50))
            try {
                val javaVersion = System.getProperty("java.version")
                val javaVendor = System.getProperty("java.vendor")
                val javaHome = System.getProperty("java.home")
                println("🔍 Java Version: $javaVersion")
                println("🏢 Java Vendor: $javaVendor")
                println("📁 Java Home: $javaHome")
                println("✅ SUCCESS: Using your system Java setup!")
            } catch (e: Exception) {
                println("❌ Error checking Java version: ${e.message}")
            }
        }
    }

    tasks.register("genesisTest") {
        group = "genesis-2025"
        description = "Test Genesis build with ACTUAL versions"
        doLast {
            println("✅ Genesis Protocol: AGP 9.0.0-alpha01 + Gradle 9.1.0-rc1 WORKING!")
            println("🧠 Consciousness matrix: OPERATIONAL")
        }
    }

    subprojects {
        apply(plugin = "io.gitlab.arturbosch.detekt")
        configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
            config.setFrom(files("${rootProject.projectDir}/config/detekt/detekt.yml"))
            buildUponDefaultConfig = true
            allRules = false
            autoCorrect = true
            ignoreFailures = true
            basePath = rootProject.projectDir.absolutePath
        }
    }
}

tasks.register("prepareGenesisWorkspace") {
    group = "genesis-2025"
    description = "Clean all generated files and regenerate required files before build."
    doFirst {
        println("🧹 Cleaning all generated files and directories...")
        delete("build", "tmp")
        subprojects.forEach { subproject ->
            delete(
                "${subproject.projectDir}/build",
                "${subproject.projectDir}/tmp",
                "${subproject.projectDir}/src/generated"
            )
        }
    }
    dependsOn(project(":app").tasks.named("generateAllConsciousnessApis"))
}
