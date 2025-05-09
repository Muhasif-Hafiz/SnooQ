plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("androidx.navigation.safeargs.kotlin")
    kotlin("plugin.serialization") version "2.0.21"
    id("com.google.gms.google-services")
   // id("kotlin-android-extensions")


}

android {
    namespace = "com.muhasib.snooq"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.muhasib.snooq"
        minSdk = 26
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
    buildFeatures{
        dataBinding=true
        viewBinding=true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}



dependencies {

    implementation("com.airbnb.android:lottie:6.6.2")
    implementation(libs.androidx.core.ktx)
   // implementation(libs.androidx.appcompat)

    implementation("androidx.core:core:1.15.0")
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    val nav_version = "2.8.5"

    // Jetpack Compose integration
    implementation("androidx.navigation:navigation-compose:$nav_version")

    // Views/Fragments integration
    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")

    // Feature module support for Fragments
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$nav_version")

    // Testing Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:$nav_version")

    // JSON serialization library, works with the Kotlin serialization plugin
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    implementation("com.tbuonomo:dotsindicator:4.3")

    //AppWrite dependency
    implementation("io.appwrite:sdk-for-android:6.0.0")
    implementation ("io.insert-koin:koin-android:3.5.0")

    implementation("com.tbuonomo:dotsindicator:4.2")

    //Navigation Drawer

    implementation("com.github.shrikanth7698:Custom-Navigation-Drawer:v0.0.1")

    //Stepper
    implementation ("com.github.shuhart:stepview:1.5.1")

    // Google play services Location dependency
    implementation("com.google.android.gms:play-services-location:21.3.0")

   implementation("com.github.bumptech.glide:glide:4.16.0")

    //Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.8.0"))

    implementation("com.google.firebase:firebase-firestore")

    implementation("com.google.android.material:material:1.11.0")


    //Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")

    //Material Design
    implementation ("com.google.android.material:material:1.10.0")

    // Pallete
    implementation("androidx.palette:palette-ktx:1.0.0")

    // Circular Image view
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //U Crop Dependency
    implementation ("com.github.yalantis:ucrop:2.2.10")
    // coil
    implementation("io.coil-kt:coil:0.13.0")

    // image cropper canHub
    implementation("com.vanniktech:android-image-cropper:4.6.0")

    implementation("com.github.chrisbanes:PhotoView:2.3.0")


    //Shimmer Effect
    implementation ("com.facebook.shimmer:shimmer:0.5.0")

}