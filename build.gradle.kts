import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.net.InetAddress

application.mainClass = "com.tamdao.webapp.ApplicationKt"

plugins {
    kotlin("jvm") version "1.9.21"
    id("com.github.johnrengelman.shadow") version "8.1.1" // Update to the latest Shadow plugin version
    application
}

group = "com.tamdao.webapp"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(platform("io.dropwizard:dropwizard-bom:4.0.5"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.dropwizard:dropwizard-core")

    // Database
    implementation("io.dropwizard:dropwizard-jdbi3")
    implementation("io.dropwizard.modules:dropwizard-flyway:4.0.0-4")
    implementation("org.postgresql:postgresql:42.7.1")

    // Swagger
    implementation("com.smoketurner:dropwizard-swagger:4.0.5-1")

    implementation("com.fasterxml.jackson.core:jackson-annotations:2.16.1")

    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.9.3")
}

tasks {
    compileJava {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "21"
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "21"
    }

    named<JavaExec>("run") {
        args = listOf("server", "config.yml")
    }
}
tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("shadow")
        mergeServiceFiles()
        exclude("META-INF/*.DSA", "META-INF/*.RSA", "META-INF/*.SF")
        manifest {
            attributes(mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Implementation-Vendor-Id" to project.group,
                "Built-By" to InetAddress.getLocalHost().hostName,
                "Created-By" to "Gradle " + gradle.gradleVersion,
                "Main-Class" to "com.tamdao.webapp.ApplicationKt"))
        }
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}
