import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.4.10"
  id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
  idea
}
group = "me.roman"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

tasks.withType<KotlinCompile>() {
  kotlinOptions.jvmTarget = "1.8"
}
