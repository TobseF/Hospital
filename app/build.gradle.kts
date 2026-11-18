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
        // CI sets these, so every build can be installed over the previous one
        versionCode = System.getenv("VERSION_CODE")?.toIntOrNull() ?: 1
        versionName = System.getenv("VERSION_NAME") ?: "2.0"
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
        // The release key is not part of the repository. CI passes it in via environment variables
        // (see README, "Android release"). Without them, the release build falls back to the debug key.
        val releaseKeystore = System.getenv("RELEASE_KEYSTORE")
        if (releaseKeystore != null) {
            create("release") {
                storeFile = file(releaseKeystore)
                storePassword = System.getenv("RELEASE_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("RELEASE_KEY_ALIAS")
                keyPassword = System.getenv("RELEASE_KEY_PASSWORD")?.takeIf { it.isNotEmpty() }
                    ?: System.getenv("RELEASE_KEYSTORE_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            signingConfig = signingConfigs.findByName("release") ?: signingConfigs.getByName("debug")
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
