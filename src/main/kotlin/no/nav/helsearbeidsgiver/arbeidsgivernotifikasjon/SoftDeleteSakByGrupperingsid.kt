package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.ID
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.SoftDeleteSakByGrupperingsid
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.softdeletesakbygrupperingsid.SakFinnesIkke
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.softdeletesakbygrupperingsid.SoftDeleteSakVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.softdeletesakbygrupperingsid.UgyldigMerkelapp
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.softdeletesakbygrupperingsid.UkjentProdusent

suspend fun ArbeidsgiverNotifikasjonKlient.softDeleteSakByGrupperingsid(grupperingsid: String, merkelapp: String): ID {
    logger.info("Forsøker å slette sak med grupperingsid $grupperingsid og merkelapp $merkelapp")

    val query = SoftDeleteSakByGrupperingsid(
        variables = SoftDeleteSakByGrupperingsid.Variables(
            grupperingsid,
            merkelapp
        )
    )

    val resultat = execute(query)

    val deleteSak = resultat.data?.softDeleteSakByGrupperingsid
    if (deleteSak !is SoftDeleteSakVellykket) {
        when (deleteSak) {
            is UgyldigMerkelapp -> {
                logger.error("Feilmelding ${deleteSak.feilmelding}")
                throw SoftDeleteSakException(grupperingsid, deleteSak.feilmelding)
            }
            is SakFinnesIkke -> {
                logger.error("Feilmelding ${deleteSak.feilmelding}")
                throw SoftDeleteSakException(grupperingsid, deleteSak.feilmelding)
            }
            is UkjentProdusent -> {
                logger.error("Feilmelding ${deleteSak.feilmelding}")
                throw SoftDeleteSakException(grupperingsid, deleteSak.feilmelding)
            }
            else -> {
                logger.error("Kunne ikke softdelete sak: $deleteSak")
                throw SoftDeleteSakException(grupperingsid, "ukjent feil: ${resultat.errors}")
            }
        }
    }
    logger.info("Slettet sak ${deleteSak.id}")
    return deleteSak.id
}
