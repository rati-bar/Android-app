plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    kotlin("kapt")
}

android {
    namespace = "com.screetime.child"
    compileSdk = Versions.compileSdk

    defaultConfig {
        applicationId = "com.screetime.child"
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk
        versionCode = Versions.versionCode
        versionName = Versions.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    flavorDimensions += "version"

    productFlavors {
        create("playstore") {
            dimension = "version"
            applicationIdSuffix = ".lite"
            versionNameSuffix = "-lite"

            // Play Store compliant - notification only
            buildConfigField("Boolean", "ENFORCEMENT_ENABLED", "false")
            buildConfigField("String", "VERSION_TYPE", "\"Play Store Edition\"")

            // Use different app name
            resValue("string", "app_name", "Screen Time Lite")
        }

        create("pro") {
            dimension = "version"

            // Full enforcement with Device Owner
            buildConfigField("Boolean", "ENFORCEMENT_ENABLED", "true")
            buildConfigField("String", "VERSION_TYPE", "\"Pro Edition\"")

            resValue("string", "app_name", "Screen Time Pro")
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
            signingConfig = signingConfigs.getByName("debug") // TODO: Configure release signing
        }
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.composeCompiler
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core modules
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))

    // Android Core
    implementation(Dependencies.kotlin)
    implementation(Dependencies.androidxCore)
    implementation(Dependencies.appCompat)
    implementation(Dependencies.lifecycleRuntime)
    implementation(Dependencies.lifecycleViewModel)
    implementation(Dependencies.lifecycleCompose)
    implementation(Dependencies.activityCompose)
    implementation(Dependencies.material)

    // Compose
    implementation(platform(Dependencies.composeBom))
    implementation(Dependencies.composeUI)
    implementation(Dependencies.composeUIGraphics)
    implementation(Dependencies.composeUIToolingPreview)
    implementation(Dependencies.composeMaterial3)
    implementation(Dependencies.composeIconsExtended)
    implementation(Dependencies.navigationCompose)

    // Hilt
    implementation(Dependencies.hiltAndroid)
    implementation(Dependencies.hiltNavigationCompose)
    kapt(Dependencies.hiltCompiler)

    // Firebase
    implementation(platform(Dependencies.firebaseBom))
    implementation(Dependencies.firebaseAuth)
    implementation(Dependencies.firebaseFirestore)
    implementation(Dependencies.firebaseMessaging)
    implementation(Dependencies.firebaseCrashlytics)
    implementation(Dependencies.firebaseAnalytics)

    // Coroutines
    implementation(Dependencies.coroutinesCore)
    implementation(Dependencies.coroutinesAndroid)

    // DataStore
    implementation(Dependencies.datastorePreferences)

    // Security
    implementation(Dependencies.securityCrypto)
    implementation(Dependencies.playIntegrity)

    // WorkManager
    implementation(Dependencies.workManager)
    implementation(Dependencies.hiltWork)

    // Network
    implementation(Dependencies.okhttp)
    implementation(Dependencies.okhttpLogging)

    // Image Loading
    implementation(Dependencies.coil)

    // Debug
    debugImplementation(Dependencies.composeUITooling)
    debugImplementation(Dependencies.composeUITestManifest)

    // Testing
    testImplementation(Dependencies.junit)
    testImplementation(Dependencies.mockk)
    testImplementation(Dependencies.coroutinesTest)
    testImplementation(Dependencies.turbine)

    androidTestImplementation(Dependencies.junitExt)
    androidTestImplementation(Dependencies.espresso)
    androidTestImplementation(Dependencies.composeUITest)
    androidTestImplementation(Dependencies.hiltTesting)
    kaptAndroidTest(Dependencies.hiltCompiler)
}

kapt {
    correctErrorTypes = true
}
