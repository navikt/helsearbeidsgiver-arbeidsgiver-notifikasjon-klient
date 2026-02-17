package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.ISO8601Duration
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.enums.Sendevindu
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.inputs.AltinnRessursMottakerInput
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.inputs.AltinntjenesteMottakerInput
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.inputs.PaaminnelseEksterntVarselAltinnressursInput
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.inputs.PaaminnelseEksterntVarselAltinntjenesteInput
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.inputs.PaaminnelseEksterntVarselInput
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.inputs.PaaminnelseInput
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.inputs.PaaminnelseTidspunktInput

data class Paaminnelse(
    val tittel: String,
    val innhold: String,
    val tidMellomOppgaveopprettelseOgPaaminnelse: ISO8601Duration,
)

internal fun Paaminnelse.tilPaaminnelseInput(
    mottaker: AltinnMottaker,
    sendevindu: Sendevindu,
): PaaminnelseInput =
    PaaminnelseInput(
        eksterneVarsler =
            listOf(
                PaaminnelseEksterntVarselInput(
                    altinnressurs =
                        when (mottaker) {
                            is AltinnMottaker.Altinn3 ->
                                PaaminnelseEksterntVarselAltinnressursInput(
                                    mottaker =
                                        AltinnRessursMottakerInput(
                                            ressursId = mottaker.ressurs.value,
                                        ),
                                    epostTittel = tittel,
                                    epostHtmlBody = innhold,
                                    smsTekst = innhold,
                                    sendevindu = sendevindu,
                                )
                            is AltinnMottaker.Altinn2 -> null
                        },
                    altinntjeneste =
                        when (mottaker) {
                            is AltinnMottaker.Altinn2 ->
                                PaaminnelseEksterntVarselAltinntjenesteInput(
                                    mottaker =
                                        AltinntjenesteMottakerInput(
                                            serviceCode = mottaker.serviceCode,
                                            serviceEdition = mottaker.serviceEdition,
                                        ),
                                    tittel = tittel,
                                    innhold = innhold,
                                    sendevindu = sendevindu,
                                )
                            is AltinnMottaker.Altinn3 -> null
                        },
                ),
            ),
        tidspunkt = PaaminnelseTidspunktInput(etterOpprettelse = tidMellomOppgaveopprettelseOgPaaminnelse),
    )
