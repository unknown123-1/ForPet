import java.util.Properties

plugins {
    alias(libs.plugins.forpet.android.application)
    alias(libs.plugins.forpet.android.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.forpet.hilt)
}

// local.properties에서 Google Maps API 키 읽기
val localProps = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
}
val googleMapsApiKey: String = localProps.getProperty("GOOGLE_MAPS_API_KEY", "")

android {
    namespace = "com.forpet.app"

    defaultConfig {
        applicationId = "com.forpet.app"
        versionCode = 1
        versionName = "1.0"

        manifestPlaceholders["googleMapsApiKey"] = googleMapsApiKey
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
}

dependencies {
    // Core Modules
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
    implementation(project(":core:model"))
    implementation(project(":core:data"))
    implementation(project(":core:background"))

    // Feature Modules
    implementation(project(":feature:home"))
    implementation(project(":feature:calendar"))
    implementation(project(":feature:mypet"))
    implementation(project(":feature:schedule"))
    implementation(project(":feature:walk"))

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Activity
    implementation(libs.androidx.activity.compose)

}
