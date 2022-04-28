package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.ID
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.OppgaveUtfoert
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutfoert.NotifikasjonFinnesIkke
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutfoert.OppgaveUtfoertVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutfoert.UgyldigMerkelapp
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutfoert.UkjentProdusent

suspend fun ArbeidsgiverNotifikasjonKlient.oppgaveUtfoert(id: String): ID {
    val query = OppgaveUtfoert(
        variables = OppgaveUtfoert.Variables(id)
    )

    logger.info("Forsøker å opprette ny sak mot arbeidsgiver-notifikasjoner")

    val resultat = execute(query)
    val utfoertOppgave = resultat.data?.oppgaveUtfoert

    if (utfoertOppgave !is OppgaveUtfoertVellykket) {
        when (utfoertOppgave) {
            is UkjentProdusent -> {
                logger.error("Feilmelding ${utfoertOppgave.feilmelding}")
                throw OppgaveUtfoertException(id, utfoertOppgave.feilmelding)
            }
            is UgyldigMerkelapp -> {
                logger.error("Feilmelding ${utfoertOppgave.feilmelding}")
                throw OppgaveUtfoertException(id, utfoertOppgave.feilmelding)
            }
            is NotifikasjonFinnesIkke -> {
                logger.error("Feilmelding ${utfoertOppgave.feilmelding}")
                throw OppgaveUtfoertException(id, utfoertOppgave.feilmelding)
            }
            else -> {
                logger.error("Kunne ikke opprette utføre oppgave", utfoertOppgave)
                throw OppgaveUtfoertException(id, "ukjent feil: ${resultat.errors}")
            }
        }
    }
    logger.info("Oppgave utført ${utfoertOppgave.id}")
    return utfoertOppgave.id
}

class OppgaveUtfoertException(id: String, feilmelding: String?) :
    Exception("Utføring av oppgave $id mot arbeidsgiver-notifikasjon-api feilet: $feilmelding")
