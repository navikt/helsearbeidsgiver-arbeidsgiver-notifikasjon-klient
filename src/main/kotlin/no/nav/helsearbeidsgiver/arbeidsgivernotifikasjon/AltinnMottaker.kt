package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

/**
 * Representerer altinn mottakere.
 */
sealed class AltinnMottaker {
    /**
     * Altinn 3 ressurs
     */
    data class Altinn3(
        val ressurs: Altinn3Ressurs,
    ) : AltinnMottaker()
}
