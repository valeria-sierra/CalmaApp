plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.calmaapp_proyect"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.calmaapp_proyect"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"

    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.generativeai)


    //*agregue este codigo para que me de la vista de las estadisticas*//



    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debug
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Firebase (usando BOM)
    implementation(platform(libs.firebase.bom))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation ("com.google.firebase:firebase-storage-ktx:20.0.0")
    implementation ("com.google.firebase:firebase-firestore-ktx:24.0.0")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation(platform("androidx.compose:compose-bom:2023.08.00")) // Usa tu versión actual
    implementation ("androidx.compose.runtime:runtime-livedata")
    implementation("com.google.firebase:firebase-storage-ktx")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")

    // ... otras dependencias ...
    implementation("androidx.compose.runtime:runtime-saveable:1.6.5")
    implementation ("androidx.core:core-ktx:1.10.1")
    implementation("androidx.compose.material:material-icons-extended:1.6.5")
    implementation ("androidx.compose.ui:ui:1.5.4") // o versión más reciente
    implementation("io.coil-kt:coil-compose:2.4.0") // o versión más reciente

    // Firebase BoM (Bill of Materials) para gestionar versiones
    implementation (platform("com.google.firebase:firebase-bom:32.0.0"))

    // Dependencias básicas de Firebase
    implementation ("com.google.firebase:firebase-auth-ktx")
    implementation ("com.google.firebase:firebase-firestore-ktx")
    implementation ("com.google.firebase:firebase-storage-ktx")

    // Firebase Analytics (opcional)
    implementation ("com.google.firebase:firebase-analytics-ktx")
}