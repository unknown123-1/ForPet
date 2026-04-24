package com.forpet.app

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Configure base Kotlin with Android options
 */
internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        compileSdk = 36

        defaultConfig {
            minSdk = 26
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
            // isCoreLibraryDesugaringEnabled = true // 필요시 활성화
        }
    }

    configureKotlin()
    
    // Desugaring이 필요하다면 여기서 dependencies 추가
    // dependencies {
    //     add("coreLibraryDesugaring", libs.findLibrary("android.desugarJdkLibs").get())
    // }
}

/**
 * Configure base Kotlin options
 */
private fun Project.configureKotlin() {
    // Use withType to workaround https://youtrack.jetbrains.com/issue/KT-55947
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            // Treat all Kotlin warnings as errors (disabled by default)
            // allWarningsAsErrors.set(true)
            freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
            // Enable experimental coroutines APIs, including Flow
            freeCompilerArgs.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
            freeCompilerArgs.add("-opt-in=androidx.compose.material3.ExperimentalMaterial3Api")
        }
    }
}
