import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

object Versions {
    const val graphQLKotlin = "5.3.2"
    const val ktor = "1.6.0"
    const val logback = "1.2.11"
    const val logstash = "7.1.1"
    const val kotlin = "1.6.21"
}

val githubPassword: String by project

plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21"
    id("maven-publish")
    id("com.expediagroup.graphql") version "5.3.2"
    id("org.jmailen.kotlinter") version "3.10.0"
}

group = "no.nav.helsearbeidsgiver"
project.version = "0.1.5"

repositories {
    mavenCentral()
    maven {
        credentials {
            username = "x-access-token"
            password = githubPassword
        }
        setUrl("https://maven.pkg.github.com/navikt/*")
    }
}

dependencies {
    implementation("com.expediagroup:graphql-kotlin-ktor-client:${Versions.graphQLKotlin}")
    implementation("no.nav.helsearbeidsgiver:helse-arbeidsgiver-felles-backend:2022.01.18-08-47-f6aa0")

    runtimeOnly("ch.qos.logback:logback-classic:${Versions.logback}")

    testImplementation(kotlin("test"))
    testImplementation("net.logstash.logback:logstash-logback-encoder:${Versions.logstash}")
    testImplementation("io.ktor:ktor-client-core:${Versions.ktor}")
    testImplementation("io.ktor:ktor-client-json:${Versions.ktor}")
    testImplementation("io.ktor:ktor-client-serialization:${Versions.ktor}")
    testImplementation("io.ktor:ktor-client-mock:${Versions.ktor}")
}

tasks {
    test {
        useJUnitPlatform()
    }
    lintKotlinMain {
        exclude("no/nav/helsearbeidsgiver/arbeidsgivernotifkasjon/graphql/generated/**/*.kt")
    }
    formatKotlinMain {
        exclude("no/nav/helsearbeidsgiver/arbeidsgivernotifkasjon/graphql/generated/**/*.kt")
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

graphql {
    client {
        packageName = "no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated"
        // uncomment if you need schema to be local
        // schemaFile = File("src/main/resources/arbeidsgivernotifikasjon/Schema.graphql")
        endpoint = "https://notifikasjon-fake-produsent-api.labs.nais.io/"
        queryFiles = file("src/main/resources/arbeidsgivernotifikasjon").listFiles().toList()
        serializer = com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer.KOTLINX
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/navikt/${rootProject.name}")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
