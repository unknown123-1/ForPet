plugins {
    `kotlin-dsl`
}

group = "com.forpet.app.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.compiler.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "forpet.android.application"
            implementationClass = "com.forpet.app.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "forpet.android.library"
            implementationClass = "com.forpet.app.AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "forpet.android.compose"
            implementationClass = "com.forpet.app.AndroidComposeConventionPlugin"
        }
        register("hilt") {
            id = "forpet.hilt"
            implementationClass = "com.forpet.app.HiltConventionPlugin"
        }
        register("androidRoom") {
            id = "forpet.android.room"
            implementationClass = "com.forpet.app.AndroidRoomConventionPlugin"
        }
    }
}

