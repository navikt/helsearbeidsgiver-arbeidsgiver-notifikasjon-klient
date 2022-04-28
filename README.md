# helsearbeidsgiver-arbeidsgiver-notifikasjon-klient

Klient for å lage oppgaver, saker og notifikasjoner mot [arbeidsgiver-notifikasjon-api](https://navikt.github.io/arbeidsgiver-notifikasjon-produsent-api/).

### Setup

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

### Usage

```kt
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import no.nav.helsearbeidsgiver.ArbeidsgiverNotifikasjonKlient
import no.nav.helsearbeidsgiver.AccessTokenProvider.OAuth2Provider

suspend fun main () {
    val url = URL("https://notifikasjon-fake-produsent-api.labs.nais.io/")
    val httpClient = HttpClient() { install(JsonFeature) }
    val accessTokenProvider = OAuth2Provider()

    val arbeidsgiverNotifikasjonKlient = ArbeidsgiverNotifikasjonKlient(url, accessTokenProvider, httpClient)
    val result = arbeidsgiverNotifikasjonKlient.whoami()
    println(result)
}
```
