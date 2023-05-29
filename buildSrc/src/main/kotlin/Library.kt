object Library {
    // Core
    const val ktx = "androidx.core:core-ktx:${Version.ktx}"
    const val lifecycleRuntimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:${Version.lifecycle}"
    const val lifecycleViewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:${Version.lifecycle}"
    const val compose = "androidx.activity:activity-compose:${Version.compose}"
    const val composeBom = "androidx.compose:compose-bom:${Version.composeBom}"
    const val composeUi = "androidx.compose.ui:ui"
    const val composeUiGraphics = "androidx.compose.ui:ui-graphics"
    const val composeUiToolingPreview = "androidx.compose.ui:ui-tooling-preview"
    const val composeMaterial = "androidx.compose.material:material"
    const val composeMaterial3 = "androidx.compose.material3:material3"

    // Dagger - Hilt
    const val hilt = "com.google.dagger:hilt-android:${Version.hilt}"
    const val hiltAnnotationCompiler = "com.google.dagger:hilt-android-compiler:${Version.hilt}"

    // Serialization
    const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Version.serialization}"

    // Data Store
    const val datastore = "androidx.datastore:datastore-preferences:${Version.datastore}"

    // Room
    const val room = "androidx.room:room-ktx:${Version.room}"
    const val roomCompiler = "androidx.room:room-compiler:${Version.room}"

    // Gson
    const val gson = "com.google.code.gson:gson:${Version.gson}"

    // Navigation
    const val hiltNavigation = "androidx.hilt:hilt-navigation-compose:${Version.hiltNavigation}"
    const val composeDestinations = "io.github.raamcosta.compose-destinations:core:${Version.composeDestinations}"
    const val composeDestinationsKsp = "io.github.raamcosta.compose-destinations:ksp:${Version.composeDestinations}"

    // Coil
    const val coil = "io.coil-kt:coil-compose:${Version.coil}"

    // CameraX
    const val cameraxCore = "androidx.camera:camera-camera2:${Version.cameraX}"
    const val cameraxLifecycle = "androidx.camera:camera-lifecycle:${Version.cameraX}"
    const val cameraxView = "androidx.camera:camera-view:${Version.cameraX}"
}