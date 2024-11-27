plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "io.iskopasi.geoeventtesttask"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.iskopasi.geoeventtesttask"
        minSdk = 24
        targetSdk = 34
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    testImplementation(libs.androidx.core)
    testImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Viewmodel extensions for Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // Gson
    implementation(libs.gson)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)

    // Location
    implementation(libs.play.services.location)
    implementation(libs.accompanist.permissions)


    testImplementation(libs.core.ktx)

    // Required -- JUnit 4 framework
    testImplementation ("junit:junit:4.13.2")
    // Optional -- Robolectric environment
    testImplementation ("androidx.test:core:1.5.0")
    // Optional -- Mockito framework
    testImplementation ("org.mockito:mockito-core:5.14.2")
    // Optional -- mockito-kotlin
    testImplementation ("org.mockito.kotlin:mockito-kotlin:5.4.0")
    // Optional -- Mockk framework
    testImplementation ("io.mockk:mockk:1.13.13")
}