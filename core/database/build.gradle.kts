plugins {
    alias(libs.plugins.forpet.android.library)
    alias(libs.plugins.forpet.hilt)
    alias(libs.plugins.forpet.android.room)
}

android {
    namespace = "com.forpet.app.core.database"
}

dependencies {
    api(project(":core:model"))
    implementation(libs.kotlinx.coroutines.android)
}
