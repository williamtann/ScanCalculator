plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp") version "1.8.21-1.0.11"
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}

android {
    namespace = "com.will.scancalculator"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.will.scancalculator"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    @Suppress("UnstableApiUsage")
    flavorDimensions.add("variant")
    productFlavors {
        create("redFile") {
            dimension = "variant"
            applicationIdSuffix = ".red.file"
            buildConfigField("String", "BUILD_THEME", "\"RED\"")
            buildConfigField("String", "MEDIA_SOURCE", "\"FILE\"")
            resValue("string", "app_name", "ScanCalculatorRedFile")
        }
        create("redCamera") {
            dimension = "variant"
            applicationIdSuffix = ".red.camera"
            buildConfigField("String", "BUILD_THEME", "\"RED\"")
            buildConfigField("String", "MEDIA_SOURCE", "\"CAMERA\"")
            resValue("string", "app_name", "ScanCalculatorRedCamera")
        }
        create("greenFile") {
            dimension = "variant"
            applicationIdSuffix = ".green.file"
            buildConfigField("String", "BUILD_THEME", "\"GREEN\"")
            buildConfigField("String", "MEDIA_SOURCE", "\"FILE\"")
            resValue("string", "app_name", "ScanCalculatorGreenFile")
        }
        create("greenCamera") {
            dimension = "variant"
            applicationIdSuffix = ".green.camera"
            buildConfigField("String", "BUILD_THEME", "\"GREEN\"")
            buildConfigField("String", "MEDIA_SOURCE", "\"CAMERA\"")
            resValue("string", "app_name", "ScanCalculatorGreenCamera")
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            @Suppress("UnstableApiUsage") proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    @Suppress("UnstableApiUsage") buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

dependencies {
    // Core
    implementation(Library.ktx)
    implementation(Library.lifecycleRuntimeKtx)
    implementation(Library.lifecycleViewModelCompose)
    implementation(Library.compose)
    implementation(platform(Library.composeBom))
    implementation(Library.composeUi)
    implementation(Library.composeUiGraphics)
    implementation(Library.composeUiToolingPreview)
    implementation(Library.composeMaterial)
    implementation(Library.composeMaterial3)

    // Dagger - Hilt
    implementation(Library.hilt)
    kapt(Library.hiltAnnotationCompiler)

    // Serialization
    implementation(Library.serialization)

    // Data Store
    implementation(Library.datastore)

    // Room
    implementation(Library.room)
    kapt(Library.roomCompiler)

    // Gson
    implementation(Library.gson)

    // Navigation
    implementation(Library.hiltNavigation)
    implementation(Library.composeDestinations)
    ksp(Library.composeDestinationsKsp)

    // Coil
    implementation(Library.coil)

    // CameraX
    implementation(Library.cameraxCore)
    implementation(Library.cameraxLifecycle)
    implementation(Library.cameraxView)

    // MlKit
    implementation(Library.mlKit)
}