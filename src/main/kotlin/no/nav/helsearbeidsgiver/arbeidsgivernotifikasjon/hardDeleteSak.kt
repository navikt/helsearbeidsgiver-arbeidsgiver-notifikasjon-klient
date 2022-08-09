package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.HardDeleteSak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.ID
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.harddeletesak.HardDeleteSakVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.harddeletesak.SakFinnesIkke
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.harddeletesak.UgyldigMerkelapp
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.harddeletesak.UkjentProdusent

suspend fun ArbeidsgiverNotifikasjonKlient.hardDeleteSak(id: String): ID {
    logger.info("Forsøker å slette sak $id")

    val query = HardDeleteSak(
        variables = HardDeleteSak.Variables(
            id
        )
    )

    val resultat = execute(query)

    val deleteSak = resultat.data?.hardDeleteSak
    if (deleteSak !is HardDeleteSakVellykket) {
        when (deleteSak) {
            is UgyldigMerkelapp -> {
                logger.error("Feilmelding ${deleteSak.feilmelding}")
                throw HardDeleteSakException(id, deleteSak.feilmelding)
            }
            is SakFinnesIkke -> {
                logger.error("Feilmelding ${deleteSak.feilmelding}")
                throw HardDeleteSakException(id, deleteSak.feilmelding)
            }
            is UkjentProdusent -> {
                logger.error("Feilmelding ${deleteSak.feilmelding}")
                throw HardDeleteSakException(id, deleteSak.feilmelding)
            }
            else -> {
                logger.error("Kunne ikke harddelete sak", deleteSak)
                throw HardDeleteSakException(id, "ukjent feil: ${resultat.errors}")
            }
        }
    }
    logger.info("Slettet sak ${deleteSak.id}")
    return deleteSak.id
}

class HardDeleteSakException(id: String, feilmelding: String?) :
    Exception("Sletting av sak $id mot arbeidsgiver-notifikasjon-api feilet: $feilmelding")
