import java.util.*

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.10"
}

val properties = Properties().apply {
    runCatching { load(File(rootDir, "keystore.properties").inputStream()) }
}

val localProperties = Properties().apply {
    runCatching { load(File(rootDir, "local.properties").inputStream()) }
}

val defaultGuestPortal = localProperties.getProperty("DEFAULT_GUEST_PORTAL").orEmpty()
val defaultGuestName = localProperties.getProperty("DEFAULT_GUEST_NAME").orEmpty()
val defaultGuestRoomKey = localProperties.getProperty("DEFAULT_GUEST_ROOM_KEY").orEmpty()
val defaultGuestRoomPin = localProperties.getProperty("DEFAULT_GUEST_ROOM_PIN").orEmpty()

val defaultGoogleAnalyticsId = System.getProperty("VC_DEFAULT_GOOGLE_ANALYTICS_ID")
    ?: (gradle as ExtensionAware).extra.properties["VC_DEFAULT_GOOGLE_ANALYTICS_ID"]
    ?: ""

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "com.vidyo.connector"
        minSdk = 23
        targetSdk = 31
        versionCode = 1
        versionName = "1.0.0"

        buildConfigField("String", "DEFAULT_GUEST_PORTAL", "\"$defaultGuestPortal\"")
        buildConfigField("String", "DEFAULT_GUEST_NAME", "\"$defaultGuestName\"")
        buildConfigField("String", "DEFAULT_GUEST_ROOM_KEY", "\"$defaultGuestRoomKey\"")
        buildConfigField("String", "DEFAULT_GUEST_ROOM_PIN", "\"$defaultGuestRoomPin\"")
        buildConfigField("String", "DEFAULT_GOOGLE_ANALYTICS_ID", "\"$defaultGoogleAnalyticsId\"")
    }

    val releaseSigningConfig = signingConfigs.create("release") {
        storeFile = File(System.getProperty("user.home"), properties.getProperty("storeFile").orEmpty())
        storePassword = properties.getProperty("storePassword").orEmpty()
        keyAlias = properties.getProperty("keyAlias").orEmpty()
        keyPassword = properties.getProperty("keyPassword").orEmpty()
    }

    buildTypes {
        getByName("release") {
            signingConfig = releaseSigningConfig
            isMinifyEnabled = true
            proguardFile("proguard-rules.pro")
            proguardFile(getDefaultProguardFile("proguard-android.txt"))
        }
    }

    buildFeatures {
        compose = true
        dataBinding = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf(
            "-Werror",
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xopt-in=kotlin.ExperimentalStdlibApi",
            "-Xopt-in=kotlin.time.ExperimentalTime",
            "-Xopt-in=kotlinx.coroutines.FlowPreview",
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xopt-in=androidx.compose.material.ExperimentalMaterialApi",
            "-Xopt-in=androidx.compose.animation.ExperimentalAnimationApi",
            "-Xopt-in=androidx.compose.foundation.ExperimentalFoundationApi",
        )
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.1"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    ((gradle as ExtensionAware).extra["VC_DEPENDENCIES"] as List<*>).forEach {
        implementation(checkNotNull(it))
    }

    // Kotlin
    val kotlinVersion: String by rootProject.extra
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

    // AppCompat
    implementation("androidx.appcompat:appcompat:1.4.1")

    // Compose
    implementation("androidx.compose.ui:ui:1.1.1")
    implementation("androidx.compose.ui:ui-tooling:1.1.1")
    implementation("androidx.compose.foundation:foundation:1.1.1")
    implementation("androidx.compose.runtime:runtime-livedata:1.1.1")
    implementation("androidx.activity:activity-compose:1.4.0")

    // Compose Controls
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.0")

    // Compose Material
    implementation("androidx.compose.material:material:1.1.1")
    implementation("androidx.compose.material:material-icons-core:1.1.1")
    implementation("androidx.compose.material:material-icons-extended:1.1.1")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.1")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.4.2")

    // Material
    implementation("com.google.android.material:material:1.6.0")

    // Ktor
    implementation("io.ktor:ktor-client-core:2.0.1")
    implementation("io.ktor:ktor-client-cio:2.0.1")

    // ComposableRoutes
    implementation("com.github.MatrixDev.ComposableRoutes:composable-routes-lib:0.1.14")
    kapt("com.github.MatrixDev.ComposableRoutes:composable-routes-processor:0.1.14")
}
