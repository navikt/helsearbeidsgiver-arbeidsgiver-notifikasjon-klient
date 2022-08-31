package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.ID
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.SoftDeleteSak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.softdeletesak.SakFinnesIkke
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.softdeletesak.SoftDeleteSakVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.softdeletesak.UgyldigMerkelapp
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.softdeletesak.UkjentProdusent

suspend fun ArbeidsgiverNotifikasjonKlient.softDeleteSak(id: String): ID {
    logger.info("Forsøker å slette sak $id")

    val query = SoftDeleteSak(
        variables = SoftDeleteSak.Variables(
            id
        )
    )

    val resultat = execute(query)

    val deleteSak = resultat.data?.softDeleteSak
    if (deleteSak !is SoftDeleteSakVellykket) {
        when (deleteSak) {
            is UgyldigMerkelapp -> {
                logger.error("Feilmelding ${deleteSak.feilmelding}")
                throw SoftDeleteSakException(id, deleteSak.feilmelding)
            }
            is SakFinnesIkke -> {
                logger.error("Feilmelding ${deleteSak.feilmelding}")
                throw SoftDeleteSakException(id, deleteSak.feilmelding)
            }
            is UkjentProdusent -> {
                logger.error("Feilmelding ${deleteSak.feilmelding}")
                throw SoftDeleteSakException(id, deleteSak.feilmelding)
            }
            else -> {
                logger.error("Kunne ikke softdelete sak", deleteSak)
                throw SoftDeleteSakException(id, "ukjent feil: ${resultat.errors}")
            }
        }
    }
    logger.info("Slettet sak ${deleteSak.id}")
    return deleteSak.id
}

class SoftDeleteSakException(id: String, feilmelding: String?) :
    Exception("Sletting av sak $id mot arbeidsgiver-notifikasjon-api feilet: $feilmelding")
