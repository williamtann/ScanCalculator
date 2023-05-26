plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
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

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            @Suppress("UnstableApiUsage")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    @Suppress("UnstableApiUsage")
    buildFeatures {
        compose = true
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
    implementation(Library.ktx)
    implementation(Library.lifecycle)
    implementation(Library.compose)
    implementation(platform(Library.composeBom))
    implementation(Library.composeUi)
    implementation(Library.composeUiGraphics)
    implementation(Library.composeUiToolingPreview)
    implementation(Library.composeMaterial3)

    // by ViewModel() for Activity
    implementation(Library.activityKtx)

    // Dagger - Hilt
    implementation(Library.hilt)
    kapt(Library.hiltAnnotationCompiler)
}