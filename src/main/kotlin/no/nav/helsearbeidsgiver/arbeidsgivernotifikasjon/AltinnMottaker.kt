package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

/**
 * Representerer altinn mottakere.
 */
sealed interface AltinnMottaker {
    /**
     * Altinn 2 service code (deprecated, vil forsvinne innen juni 2026)
     */
    @Deprecated("Altinn 2 tjeneste koder vil forsvinne innen juni 2026")
    data class Altinn2(
        val serviceCode: String,
        val serviceEdition: String,
    ) : AltinnMottaker

    /**
     * Altinn 3 ressurs
     */
    data class Altinn3(
        val ressurs: Altinn3Ressurs,
    ) : AltinnMottaker
}
