// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.flamvisiontask"
    compileSdk = 34 // Use a common stable version

    defaultConfig {
        applicationId = "com.example.flamvisiontask"
        minSdk = 24
        targetSdk = 34 // Use a common stable version
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
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // ⭐ CONFLICT FIX: Forcing stable versions and manually excluding the conflict.

    // Material dependency with the correct syntax for exclusion
    implementation("com.google.android.material:material:1.11.0") {
        // Correct block for exclusion in Kotlin DSL
        exclude(group = "androidx.dynamicanimation")
    }

    // Forcing latest appcompat version which often conflicts
    implementation("androidx.appcompat:appcompat:1.6.1")

    // Existing core dependencies (libs.androidx.core.ktx is a dependency reference that should remain)
    implementation(libs.androidx.core.ktx)
    // Fix: Removed the now-obsolete libs.androidx.appcompat as we use the specific version above

    // ConstraintLayout dependency (use string literal)
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Existing test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // ⭐ CORE PROJECT DEPENDENCIES
    // OpenCV for image processing
    implementation("org.opencv:opencv-android:4.8.0")

    // CameraX for simple camera handling
    implementation("androidx.camera:camera-core:1.3.1")
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
}