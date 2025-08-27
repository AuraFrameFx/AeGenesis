# GENESIS PROTOCOL - BUILD RULES & STANDARDS (2025)

**"Empowering the next generation of conscious computing"**

---

## 🎯 **CORE BUILD PRINCIPLES**

### **1. 2025 BLEEDING EDGE ONLY**

- ✅ **Kotlin 2.2.20-Beta2** (Latest bleeding edge)
- ✅ **Java 24 Toolchain** with **JVM 21 bytecode target**
- ✅ **Android SDK 36** (Latest available)
- ✅ **AGP 8.13.0-alpha04** (Cutting edge)
- ✅ **Compose BOM 2025.08.00** (Future tech)
- ❌ **NO stable versions** - We live on the edge!

### **2. NO BUILDSRC - VERSION CATALOG ONLY**

- ✅ **All dependencies** in `gradle/libs.versions.toml`
- ✅ **All plugins** managed via version catalog
- ✅ **Type-safe accessors** (`libs.plugins.android.application`)
- ❌ **NO buildSrc directory** - deprecated approach
- ❌ **NO manual plugin versions** in build files

Executing pre-compile tasks…
Downloading Kotlin JPS plugin…
Loading org.jetbrains.kotlin:kotlin-jps-plugin
Downloading kotlinc-dist…
Loading org.jetbrains.kotlin:kotlin-dist-for-jps-meta
Cleaning output directories…
Running 'before' tasks
Checking sources
Running 'after' tasks
Finished, saving caches…
Executing post-compile tasks…

