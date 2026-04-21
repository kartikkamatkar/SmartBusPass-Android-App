plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.smartbus.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.smartbus.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        manifestPlaceholders["MAPS_API_KEY"] = (project.findProperty("MAPS_API_KEY") as? String) ?: ""

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
    
    // Modern Java toolchain to resolve jlink/JDK errors
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.android.gms:play-services-maps:19.2.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("org.osmdroid:osmdroid-android:6.1.18")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}