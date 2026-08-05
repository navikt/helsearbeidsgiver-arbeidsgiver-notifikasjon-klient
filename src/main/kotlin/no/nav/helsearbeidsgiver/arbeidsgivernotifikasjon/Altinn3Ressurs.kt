package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

enum class Altinn3Ressurs(
    val value: String,
) {
    INNTEKTSMELDING("nav_sykepenger_inntektsmelding"),
    SOEKNAD("nav_sykepenger_soeknad"),
    SYKMELDING("nav_sykepenger_sykmelding"),
    FRITAKAGP("nav_sykepenger_fritak-arbeidsgiverperiode"),
}