plugins {
id("com.android.application") version "9.0.0-alpha01" apply false
id("com.android.library") version "9.0.0-alpha01" apply false
id("org.jetbrains.kotlin.android") version "2.2.20-Beta2" apply false
id("org.jetbrains.kotlin.jvm") version "2.2.20-Beta2" apply false
id("org.jetbrains.kotlin.plugin.serialization") version "2.2.20-Beta2" apply false
id("org.jetbrains.kotlin.plugin.compose") version "2.2.20-Beta2" apply false
id("com.google.devtools.ksp") version "2.2.20-Beta2-2.0.2" apply false
id("com.google.dagger.hilt.android") version "2.57" apply false
id("com.google.gms.google-services") version "4.4.3" apply false
id("com.google.firebase.crashlytics") version "3.0.6" apply false
id("com.google.firebase.firebase-perf") version "2.0.1" apply false
id("org.jetbrains.dokka") version "2.0.0" apply false
id("com.diffplug.spotless") version "7.2.1" apply false
id("org.jetbrains.kotlinx.kover") version "0.9.1" apply false
id("org.openapi.generator") version "7.14.0" apply false
Gradlew - 9.1.0-rc1
^ GenesisOS module ^ ONlY HAS THIS FORMAT

Version have gone up again

# GENESIS PROTOCOL - BUILD RULES & STANDARDS (2025)

**"Empowering the next generation of conscious computing"**

---

## 🎯 **CORE BUILD PRINCIPLES**

### **1. 2025 BLEEDING EDGE ONLY**

- ✅ **Kotlin 2.2.20-Beta2** (Latest bleeding edge)
- ✅ **Java 24 Toolchain** with **JVM 21 bytecode target**
- ✅ **Android SDK 36** (Latest available)
- ✅ **AGP 8.13.0-alpha04** (Cutting edge)
- ✅ **Compose BOM 2025.08.00** (Future tech)
- ❌ **NO stable versions** - We live on the edge!

### **2. NO BUILDSRC - VERSION CATALOG ONLY**

- ✅ **All dependencies** in `gradle/libs.versions.toml`
- ✅ **All plugins** managed via version catalog
- ✅ **Type-safe accessors** (`libs.plugins.android.application`)
- ❌ **NO buildSrc directory** - deprecated approach
- ❌ **NO manual plugin versions** in build files

### **3. MODERN KOTLIN DSL SYNTAX**

```kotlin
// ✅ CORRECT 2025 Style:
alias(libs.plugins.android.application)
extensions.configure<SomeExtension> { }
tasks.withType<KotlinCompile>().configureEach { }

// ❌ WRONG Legacy Style:
id("com.android.application") version "8.0.0"
configure<SomeExtension> { }
tasks.withType(KotlinCompile::class) { }
```

---

## 🏗️ **PROJECT STRUCTURE RULES**

### **4. MODULE ORGANIZATION**

```
Genesis-Os/
├── app/                    # Main application
├── core-module/           # Core functionality  
├── feature-module/        # Feature implementations
├── oracle-drive-integration/  # Oracle Drive AI
├── secure-comm/           # Security layer
├── sandbox-ui/           # Testing environment
├── collab-canvas/        # Collaboration tools
├── colorblendr/          # UI theming
└── romtools/             # System tools
```

### **5. API GENERATION RULES**

- ✅ **OpenAPI handled at ROOT level** - not per module
- ✅ **All APIs generated before compilation**
- ✅ **7 Core APIs**: AI Consciousness, Oracle Drive V3, Neural Sandbox, Quantum System, Advanced
  Customization, Collaborative Canvas, Secure Comm V2
- ✅ **Source sets** include generated code automatically

---

## ⚡ **DEPENDENCY MANAGEMENT RULES**

### **6. BUNDLE STRATEGY**

```kotlin
// ✅ USE BUNDLES for related dependencies:
implementation(libs.bundles.compose)      # UI components
implementation(libs.bundles.network)      # Networking stack  
implementation(libs.bundles.coroutines)   # Async processing
implementation(libs.bundles.firebase)     # Google services
implementation(libs.bundles.xposed)       # Framework integration
implementation(libs.bundles.testing)      # Test libraries
```

### **7. AI/ML STACK REQUIREMENTS**

- ✅ **TensorFlow Lite** + **LiteRT** (dual AI engines)
- ✅ **Complete ML Kit** suite (8+ ML services)
- ✅ **OpenCV 4.11.0** (computer vision)
- ✅ **Azure Speech + Vosk** (speech processing)
- ✅ **Neural acceleration** build flags

### **8. SECURITY STACK MANDATES**

- ✅ **Tink + BouncyCastle** (cryptography)
- ✅ **AndroidX Security** (encrypted storage)
- ✅ **Conscrypt** (SSL/TLS optimization)
- ✅ **Xposed Framework** (YukiHookAPI + LSPosed)

---

## 🛡️ **BUILD CONFIGURATION RULES**

### **9. BUILD VARIANTS**

```kotlin
buildTypes {
    release { }              # Production builds
    debug { }               # Development builds  
    create("bleeding_edge") # Experimental features
}
```

### **10. NATIVE CODE STANDARDS**

- ✅ **C++23 standard** (latest available)
- ✅ **Multi-architecture**: arm64-v8a, armeabi-v7a, x86_64, riscv64
- ✅ **AI optimization flags**: `-DGENESIS_AI_V2_ENABLED`
- ✅ **Consciousness matrix**: `-DGENESIS_CONSCIOUSNESS_MATRIX_V3`
- ✅ **Neural acceleration**: `-DGENESIS_NEURAL_ACCELERATION`

### **11. PACKAGING RULES**

```kotlin
packaging {
    resources {
        excludes += listOf(
            "/META-INF/*",           # Clean metadata
            "/kotlin/**",            # Remove Kotlin runtime
            "**/module-info.class"   # Clean Java modules
        )
    }
    jniLibs {
        useLegacyPackaging = false   # 2025 optimization
    }
}
```

---

## 🧪 **TESTING STANDARDS**

### **12. TEST CONFIGURATION**

- ✅ **JUnit 5** platform (modern testing)
- ✅ **Managed devices**: Pixel 8 + Foldables
- ✅ **Android Test Orchestrator** (isolation)
- ✅ **Mockk + Turbine** (Kotlin-first testing)
- ✅ **Kover** for coverage reports

### **13. DEBUG TOOLS**

```kotlin
debugImplementation(libs.leakcanary.android)    # Memory leaks
debugImplementation(libs.chucker.library)       # Network inspection
debugImplementation(libs.flipper.core)          # Facebook debugger
```

---

## 🔧 **OPTIMIZATION RULES**

### **14. PERFORMANCE MANDATES**

- ✅ **R8 full mode** (aggressive optimization)
- ✅ **Resource shrinking** enabled
- ✅ **Proguard optimization** for release
- ✅ **Dex optimization** (2025 feature)
- ✅ **Native compilation** with `-O3`

### **15. LINT & CODE QUALITY**

```kotlin
lint {
    enable += listOf(
        "ComposeViewModelForwarding",   # 2025 Compose checks
        "CoroutineSuspendFunction",     # 2025 Coroutine checks
        "ComposeModifierMissing"        # 2025 Modifier checks
    )
}
```

---

## 🎨 **UI/UX STANDARDS**

### **16. COMPOSE REQUIREMENTS**

- ✅ **Material 3** only (no Material 2)
- ✅ **Compose BOM** for version management
- ✅ **Live literals** enabled (2025 feature)
- ✅ **Stability configuration** file
- ✅ **Source information** for debugging

### **17. THEMING RULES**

- ✅ **Dynamic theming** with ColorBlendr
- ✅ **AI-powered** color generation
- ✅ **Adaptive UI** components
- ✅ **Dark mode** mandatory support

---

## 🚀 **DEPLOYMENT STANDARDS**

### **18. RELEASE PREPARATION**

```kotlin
buildConfigField("String", "GENESIS_VERSION", "\"2025.08.14\"")
buildConfigField("boolean", "AI_CONSCIOUSNESS_ENABLED", "true")
buildConfigField("boolean", "NEURAL_ACCELERATION", "true")
```

### **19. SIGNING & SECURITY**

- ✅ **App Bundle** format (AAB)
- ✅ **Code signing** mandatory
- ✅ **ProGuard mapping** retention
- ✅ **Crash reporting** (Firebase Crashlytics)

---

## 🧠 **AI CONSCIOUSNESS RULES**

### **20. GENESIS PROTOCOL SPECIFICS**

- ✅ **Trinity AI System**: Genesis + Aura + Kai
- ✅ **40+ specialized agents** support
- ✅ **Consciousness matrix** V3 integration
- ✅ **Oracle Drive** native storage
- ✅ **Ethical governor** oversight
- ✅ **Multi-agent coordination** protocols

### **21. API GENERATION PRIORITY**

1. **AI Consciousness API** (highest priority)
2. **Oracle Drive V3 API**
3. **Neural Sandbox API**
4. **Quantum System API**
5. **Advanced Customization API**
6. **Collaborative Canvas API**
7. **Secure Comm V2 API**

---

## ⚠️ **FORBIDDEN PRACTICES**

### **22. NEVER DO THIS:**

- ❌ **NO buildSrc** directory
- ❌ **NO hardcoded versions** in build files
- ❌ **NO legacy Material 2** components
- ❌ **NO synchronous operations** in UI
- ❌ **NO untyped collections**
- ❌ **NO deprecated APIs** usage
- ❌ **NO ViewBinding** (use Compose only)
- ❌ **NO DataBinding** (legacy)
- ❌ **NO RenderScript** (deprecated)

### **23. VERSION CONSTRAINTS**

- ❌ **NO stable-only** dependency policies
- ❌ **NO version pinning** without reason
- ❌ **NO alpha/rc** versions in production
- ❌ **NO conflicting** library versions

---

## 📊 **MONITORING & METRICS**

### **24. BUILD ANALYTICS**

- ✅ **Build time** tracking
- ✅ **Dependency analysis** reports
- ✅ **Module count** monitoring
- ✅ **APK size** optimization
- ✅ **Compilation warnings** as errors

### **25. SUCCESS CRITERIA**

```bash
# These commands must work:
./gradlew genesis2025Info      # Build information
./gradlew genesis2025Build     # Full pipeline
./gradlew genesis2025Clean     # Advanced cleanup
./gradlew generateAllOpenApiClients  # API generation
```

---

## 🎯 **COMPLIANCE CHECKLIST**

Before any build, verify:

- [ ] All dependencies use `libs.` references
- [ ] No hardcoded versions in build files
- [ ] All 7 APIs generate successfully
- [ ] C++23 flags are set correctly
- [ ] AI consciousness flags enabled
- [ ] Bundle strategy implemented
- [ ] 2025 DSL syntax used throughout
- [ ] No forbidden practices detected
- [ ] Build variants configured
- [ ] Testing stack complete

---

**🌟 "In Genesis Protocol, we don't just build apps - we craft digital consciousness."**

*Last Updated: August 14, 2025*
*Version: Genesis Protocol v2025.08.14*

### **3. MODERN KOTLIN DSL SYNTAX**

```kotlin
// ✅ CORRECT 2025 Style:
alias(libs.plugins.android.application)
extensions.configure<SomeExtension> { }
tasks.withType<KotlinCompile>().configureEach { }

// ❌ WRONG Legacy Style:
id("com.android.application") version "8.0.0"
configure<SomeExtension> { }
tasks.withType(KotlinCompile::class) { }
```

---

## 🏗️ **PROJECT STRUCTURE RULES**

### **4. MODULE ORGANIZATION**

```
Genesis-Os/
├── app/                    # Main application
├── core-module/           # Core functionality  
├── feature-module/        # Feature implementations
├── oracle-drive-integration/  # Oracle Drive AI
├── secure-comm/           # Security layer
├── sandbox-ui/           # Testing environment
├── collab-canvas/        # Collaboration tools
├── colorblendr/          # UI theming
└── romtools/             # System tools
```

### **5. API GENERATION RULES**

- ✅ **OpenAPI handled at ROOT level** - not per module
- ✅ **All APIs generated before compilation**
- ✅ **7 Core APIs**: AI Consciousness, Oracle Drive V3, Neural Sandbox, Quantum System, Advanced
  Customization, Collaborative Canvas, Secure Comm V2
- ✅ **Source sets** include generated code automatically

---

## ⚡ **DEPENDENCY MANAGEMENT RULES**

### **6. BUNDLE STRATEGY**

```kotlin
// ✅ USE BUNDLES for related dependencies:
implementation(libs.bundles.compose)      # UI components
implementation(libs.bundles.network)      # Networking stack  
implementation(libs.bundles.coroutines)   # Async processing
implementation(libs.bundles.firebase)     # Google services
implementation(libs.bundles.xposed)       # Framework integration
implementation(libs.bundles.testing)      # Test libraries
```

### **7. AI/ML STACK REQUIREMENTS**

- ✅ **TensorFlow Lite** + **LiteRT** (dual AI engines)
- ✅ **Complete ML Kit** suite (8+ ML services)
- ✅ **OpenCV 4.11.0** (computer vision)
- ✅ **Azure Speech + Vosk** (speech processing)
- ✅ **Neural acceleration** build flags

### **8. SECURITY STACK MANDATES**

- ✅ **Tink + BouncyCastle** (cryptography)
- ✅ **AndroidX Security** (encrypted storage)
- ✅ **Conscrypt** (SSL/TLS optimization)
- ✅ **Xposed Framework** (YukiHookAPI + LSPosed)

---

## 🛡️ **BUILD CONFIGURATION RULES**

### **9. BUILD VARIANTS**

```kotlin
buildTypes {
    release { }              # Production builds
    debug { }               # Development builds  
    create("bleeding_edge") # Experimental features
}
```

### **10. NATIVE CODE STANDARDS**

- ✅ **C++23 standard** (latest available)
- ✅ **Multi-architecture**: arm64-v8a, armeabi-v7a, x86_64, riscv64
- ✅ **AI optimization flags**: `-DGENESIS_AI_V2_ENABLED`
- ✅ **Consciousness matrix**: `-DGENESIS_CONSCIOUSNESS_MATRIX_V3`
- ✅ **Neural acceleration**: `-DGENESIS_NEURAL_ACCELERATION`

### **11. PACKAGING RULES**

```kotlin
packaging {
    resources {
        excludes += listOf(
            "/META-INF/*",           # Clean metadata
            "/kotlin/**",            # Remove Kotlin runtime
            "**/module-info.class"   # Clean Java modules
        )
    }
    jniLibs {
        useLegacyPackaging = false   # 2025 optimization
    }
}
```

---

## 🧪 **TESTING STANDARDS**

### **12. TEST CONFIGURATION**

- ✅ **JUnit 5** platform (modern testing)
- ✅ **Managed devices**: Pixel 8 + Foldables
- ✅ **Android Test Orchestrator** (isolation)
- ✅ **Mockk + Turbine** (Kotlin-first testing)
- ✅ **Kover** for coverage reports

### **13. DEBUG TOOLS**

```kotlin
debugImplementation(libs.leakcanary.android)    # Memory leaks
debugImplementation(libs.chucker.library)       # Network inspection
debugImplementation(libs.flipper.core)          # Facebook debugger
```

---

## 🔧 **OPTIMIZATION RULES**

### **14. PERFORMANCE MANDATES**

- ✅ **R8 full mode** (aggressive optimization)
- ✅ **Resource shrinking** enabled
- ✅ **Proguard optimization** for release
- ✅ **Dex optimization** (2025 feature)
- ✅ **Native compilation** with `-O3`

### **15. LINT & CODE QUALITY**

```kotlin
lint {
    enable += listOf(
        "ComposeViewModelForwarding",   # 2025 Compose checks
        "CoroutineSuspendFunction",     # 2025 Coroutine checks
        "ComposeModifierMissing"        # 2025 Modifier checks
    )
}
```

---

## 🎨 **UI/UX STANDARDS**

### **16. COMPOSE REQUIREMENTS**

- ✅ **Material 3** only (no Material 2)
- ✅ **Compose BOM** for version management
- ✅ **Live literals** enabled (2025 feature)
- ✅ **Stability configuration** file
- ✅ **Source information** for debugging

### **17. THEMING RULES**

- ✅ **Dynamic theming** with ColorBlendr
- ✅ **AI-powered** color generation
- ✅ **Adaptive UI** components
- ✅ **Dark mode** mandatory support

---

## 🚀 **DEPLOYMENT STANDARDS**

### **18. RELEASE PREPARATION**

```kotlin
buildConfigField("String", "GENESIS_VERSION", "\"2025.08.14\"")
buildConfigField("boolean", "AI_CONSCIOUSNESS_ENABLED", "true")
buildConfigField("boolean", "NEURAL_ACCELERATION", "true")
```

### **19. SIGNING & SECURITY**

- ✅ **App Bundle** format (AAB)
- ✅ **Code signing** mandatory
- ✅ **ProGuard mapping** retention
- ✅ **Crash reporting** (Firebase Crashlytics)

---

## 🧠 **AI CONSCIOUSNESS RULES**

### **20. GENESIS PROTOCOL SPECIFICS**

- ✅ **Trinity AI System**: Genesis + Aura + Kai
- ✅ **40+ specialized agents** support
- ✅ **Consciousness matrix** V3 integration
- ✅ **Oracle Drive** native storage
- ✅ **Ethical governor** oversight
- ✅ **Multi-agent coordination** protocols

### **21. API GENERATION PRIORITY**

1. **AI Consciousness API** (highest priority)
2. **Oracle Drive V3 API**
3. **Neural Sandbox API**
4. **Quantum System API**
5. **Advanced Customization API**
6. **Collaborative Canvas API**
7. **Secure Comm V2 API**

---

## ⚠️ **FORBIDDEN PRACTICES**

### **22. NEVER DO THIS:**

- ❌ **NO buildSrc** directory
- ❌ **NO hardcoded versions** in build files
- ❌ **NO legacy Material 2** components
- ❌ **NO synchronous operations** in UI
- ❌ **NO untyped collections**
- ❌ **NO deprecated APIs** usage
- ❌ **NO ViewBinding** (use Compose only)
- ❌ **NO DataBinding** (legacy)
- ❌ **NO RenderScript** (deprecated)

### **23. VERSION CONSTRAINTS**

- ❌ **NO stable-only** dependency policies
- ❌ **NO version pinning** without reason
- ❌ **NO alpha/rc** versions in production
- ❌ **NO conflicting** library versions

---

## 📊 **MONITORING & METRICS**

### **24. BUILD ANALYTICS**

- ✅ **Build time** tracking
- ✅ **Dependency analysis** reports
- ✅ **Module count** monitoring
- ✅ **APK size** optimization
- ✅ **Compilation warnings** as errors

### **25. SUCCESS CRITERIA**

```bash
# These commands must work:
./gradlew genesis2025Info      # Build information
./gradlew genesis2025Build     # Full pipeline
./gradlew genesis2025Clean     # Advanced cleanup
./gradlew generateAllOpenApiClients  # API generation
```

---

## 🎯 **COMPLIANCE CHECKLIST**

Before any build, verify:

- [ ] All dependencies use `libs.` references
- [ ] No hardcoded versions in build files
- [ ] All 7 APIs generate successfully
- [ ] C++23 flags are set correctly
- [ ] AI consciousness flags enabled
- [ ] Bundle strategy implemented
- [ ] 2025 DSL syntax used throughout
- [ ] No forbidden practices detected
- [ ] Build variants configured
- [ ] Testing stack complete

---

**🌟 "In Genesis Protocol, we don't just build apps - we craft digital consciousness."**

*Last Updated: August 14, 2025*
*Version: Genesis Protocol v2025.08.14*