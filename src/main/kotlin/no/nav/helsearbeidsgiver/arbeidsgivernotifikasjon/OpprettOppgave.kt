package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.ID
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.ISO8601DateTime
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.OpprettNyOppgave
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.DuplikatEksternIdOgMerkelapp
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.NyOppgaveVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.UgyldigMerkelapp
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.UgyldigMottaker
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.UkjentProdusent
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.UkjentRolle

suspend fun ArbeidsgiverNotifikasjonKlient.opprettNyOppgave(
    eksternId: String,
    lenke: String,
    tekst: String,
    virksomhetsnummer: String,
    merkelapp: String,
    tidspunkt: ISO8601DateTime?,
    grupperingsid: String?,
    varslingTittel: String,
    varslingInnhold: String,
): ID {
    val query = OpprettNyOppgave(
        variables = OpprettNyOppgave.Variables(
            eksternId,
            lenke,
            tekst,
            virksomhetsnummer,
            merkelapp,
            tidspunkt,
            grupperingsid,
            varslingTittel,
            varslingInnhold
        )
    )

    logger.info("Forsøker å opprette ny oppgave mot arbeidsgiver-notifikasjoner")

    val resultat = execute(query)

    val nyOppgave = resultat.data?.nyOppgave

    if (nyOppgave !is NyOppgaveVellykket) {
        when (nyOppgave) {
            is UgyldigMottaker -> {
                logger.error("Feilmelding ${nyOppgave.feilmelding}")
                throw OpprettNyOppgaveException(nyOppgave.feilmelding)
            }
            is UkjentRolle -> {
                logger.error("Feilmelding ${nyOppgave.feilmelding}")
                throw OpprettNyOppgaveException(nyOppgave.feilmelding)
            }
            is UgyldigMerkelapp -> {
                logger.error("Feilmelding ${nyOppgave.feilmelding}")
                throw OpprettNyOppgaveException(nyOppgave.feilmelding)
            }
            is UkjentProdusent -> {
                logger.error("Feilmelding ${nyOppgave.feilmelding}")
                throw OpprettNyOppgaveException(nyOppgave.feilmelding)
            }
            is DuplikatEksternIdOgMerkelapp -> {
                logger.error("Feilmelding ${nyOppgave.feilmelding}")
                throw OpprettNyOppgaveException(nyOppgave.feilmelding)
            }
            else -> {
                logger.error("Kunne ikke opprette ny oppgave: $nyOppgave")
                throw OpprettNyOppgaveException("ukjent feil: ${resultat.errors}")
            }
        }
    }
    logger.info("Opprettet ny oppgave med id: ${nyOppgave.id}")
    return nyOppgave.id
}

class OpprettNyOppgaveException(feilmelding: String?) :
    Exception("Opprettelse av ny oppgave mot arbeidsgiver-notifikasjon-api feilet: $feilmelding")
