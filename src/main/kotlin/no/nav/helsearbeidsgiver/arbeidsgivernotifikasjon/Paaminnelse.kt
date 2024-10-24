package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.ISO8601Duration
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.enums.Sendevindu
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.inputs.AltinntjenesteMottakerInput
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.inputs.PaaminnelseEksterntVarselAltinntjenesteInput
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.inputs.PaaminnelseEksterntVarselInput
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.inputs.PaaminnelseInput
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.inputs.PaaminnelseTidspunktInput

const val ALTINN_MOTTAKER_SERVICE_CODE = "4936"
const val ALTINN_MOTTAKER_SERVICE_EDITION = "1"

data class Paaminnelse(
    val tittel: String,
    val innhold: String,
    val tidMellomOppgaveopprettelseOgPaaminnelse: ISO8601Duration,
)

fun Paaminnelse.toPaaminnelseInput(): PaaminnelseInput =
    PaaminnelseInput(
        eksterneVarsler =
            listOf(
                PaaminnelseEksterntVarselInput(
                    altinntjeneste =
                        PaaminnelseEksterntVarselAltinntjenesteInput(
                            mottaker =
                                AltinntjenesteMottakerInput(
                                    serviceCode = ALTINN_MOTTAKER_SERVICE_CODE,
                                    serviceEdition = ALTINN_MOTTAKER_SERVICE_EDITION,
                                ),
                            tittel = tittel,
                            innhold = innhold,
                            sendevindu = Sendevindu.LOEPENDE,
                        ),
                ),
            ),
        tidspunkt = PaaminnelseTidspunktInput(etterOpprettelse = tidMellomOppgaveopprettelseOgPaaminnelse),
    )
