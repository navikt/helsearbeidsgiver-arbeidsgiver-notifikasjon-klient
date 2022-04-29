# helsearbeidsgiver-arbeidsgiver-notifikasjon-klient

Klient for å lage oppgaver, saker og notifikasjoner mot [arbeidsgiver-notifikasjon-api](https://navikt.github.io/arbeidsgiver-notifikasjon-produsent-api/).

### Bruk av helsearbeidsgiver-arbeidsgiver-notifikasjon-klient

***gradle.build.kts***
```
dependencies {
  implementation("no.nav.helsearbeidsgiver.helsearbeidsgiver-arbeidsgiver-notifikasjon-klient:${Versions.arbeidsgiverNotifikasjonKlient}")
  implementation("no.nav.helsearbeidsgiver.access-token-provider:${Versions.arbeidsgiverNotifikasjonKlient}")
  implementation("io.ktor:ktor-client-core:${Versions.ktor}")
  implementation("io.ktor:ktor-client-json:${Versions.ktor}")
  implementation("io.ktor:ktor-client-serialization:${Versions.ktor}")
}
```

### Klienten instansieres slik

```kt
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import no.nav.helsearbeidsgiver.ArbeidsgiverNotifikasjonKlient
import no.nav.helsearbeidsgiver.AccessTokenProvider.OAuth2Provider

fun main () {
    val url = URL("https://notifikasjon-fake-produsent-api.labs.nais.io/")
    val httpClient = HttpClient() { install(JsonFeature) }
    val accessTokenProvider = OAuth2Provider()

    val arbeidsgiverNotifikasjonKlient = runBlocking {
        ArbeidsgiverNotifikasjonKlient(url, accessTokenProvider, httpClient)
    }
    val result = arbeidsgiverNotifikasjonKlient.whoami()
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
