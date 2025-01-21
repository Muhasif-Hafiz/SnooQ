plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("androidx.navigation.safeargs.kotlin")
    kotlin("plugin.serialization") version "2.0.21"


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


}