plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.share"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.share"
        minSdk = 33
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
    implementation ("androidx.activity:activity-ktx:1.8.0")// 或其他最新版本
    implementation ("androidx.fragment:fragment-ktx:1.6.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.1")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.github.bumptech.glide:glide:4.14.2")
    implementation(libs.recyclerview)
    annotationProcessor ("com.github.bumptech.glide:compiler:4.14.2")
// 网络请求框架 okhttp3
      implementation ("com.squareup.okhttp3:okhttp:4.9.3")
     //用来解析json串

      implementation ("com.google.code.gson:gson:2.9.1")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}