// app/build.gradle.kts
import java.util.Properties
import com.android.build.api.dsl.ApplicationExtension

fun loadLocalProperties(): Properties {
    val properties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")

    if (localPropertiesFile.exists()) {
        properties.load(localPropertiesFile.inputStream())
    } else {
        throw GradleException("local.properties file not found in project root")
    }
    return properties
}

val localProperties = loadLocalProperties()
val apiKey: String = localProperties.getProperty("API_KEY")?.trim() ?: throw GradleException("apiKey not found")

plugins {
    alias(libs.plugins.android.application)
}

android {
    android.buildFeatures.buildConfig = true
    namespace = "com.example.api4tp"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.api4tp"
        minSdk = 28
        targetSdk = 36
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
            buildConfigField("String", "API_KEY", "\"${apiKey}\"")
        }

        debug {
            isDebuggable = true
            buildConfigField("String", "API_KEY", "\"${apiKey}\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.cardview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("org.osmdroid:osmdroid-android:6.1.20")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
}