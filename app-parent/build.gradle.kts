plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    kotlin("kapt")
}

android {
    namespace = "com.screetime.parent"
    compileSdk = Versions.compileSdk

    defaultConfig {
        applicationId = "com.screetime.parent"
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
            versionNameSuffix = "-lite"

            buildConfigField("String", "VERSION_TYPE", "\"Play Store Edition\"")
            resValue("string", "app_name", "Screen Time Parent")
        }

        create("pro") {
            dimension = "version"

            buildConfigField("String", "VERSION_TYPE", "\"Pro Edition\"")
            resValue("string", "app_name", "Screen Time Parent Pro")
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

    // Hilt
    implementation(Dependencies.hiltAndroid)
    kapt(Dependencies.hiltCompiler)

    // Firebase
    implementation(platform(Dependencies.firebaseBom))
    implementation(Dependencies.firebaseFirestore)
    implementation(Dependencies.firebaseMessaging)
    implementation(Dependencies.firebaseAnalytics)

    // Coroutines
    implementation(Dependencies.coroutinesCore)
    implementation(Dependencies.coroutinesAndroid)

    // Debug
    debugImplementation(Dependencies.composeUITooling)
    debugImplementation(Dependencies.composeUITestManifest)

    // Testing
    testImplementation(Dependencies.junit)
    androidTestImplementation(Dependencies.junitExt)
    androidTestImplementation(Dependencies.espresso)
    androidTestImplementation(Dependencies.composeUITest)
    androidTestImplementation(Dependencies.hiltTesting)
    kaptAndroidTest(Dependencies.hiltCompiler)
}

kapt {
    correctErrorTypes = true
}
