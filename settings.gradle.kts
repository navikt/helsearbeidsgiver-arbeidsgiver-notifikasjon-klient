rootProject.name = "arbeidsgiver-notifikasjon-klient"

pluginManagement {
    plugins {
        val graphQLKotlinVersion: String by settings
        val kotestVersion: String by settings
        val kotlinVersion: String by settings
        val kotlinterVersion: String by settings

        kotlin("jvm") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        id("com.expediagroup.graphql") version graphQLKotlinVersion
        id("io.kotest") version kotestVersion
        id("org.jmailen.kotlinter") version kotlinterVersion
    }
}
