package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.ISO8601Duration
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.enums.Sendevindu
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.inputs.AltinnRessursMottakerInput
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.inputs.PaaminnelseEksterntVarselAltinnressursInput
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.inputs.PaaminnelseEksterntVarselInput
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.inputs.PaaminnelseInput
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.inputs.PaaminnelseTidspunktInput

data class Paaminnelse(
    val ressursId: Altinn3Ressurs,
    val tittel: String,
    val innhold: String,
    val tidMellomOppgaveopprettelseOgPaaminnelse: ISO8601Duration,
)

fun Paaminnelse.tilPaaminnelseInput(): PaaminnelseInput =
    PaaminnelseInput(
        eksterneVarsler =
            listOf(
                PaaminnelseEksterntVarselInput(
                    altinnressurs =
                        PaaminnelseEksterntVarselAltinnressursInput(
                            mottaker =
                                AltinnRessursMottakerInput(
                                    ressursId = ressursId.value,
                                ),
                            epostTittel = tittel,
                            epostHtmlBody = innhold,
                            smsTekst = innhold,
                            sendevindu = Sendevindu.NKS_AAPNINGSTID,
                        ),
                ),
            ),
        tidspunkt = PaaminnelseTidspunktInput(etterOpprettelse = tidMellomOppgaveopprettelseOgPaaminnelse),
    )
