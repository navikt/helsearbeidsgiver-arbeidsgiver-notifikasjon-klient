package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.ID
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.NyStatusSakByGrupperingsid
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.enums.SaksStatus
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussakbygrupperingsid.Konflikt
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussakbygrupperingsid.NyStatusSakVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussakbygrupperingsid.SakFinnesIkke

suspend fun ArbeidsgiverNotifikasjonKlient.nyStatusSakByGrupperingsid(
    grupperingsid: String,
    merkelapp: String,
    nyStatus: SaksStatus
): ID {
    logger.info("Forsøker å sette ny status $nyStatus for grupperingsid $grupperingsid")

    val query = NyStatusSakByGrupperingsid(
        variables = NyStatusSakByGrupperingsid.Variables(
            grupperingsid,
            merkelapp,
            nyStatus
        )
    )

    val resultat = execute(query)

    val nyStatusSak = resultat.data?.nyStatusSakByGrupperingsid

    if (nyStatusSak !is NyStatusSakVellykket) {
        when (nyStatusSak) {
            is SakFinnesIkke -> {
                logger.error("Feilmelding ${nyStatusSak.feilmelding}")
                throw NySakStatusException(grupperingsid, nyStatus, nyStatusSak.feilmelding)
            }
            is Konflikt -> {
                logger.error("Feilmelding ${nyStatusSak.feilmelding}")
                throw NySakStatusException(grupperingsid, nyStatus, nyStatusSak.feilmelding)
            }
            else -> {
                logger.error("Kunne ikke opprette ny sak: $nyStatusSak")
                throw NySakStatusException(grupperingsid, nyStatus, "ukjent feil: ${resultat.errors}")
            }
        }
    }
    logger.info("Satt ny status $nyStatus for sak ${nyStatusSak.id}")
    return nyStatusSak.id
}

class NySakStatusException(id: String, status: SaksStatus, feilmelding: String?) :
    Exception("Ny status $status for sak $id arbeidsgiver-notifikasjon-api feilet med: $feilmelding")
