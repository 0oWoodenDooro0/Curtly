plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(ktorLibs.plugins.ktor)
    `maven-publish`
}

group = "com.github.0owoodendooro0"
version = "1.0.0"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

kotlin {
    jvmToolchain(21)
}
dependencies {
    implementation(ktorLibs.server.callLogging)
    implementation(ktorLibs.server.config.yaml)
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.server.statusPages)
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(libs.logback.classic)

    testImplementation(kotlin("test"))
    testImplementation(ktorLibs.server.testHost)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "com.github.0owoodendooro0"
            artifactId = "curtly"
            version = "1.0.0"
        }
    }
}
