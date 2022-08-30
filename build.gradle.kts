import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "no.nav.helsearbeidsgiver"
version = "0.1.9"

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.expediagroup.graphql")
    id("maven-publish")
    id("org.jmailen.kotlinter")
}

repositories {
    val githubPassword: String by project

    mavenCentral()
    maven {
        credentials {
            username = "x-access-token"
            password = System.getenv("GITHUB_TOKEN") ?: githubPassword
        }
        setUrl("https://maven.pkg.github.com/navikt/*")
    }
}

dependencies {
    val coroutinesVersion: String by project
    val graphQLKotlinVersion: String by project
    val ktorVersion: String by project
    val logbackVersion: String by project
    val slf4jVersion: String by project

    api("com.expediagroup:graphql-kotlin-client:$graphQLKotlinVersion")
    api("io.ktor:ktor-client-core:$ktorVersion")

    implementation("com.expediagroup:graphql-kotlin-ktor-client:$graphQLKotlinVersion")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")

    runtimeOnly("ch.qos.logback:logback-classic:$logbackVersion")

    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
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
