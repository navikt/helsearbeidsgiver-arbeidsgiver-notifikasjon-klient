import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
    id("com.expediagroup.graphql") version "5.3.2"
    id("org.jmailen.kotlinter") version "3.10.0"
}

group = "no.nav.helsearbeidsgiver"
version = "1.0"

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
    withType<KotlinCompile>() {
        kotlinOptions.jvmTarget = "1.8"
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
        endpoint = "https://notifikasjon-fake-produsent-api.labs.nais.io/"
        queryFiles = file("src/main/resources/arbeidsgivernotifikasjon").listFiles().toList()
        serializer = com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer.KOTLINX
    }
}
