plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "de.tfr.hospital"
    compileSdk = 35

    defaultConfig {
        applicationId = "de.tfr.hospital"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "2.0"
    }

    signingConfigs {
        // A fixed debug key is checked in, so that every CI build is signed with the same key
        // and a new APK can be installed over an older one without uninstalling it first.
        getByName("debug") {
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}
