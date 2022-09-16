package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.ID
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.NyStatusSak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussak.Konflikt
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussak.NyStatusSakVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussak.SakFinnesIkke

suspend fun ArbeidsgiverNotifikasjonKlient.nyStatusSak(id: String, nyLenkeTilSak: String): ID {
    logger.info("Forsøker å sette ny status for sak $id")

    val query = NyStatusSak(
        variables = NyStatusSak.Variables(
            id,
            nyLenkeTilSak
        )
    )

    val resultat = execute(query)

    val nyStatusSak = resultat.data?.nyStatusSak

    if (nyStatusSak !is NyStatusSakVellykket) {
        when (nyStatusSak) {
            is SakFinnesIkke -> {
                logger.error("Feilmelding ${nyStatusSak.feilmelding}")
                throw NyStatusSakException(id, nyStatusSak.feilmelding)
            }
            is Konflikt -> {
                logger.error("Feilmelding ${nyStatusSak.feilmelding}")
                throw NyStatusSakException(id, nyStatusSak.feilmelding)
            }
            else -> {
                logger.error("Kunne ikke opprette ny sak: $nyStatusSak")
                throw NyStatusSakException(id, "ukjent feil: ${resultat.errors}")
            }
        }
    }
    logger.info("Satt ny status for sak ${nyStatusSak.id}")
    return nyStatusSak.id
}

class NyStatusSakException(id: String, feilmelding: String?) :
    Exception("Ny status for sak $id arbeidsgiver-notifikasjon-api feilet med: $feilmelding")
