import org.jetbrains.kotlin.gradle.dsl.JvmTarget

group = "no.nav.helsearbeidsgiver"
version = "2.7.0"

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.expediagroup.graphql")
    id("maven-publish")
    id("org.jmailen.kotlinter")
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
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

java {
    withSourcesJar()
}

graphql {
    client {
        packageName = "no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated"
        // uncomment if you need schema to be local
        // schemaFile = File("src/main/resources/arbeidsgivernotifikasjon/Schema.graphql")
        endpoint = "https://notifikasjon-fake-produsent-api.ekstern.dev.nav.no"
        queryFiles = file("src/main/resources/arbeidsgivernotifikasjon").listFiles()?.toList().orEmpty()
        serializer = com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer.KOTLINX
    }
}

repositories {
    mavenCentral()
    mavenNav("*")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        mavenNav("helsearbeidsgiver-${rootProject.name}")
    }
}

dependencies {
    val coroutinesVersion: String by project
    val graphQLKotlinVersion: String by project
    val ktorVersion: String by project
    val logbackVersion: String by project
    val mockkVersion: String by project
    val slf4jVersion: String by project
    val utilsVersion: String by project

    implementation("com.expediagroup:graphql-kotlin-ktor-client:$graphQLKotlinVersion")
    implementation("io.ktor:ktor-client-apache5:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("no.nav.helsearbeidsgiver:utils:$utilsVersion")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")

    testImplementation(kotlin("test"))
    testImplementation(testFixtures("no.nav.helsearbeidsgiver:utils:$utilsVersion"))
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

    testRuntimeOnly("ch.qos.logback:logback-classic:$logbackVersion")
}

fun RepositoryHandler.mavenNav(repo: String): MavenArtifactRepository {
    val githubPassword: String by project

    return maven {
        setUrl("https://maven.pkg.github.com/navikt/$repo")
        credentials {
            username = "x-access-token"
            password = githubPassword
        }
    }
}
