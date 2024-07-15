import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.realm)
    alias(libs.plugins.apollo3)
    alias(libs.plugins.conveyor)
}

group = "com.dev.fabricio.gonzalez"

version = libs.versions.systemVersion.get()


kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"


            }
        }
    }
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of("17"))
        vendor.set(JvmVendorSpec.JETBRAINS)
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
        iosX64(), iosArm64(), iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "MediaApp"
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
            implementation(libs.voyager.tabNavigation)
            implementation(libs.composeImageLoader)
            implementation(libs.voyager.koin)
            api(libs.webview)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kermit)
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

dependencies {

    linuxAmd64(compose.desktop.linux_x64)
    macAmd64(compose.desktop.macos_x64)
    macAarch64(compose.desktop.macos_arm64)
    windowsAmd64(compose.desktop.windows_x64)
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
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.systemVersion.get()
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
        version = libs.versions.systemVersion.get()
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com-dev-fabricio-gonzalez-mediaapp"
            packageVersion = libs.versions.systemVersion.get()
        }
    }
}
// region Work around temporary Compose bugs.
configurations.all {
    attributes {
        // https://github.com/JetBrains/compose-jb/issues/1404#issuecomment-1146894731
        attribute(Attribute.of("ui", String::class.java), "awt")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
    jvmTargetValidationMode.set(org.jetbrains.kotlin.gradle.dsl.jvm.JvmTargetValidationMode.WARNING)
}