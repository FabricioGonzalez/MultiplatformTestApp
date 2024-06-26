import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.realm)
    alias(libs.plugins.apollo3)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"

            }
        }
    }

    apollo {
        service("service") {
            packageName.set("graphql")
            srcDir("src/commonMain/kotlin/graphql")
            this.generateKotlinModels.set(true)
        }
    }
    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenModel)
            implementation(libs.voyager.transitions)
            implementation(libs.voyager.koin)
            implementation(libs.koin.core)
            implementation(libs.realm.base)
            implementation(libs.realm.sync)
            implementation(libs.kotlinx.coroutines)
            implementation(libs.koin.android)

        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.uiUtil)
            implementation(compose.uiTooling)
            implementation(libs.androidx.browser)
            implementation(libs.material3.windowsclasssize)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenModel)
            implementation(libs.voyager.transitions)
            implementation(libs.composeImageLoader)
            implementation(libs.voyager.koin)
            api(libs.webview)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.realm.base)
            implementation(libs.realm.sync)
            implementation(libs.kotlinx.coroutines)
            implementation(libs.apollo.client.runtime)
            implementation(libs.apollo.client.adapters)
            implementation(libs.paging.core)
            implementation(libs.paging.compose)
            implementation(libs.materialKolor)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.common)
            implementation(compose.desktop.currentOs)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenModel)
            implementation(libs.voyager.transitions)
            implementation(libs.voyager.koin)
            implementation(libs.koin.core)
            implementation(libs.realm.base)
            implementation(libs.realm.sync)
            implementation(libs.kotlinx.coroutines)
            implementation(libs.jewel)
            implementation(libs.jewel.decorated)
            implementation(libs.jna)
        }
    }
}

android {
    namespace = "com.dev.fabricio.gonzalez.mediaapp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.dev.fabricio.gonzalez.mediaapp"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }
    buildToolsVersion = "34.0.0"
    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }
}

compose.desktop {
    application {
        jvmArgs("--add-opens", "java.desktop/sun.awt=ALL-UNNAMED")
        jvmArgs(
            "--add-opens",
            "java.desktop/java.awt.peer=ALL-UNNAMED"
        ) // recommended but not necessary

        if (System.getProperty("os.name").contains("Mac")) {
            jvmArgs("--add-opens", "java.desktop/sun.lwawt=ALL-UNNAMED")
            jvmArgs("--add-opens", "java.desktop/sun.lwawt.macosx=ALL-UNNAMED")
        }

        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.dev.fabricio.gonzalez.mediaapp"
            packageVersion = "1.0.0"
        }
    }
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
    jvmTargetValidationMode.set(org.jetbrains.kotlin.gradle.dsl.jvm.JvmTargetValidationMode.WARNING)
}