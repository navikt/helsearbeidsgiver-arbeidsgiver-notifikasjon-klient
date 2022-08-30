# helsearbeidsgiver-arbeidsgiver-notifikasjon-klient

Klient for å lage oppgaver, saker og notifikasjoner mot [arbeidsgiver-notifikasjon-api](https://navikt.github.io/arbeidsgiver-notifikasjon-produsent-api/).

### Bruk av helsearbeidsgiver-arbeidsgiver-notifikasjon-klient

***gradle.build.kts***
```kts
dependencies {
    implementation("io.ktor:ktor-client-content-negotiation:${Versions.ktor}")
    implementation("io.ktor:ktor-client-core:${Versions.ktor}")
    implementation("no.nav.helsearbeidsgiver.helsearbeidsgiver-arbeidsgiver-notifikasjon-klient:${Versions.arbeidsgiverNotifikasjonKlient}")
    implementation("no.nav.helsearbeidsgiver:helse-arbeidsgiver-felles-backend:${Versions.fellesBackend}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}")
}
```

### Klienten instansieres slik

```kt
import kotlinx.coroutines.runBlocking
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import no.nav.helse.arbeidsgiver.integrasjoner.OAuth2TokenProvider
import no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon.ArbeidsgiverNotifikasjonKlient
import java.net.URL

fun main() {
    val url = URL("https://notifikasjon-fake-produsent-api.labs.nais.io/")
    val httpClient = HttpClient { install(ContentNegotiation) }
    val accessTokenProvider = OAuth2TokenProvider()

    val arbeidsgiverNotifikasjonKlient = ArbeidsgiverNotifikasjonKlient(url, httpClient) {
        accessTokenProvider.getToken()
    }
    val result = runBlocking { arbeidsgiverNotifikasjonKlient.whoami() }
    println(result)
}
```

### Lokal utvikling

For å teste klienten-endringer i en annen applikasjon uten å publisere remote, kjør:

```sh
./gradlew publishToMavenLocal
```

Pakken blir da publisert til lokalt repository, husk at du må legge til `mavenLocal()` i applikasjonen:

```dsl
repositories {
    mavenLocal()
    mavenCentral()
}
```


### Henvendelser

Spørsmål knyttet til koden eller prosjektet kan rettes mot:

* Jonas Enge <jonas.maccyber.enge@nav.no>

### For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen #helse-arbeidsgiver.
