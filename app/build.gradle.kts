plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.synngate.twowaysync"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.synngate.twowaysync"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // Или последняя стабильная версия
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Gson конвертер для Retrofit
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.5.3")

    // DataStore Preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Room dependencies
    implementation("androidx.room:room-runtime:2.6.1") // Замените "2.6.1" на последнюю стабильную версию Room
    kapt("androidx.room:room-compiler:2.6.1")         // kapt для Kotlin annotation processing (для Room)
    implementation("androidx.room:room-ktx:2.6.1")     // Kotlin extensions for coroutines and Flow (опционально, но полезно)

    // Lifecycle для Room (если вы используете LiveData или ViewModel с Room) - опционально, но часто используется
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2") // Замените "2.6.2" на последнюю стабильную версию Lifecycle
    implementation("androidx.lifecycle:lifecycle-common-java8:2.6.2")  // для поддержки Java 8

    // Paging support for Room (если вам нужна постраничная загрузка данных из Room) - опционально
    // implementation("androidx.room:room-paging:2.6.1") // Замените "2.6.1" на последнюю версию, если нужно Paging

    // Testing support for Room (если вы будете писать тесты для Room) - опционально, для тестов
    testImplementation("androidx.room:room-testing:2.6.1")   // Замените "2.6.1" на последнюю версию, если нужны тесты


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}