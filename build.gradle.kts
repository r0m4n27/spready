import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.10"

    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
    id("org.openjfx.javafxplugin") version "0.0.9"

    id("application")
    id("idea")
}
group = "me.roman"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "14"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

application {
    mainClass.set("spready.ui.SpreadyApp")

    applicationDefaultJvmArgs = listOf(
        "--add-exports=javafx.base/com.sun.javafx.event=ALL-UNNAMED",
        "--add-exports=javafx.controls" +
            "/com.sun.javafx.scene.control.behavior=ALL-UNNAMED"
    )
}

val jUnitVersion = "5.7.0"

javafx {
    version = "14"
    modules("javafx.controls", "javafx.graphics", "javafx.base", "javafx.swing")
}

kotlin {
    sourceSets {
        val main by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
                implementation("no.tornado:tornadofx:1.7.20")
                implementation("org.controlsfx:controlsfx:11.1.0-SNAPSHOT")

                // Some dependency uses version 1.3 so it has to be overwritten
                implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.30")
            }
        }

        val test by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(kotlin("test-junit5"))
                implementation("org.junit.jupiter:junit-jupiter-engine:$jUnitVersion")
                implementation("org.junit.jupiter:junit-jupiter-params:$jUnitVersion")
            }
        }
    }
}
