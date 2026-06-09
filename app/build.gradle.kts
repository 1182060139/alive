plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.example.dontstarveclone"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.dontstarveclone"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("androidx.core:core-ktx:1.12.0")
}