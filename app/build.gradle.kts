plugins {
    id("com.android.application") version "8.6.0"
}

android {
    namespace = "com.example.ssuchelin"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ssuchelin"
        minSdk = 21
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
    viewBinding{
        enable = true
    }
}




dependencies {
    implementation ("androidx.core:core-ktx:1.6.0")
    implementation("com.google.firebase:firebase-auth-ktx:21.0.1")
    implementation("com.google.firebase:firebase-database-ktx:20.0.3")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("androidx.appcompat:appcompat:1.4.0")
    implementation("androidx.activity:activity-ktx:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.1")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation ("com.google.firebase:firebase-auth:21.0.1")
    implementation(libs.firebase.storage)
    implementation("com.google.firebase:firebase-database:20.0.4")
    implementation("com.google.gms:google-services:4.3.10")
    // Glide
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation(libs.firebase.common)
    implementation(libs.cardview)
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0");
    // Material Design
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("androidx.recyclerview:recyclerview:1.3.0")
    // AndroidX 라이브러리
    implementation(libs.appcompat)
    implementation(libs.activity)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.0")
    implementation ("com.google.firebase:firebase-storage:19.2.0")
    implementation ("com.github.bumptech.glide:glide:4.11.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.11.0")
    implementation("org.jsoup:jsoup:1.13.1")

    // 테스트 라이브러리
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}