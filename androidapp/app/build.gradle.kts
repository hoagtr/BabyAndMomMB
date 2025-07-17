plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.prm392mnlv"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.prm392mnlv"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.navigation.fragment.ktx) // Or the latest version
    implementation(libs.navigation.ui.ktx)
    implementation (libs.navigation.compose)
    implementation(libs.core)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.recyclerview.swipedecorator)
    implementation(libs.retrofit.v2110)
    implementation(libs.converter.moshi)
    implementation(libs.moshi.adapters)
    implementation(libs.datastore.preferences)
    implementation(libs.datastore.preferences.rxjava3)
    implementation(libs.datastore.preferences.core.jvm)
    implementation(libs.rxandroid)
    implementation(files("src/main/libs/zpdk-release-v3.1.aar"))
    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("me.leolin:ShortcutBadger:1.1.22@aar")
    implementation("com.github.vietmap-company:maps-sdk-plugin-localization-android:2.0.0")
    implementation("com.github.vietmap-company:vietmap-services-geojson-android:1.0.0")
    implementation("com.github.vietmap-company:vietmap-services-turf-android:1.0.2")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.github.vietmap-company:maps-sdk-navigation-android:2.3.3")
    implementation("com.github.vietmap-company:maps-sdk-navigation-ui-android:2.3.2")
    implementation("com.github.vietmap-company:vietmap-services-core:1.0.0")
    implementation("com.github.vietmap-company:vietmap-services-directions-models:1.0.1")
    implementation("com.github.vietmap-company:vietmap-services-android:1.1.2")
    implementation ("com.google.android.gms:play-services-location:21.0.1")

    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation ("androidx.cardview:cardview:1,0,0")
    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation ("com.jakewharton:butterknife:10.2.3")
    implementation ("androidx.core:core-ktx:1.7.0")
    implementation ("com.github.vietmap-company:maps-sdk-android:2.6.0")
    implementation ("com.github.vietmap-company:vietmap-services-turf-android:1.0.2")
    implementation ("com.squareup.picasso:picasso:2.8")
    implementation ("com.github.vietmap-company:vietmap-services-geojson-android:1.0.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.0.0-beta4")
    implementation ("org.json:json:20210307")

    implementation("com.google.firebase:firebase-database:20.3.0")

    // Sử dụng Glide từ version catalog
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)
    implementation(libs.jwtdecode)
}
