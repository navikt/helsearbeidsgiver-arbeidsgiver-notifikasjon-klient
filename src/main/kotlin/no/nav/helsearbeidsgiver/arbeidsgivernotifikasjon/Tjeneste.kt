package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

enum class Tjeneste(
    val merkelapp: String,
    val ressurs: Altinn3Ressurs,
) {
    SYKMELDING("Sykmelding", Altinn3Ressurs.SYKMELDING),
    SOEKNAD("Søknad om sykepenger", Altinn3Ressurs.SOEKNAD),
    FRITAKAGP("Fritak arbeidsgiverperiode", Altinn3Ressurs.FRITAKAGP),
    INNTEKTSMELDING("Inntektsmelding sykepenger", Altinn3Ressurs.INNTEKTSMELDING),
}
