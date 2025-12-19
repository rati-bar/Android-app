object Versions {
    // SDK Versions
    const val compileSdk = 34
    const val minSdk = 26
    const val targetSdk = 34

    // App Versions
    const val versionCode = 1
    const val versionName = "1.0.0"

    // Kotlin & Coroutines
    const val kotlin = "1.9.22"
    const val coroutines = "1.7.3"

    // Android Core
    const val androidxCore = "1.12.0"
    const val appCompat = "1.6.1"
    const val lifecycleRuntime = "2.7.0"
    const val activityCompose = "1.8.2"

    // Compose
    const val composeBom = "2024.02.00"
    const val composeCompiler = "1.5.8"

    // Dependency Injection
    const val hilt = "2.50"
    const val hiltNavigationCompose = "1.1.0"

    // Firebase
    const val firebaseBom = "32.7.2"

    // Room Database
    const val room = "2.6.1"

    // DataStore
    const val datastore = "1.0.0"

    // Network
    const val okhttp = "4.12.0"
    const val retrofit = "2.9.0"

    // Security
    const val securityCrypto = "1.1.0-alpha06"
    const val playIntegrity = "1.3.0"

    // Work Manager
    const val workManager = "2.9.0"

    // Navigation
    const val navigation = "2.7.6"

    // Coil (Image Loading)
    const val coil = "2.5.0"

    // Material
    const val material = "1.11.0"

    // Testing
    const val junit = "4.13.2"
    const val junitExt = "1.1.5"
    const val espresso = "3.5.1"
    const val mockk = "1.13.9"
    const val turbine = "1.0.0"
    const val coroutinesTest = "1.7.3"
}

object Dependencies {
    // Kotlin
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"

    // Android Core
    const val androidxCore = "androidx.core:core-ktx:${Versions.androidxCore}"
    const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycleRuntime}"
    const val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycleRuntime}"
    const val lifecycleCompose = "androidx.lifecycle:lifecycle-runtime-compose:${Versions.lifecycleRuntime}"
    const val activityCompose = "androidx.activity:activity-compose:${Versions.activityCompose}"

    // Compose BOM
    const val composeBom = "androidx.compose:compose-bom:${Versions.composeBom}"
    const val composeUI = "androidx.compose.ui:ui"
    const val composeUIGraphics = "androidx.compose.ui:ui-graphics"
    const val composeUIToolingPreview = "androidx.compose.ui:ui-tooling-preview"
    const val composeMaterial3 = "androidx.compose.material3:material3"
    const val composeIconsExtended = "androidx.compose.material:material-icons-extended"
    const val composeUITooling = "androidx.compose.ui:ui-tooling"
    const val composeUITestManifest = "androidx.compose.ui:ui-test-manifest"

    // Hilt
    const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.hilt}"
    const val hiltCompiler = "com.google.dagger:hilt-android-compiler:${Versions.hilt}"
    const val hiltNavigationCompose = "androidx.hilt:hilt-navigation-compose:${Versions.hiltNavigationCompose}"
    const val hiltWork = "androidx.hilt:hilt-work:${Versions.hiltNavigationCompose}"

    // Firebase BOM
    const val firebaseBom = "com.google.firebase:firebase-bom:${Versions.firebaseBom}"
    const val firebaseAuth = "com.google.firebase:firebase-auth-ktx"
    const val firebaseFirestore = "com.google.firebase:firebase-firestore-ktx"
    const val firebaseMessaging = "com.google.firebase:firebase-messaging-ktx"
    const val firebaseCrashlytics = "com.google.firebase:firebase-crashlytics-ktx"
    const val firebaseAnalytics = "com.google.firebase:firebase-analytics-ktx"
    const val firebaseStorage = "com.google.firebase:firebase-storage-ktx"

    // Room
    const val roomRuntime = "androidx.room:room-runtime:${Versions.room}"
    const val roomKtx = "androidx.room:room-ktx:${Versions.room}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"

    // DataStore
    const val datastorePreferences = "androidx.datastore:datastore-preferences:${Versions.datastore}"

    // Network
    const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    const val okhttpLogging = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitGson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"

    // Security
    const val securityCrypto = "androidx.security:security-crypto:${Versions.securityCrypto}"
    const val playIntegrity = "com.google.android.play:integrity:${Versions.playIntegrity}"

    // WorkManager
    const val workManager = "androidx.work:work-runtime-ktx:${Versions.workManager}"

    // Navigation
    const val navigationCompose = "androidx.navigation:navigation-compose:${Versions.navigation}"

    // Coil
    const val coil = "io.coil-kt:coil-compose:${Versions.coil}"

    // Material
    const val material = "com.google.android.material:material:${Versions.material}"

    // Testing
    const val junit = "junit:junit:${Versions.junit}"
    const val junitExt = "androidx.test.ext:junit:${Versions.junitExt}"
    const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
    const val composeUITest = "androidx.compose.ui:ui-test-junit4"
    const val mockk = "io.mockk:mockk:${Versions.mockk}"
    const val mockkAndroid = "io.mockk:mockk-android:${Versions.mockk}"
    const val turbine = "app.cash.turbine:turbine:${Versions.turbine}"
    const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutinesTest}"
    const val roomTesting = "androidx.room:room-testing:${Versions.room}"
    const val hiltTesting = "com.google.dagger:hilt-android-testing:${Versions.hilt}"
}
