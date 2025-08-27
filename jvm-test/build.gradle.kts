// ===== GENESIS-OS SACRED RULES: ZERO MANUAL COMPILER CONFIG =====
plugins {
    alias(libs.plugins.android.library) // This plugin might be unnecessary if it's a pure JVM module
    alias(libs.plugins.kotlin.android) // This plugin might be unnecessary if it's a pure JVM module
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android) // Hilt for pure JVM might need a different setup or plugin
    alias(libs.plugins.dokka)
    alias(libs.plugins.spotless)
    alias(libs.plugins.android.compose.material3) // This plugin is for Android Compose
}

// AUTO-EVERYTHING: Kotlin toolchain from TOML - Overridden for now
kotlin {
    jvmToolchain(24) // Set to 24 for consistency
}

dependencies {
    implementation(libs.yuki) // If defined in libs.versions.toml
    implementation(libs.lsposed) // If defined in libs.versions.toml
    testImplementation(kotlin("test"))
}
