plugins {
    kotlin("jvm") version "2.2.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

tasks {
    wrapper {
        gradleVersion = "8.5"
    }
}
