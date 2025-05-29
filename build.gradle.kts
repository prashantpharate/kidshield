plugins {
    id("com.android.application")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.kidshield.childapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kidshield.childapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "11"
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

    buildFeatures {
        compose = true
    }
}


dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.androidx.constraintlayout)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    // 1. Add the Firebase BoM - REPLACE 33.0.0 WITH THE LATEST BOM VERSION
    implementation(platform(libs.firebase.bom))

    implementation(libs.kotlinx.coroutines.play.services)

    // 2. Declare Firebase dependencies WITHOUT versions
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)

    // implementation(libs.firebase.messaging.ktx) // This will also be managed by the BoM
    // implementation(libs.firebase.database.ktx) // This will also be managed by the BoM
    // If libs.firebase.messaging.ktx and libs.firebase.database.ktx are defined in your
    // libs.versions.toml without versions (relying on the BoM), that's fine.
    // Otherwise, if they specify versions, you might need to adjust them or use the string notation above.
    // For simplicity and to ensure BoM control, using the string notation as above for
    // firebase-messaging and firebase-database (if you also use its non-ktx version) is safest.
    // If you only use the KTX versions and they are defined in libs.versions.toml to respect the BoM,
    // then your existing libs.firebase.messaging.ktx and libs.firebase.database.ktx might be okay.
    // Let's assume for now you want the BoM to control all Firebase versions:
    implementation(libs.google.firebase.messaging.ktx)
    implementation(libs.google.firebase.database.ktx)
    implementation(libs.play.services.base)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.3")
}