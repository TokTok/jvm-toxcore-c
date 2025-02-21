plugins {
    kotlin("jvm") version "2.1.10"
    id("com.google.protobuf") version "0.9.4"

    // Apply the java-library plugin for API and implementation separation.
    `java-library`

    // https://docs.gradle.org/current/userguide/publishing_maven.html
    `maven-publish`
}

group = "org.toktok"
version = "0.3.0"

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

val protobufVersion = "4.29.3"

dependencies {
    implementation("com.google.protobuf:protobuf-java:$protobufVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use Kotlin Test test framework
            useKotlinTest("2.1.10")
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "tox4j-c"
            from(components["java"])
        }
    }
}
