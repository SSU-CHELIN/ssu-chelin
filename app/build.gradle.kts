plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.ssuchelin"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ssuchelin"
        minSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}




dependencies {

    implementation(libs.material)

    implementation ("com.google.firebase:firebase-storage:20.0.1")
    implementation ("com.google.firebase:firebase-auth:21.0.1")
    implementation(libs.firebase.storage)
    implementation("com.google.firebase:firebase-database:20.0.4")
    implementation("com.google.gms:google-services:4.3.10")

    // Glide
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0");

    // Material Design
    implementation ("com.google.android.material:material:1.9.0")

    // AndroidX 라이브러리
    implementation(libs.appcompat)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // 테스트 라이브러리
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}