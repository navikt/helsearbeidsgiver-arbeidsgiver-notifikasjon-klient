object Versions {
    const val graphQLKotlin = "5.3.2"
    const val ktor = "1.6.8"
    const val logback = "1.2.11"
    const val logstash = "7.1.1"
    const val kotlin = "1.6.21"
}

plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21"
    id("maven-publish")
    id("com.expediagroup.graphql") version "5.3.2"
    id("org.jmailen.kotlinter") version "3.10.0"
    id("pl.allegro.tech.build.axion-release") version "1.13.6"
    id("org.sonarqube") version "2.8"
}

sonarqube {
    properties {
        property("sonar.projectKey", "navikt_helsearbeidsgiver-arbeidsgiver-notifikasjon-klient")
        property("sonar.organization", "navikt")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.login", System.getenv("SONAR_TOKEN"))
    }
}

group = "no.nav.helsearbeidsgiver"
project.version = scmVersion.version

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.expediagroup:graphql-kotlin-ktor-client:${Versions.graphQLKotlin}")

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
