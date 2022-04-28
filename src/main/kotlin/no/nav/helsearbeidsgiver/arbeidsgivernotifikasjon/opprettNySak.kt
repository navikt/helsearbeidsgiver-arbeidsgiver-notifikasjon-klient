package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.ID
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.OpprettNySak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnysak.DuplikatGrupperingsid
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnysak.NySakVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnysak.UgyldigMerkelapp
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnysak.UgyldigMottaker
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnysak.UkjentProdusent
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnysak.UkjentRolle

suspend fun ArbeidsgiverNotifikasjonKlient.opprettNySak(
    grupperingsid: String,
    merkelapp: String,
    virksomhetsnummer: String,
    tittel: String,
    lenke: String
): ID {
    val query = OpprettNySak(
        variables = OpprettNySak.Variables(
            grupperingsid,
            merkelapp,
            virksomhetsnummer,
            tittel,
            lenke
        )
    )

    logger.info("Forsøker å opprette ny sak mot arbeidsgiver-notifikasjoner")

    val resultat = execute(query)
    val nySak = resultat.data?.nySak

    if (nySak !is NySakVellykket) {
        when (nySak) {
            is UgyldigMerkelapp -> {
                logger.error("Feilmelding ${nySak.feilmelding}")
                throw OpprettNySakFeiletException(nySak.feilmelding)
            }
            is UgyldigMottaker -> {
                logger.error("Feilmelding ${nySak.feilmelding}")
                throw OpprettNySakFeiletException(nySak.feilmelding)
            }
            is UkjentProdusent -> {
                logger.error("Feilmelding ${nySak.feilmelding}")
                throw OpprettNySakFeiletException(nySak.feilmelding)
            }
            is UkjentRolle -> {
                logger.error("Feilmelding ${nySak.feilmelding}")
                throw OpprettNySakFeiletException(nySak.feilmelding)
            }
            is DuplikatGrupperingsid -> {
                logger.error("Feilmelding ${nySak.feilmelding}")
                throw OpprettNySakFeiletException(nySak.feilmelding)
            }
            else -> {
                logger.error("Kunne ikke opprette ny sak", nySak)
                throw OpprettNySakFeiletException("ukjent feil: ${resultat.errors}")
            }
        }
    }
    logger.info("Opprettet ny sak ${nySak.id}")
    return nySak.id
}

class OpprettNySakFeiletException(feilmelding: String?) :
    Exception("Opprettelse av ny sak mot arbeidsgiver-notifikasjon-api feilet: $feilmelding")
