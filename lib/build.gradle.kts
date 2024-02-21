plugins {
    kotlin("multiplatform") version "1.9.22"
    id("com.google.protobuf") version "0.9.4"

    // Apply the java-library plugin for API and implementation separation.
    `java-library`
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

sourceSets["main"].proto {
    srcDir("src/jvmMain/proto")
    dependencies {
        implementation("com.google.protobuf:protobuf-java:3.24.4")
    }
}

kotlin {
    jvm()
//  linuxX64()

    sourceSets {
        val jvmMain by getting {
            kotlin.srcDir("${project.layout.buildDirectory}/generated/source/proto/main/java")
            dependencies {
                implementation("com.google.protobuf:protobuf-java:3.24.4")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.22")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
            }
        }
    }
}

tasks["compileKotlinJvm"].dependsOn("generateProto")

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use Kotlin Test test framework
            useKotlinTest("1.9.22")
        }
    }
}
