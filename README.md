# helsearbeidsgiver-arbeidsgiver-notifikasjon-klient

Klient for å lage oppgaver, saker og notifikasjoner mot [arbeidsgiver-notifikasjon-api](https://navikt.github.io/arbeidsgiver-notifikasjon-produsent-api/).

### Klienten kan brukes slik

```kt
fun main() {
    val url = URL("https://notifikasjon-fake-produsent-api.labs.nais.io/")
    val accessTokenProvider = OAuth2TokenProvider()

    val arbeidsgiverNotifikasjonKlient = ArbeidsgiverNotifikasjonKlient(url, accessTokenProvider::getToken)

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

```kts
repositories {
    mavenLocal()
    mavenCentral()
}
```


### Henvendelser

Spørsmål knyttet til koden kan rettes mot <helsearbeidsgiver@nav.no>.

### For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen #helse-arbeidsgiver.
