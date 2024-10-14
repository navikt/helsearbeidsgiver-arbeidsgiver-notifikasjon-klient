package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.expediagroup.graphql.client.types.GraphQLClientError
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache5.Apache5
import io.ktor.client.request.bearerAuth
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.HardDeleteSak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.ID
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.ISO8601DateTime
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.NyStatusSak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.NyStatusSakByGrupperingsid
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.OppgaveUtfoert
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.OppgaveUtfoertByEksternIdV2
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.OppgaveUtgaatt
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.OppgaveUtgaattByEksternId
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.OpprettNyOppgave
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.OpprettNySak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.SoftDeleteSak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.SoftDeleteSakByGrupperingsid
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.Whoami
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.enums.SaksStatus
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.harddeletesak.HardDeleteSakVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussak.NyStatusSakVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutfoert.OppgaveUtfoertVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutgaatt.OppgaveUtgaattVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.NyOppgaveVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnysak.NySakVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.softdeletesak.SoftDeleteSakVellykket
import no.nav.helsearbeidsgiver.utils.log.logger
import no.nav.helsearbeidsgiver.utils.log.sikkerLogger
import java.net.URI
import kotlin.time.Duration
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussakbygrupperingsid.NyStatusSakVellykket as NyStatusSakByGrupperingsidVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutfoertbyeksternidv2.OppgaveUtfoertVellykket as OppgaveUtfoertByEksternIdV2Vellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutgaattbyeksternid.OppgaveUtgaattVellykket as OppgaveUtgaattByEksternIdVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.softdeletesakbygrupperingsid.SoftDeleteSakVellykket as SoftDeleteSakByGrupperingsidVellykket

class ArbeidsgiverNotifikasjonKlient(
    url: String,
    private val getAccessToken: () -> String,
) {
    private val logger = logger()
    private val sikkerLogger = sikkerLogger()

    private val graphQLClient =
        GraphQLKtorClient(
            url = URI(url).toURL(),
            httpClient = createHttpClient(),
        )

    suspend fun opprettNySak(
        virksomhetsnummer: String,
        merkelapp: String,
        grupperingsid: String,
        lenke: String,
        tittel: String,
        statusTekst: String?,
        initiellStatus: SaksStatus,
        harddeleteOm: Duration,
    ): ID =
        OpprettNySak(
            variables =
                OpprettNySak.Variables(
                    grupperingsid = grupperingsid,
                    merkelapp = merkelapp,
                    virksomhetsnummer = virksomhetsnummer,
                    tittel = tittel,
                    lenke = lenke,
                    initiellStatus = initiellStatus,
                    statusTekst = statusTekst,
                    // https://en.wikipedia.org/wiki/ISO_8601#Durations
                    harddeleteOm = "P${harddeleteOm.inWholeDays}D",
                ),
        ).also { loggInfo("Forsøker å opprette ny sak mot arbeidsgiver-notifikasjoner.") }
            .executeOrThrow(
                toResult = OpprettNySak.Result::nySak,
                toSuccess = { it as? NySakVellykket },
                onError = Feil::opprettNySak,
            ).id
            .also { loggInfo("Opprettet ny sak med id '$it'.") }

    suspend fun nyStatusSak(
        id: String,
        status: SaksStatus,
        statusTekst: String? = null,
        nyLenkeTilSak: String? = null,
        tidspunkt: ISO8601DateTime? = null,
    ) {
        loggInfo("Forsøker å sette ny status '$status' på sak med id '$id'.")

        NyStatusSak(
            variables =
                NyStatusSak.Variables(
                    nyStatusSakId = id,
                    status = status,
                    statusTekst = statusTekst,
                    nyLenkeTilSak = nyLenkeTilSak,
                    tidspunkt = tidspunkt,
                ),
        ).execute(
            toResult = NyStatusSak.Result::nyStatusSak,
            toSuccess = { it as? NyStatusSakVellykket },
            onError = { res, err -> Feil.nyStatusSak(id, status, res, err) },
        )

        loggInfo("Satt ny status '$status' på sak for id '$id'.")
    }

    suspend fun nyStatusSakByGrupperingsid(
        grupperingsid: String,
        merkelapp: String,
        status: SaksStatus,
        statusTekst: String? = null,
        nyLenke: String? = null,
        tidspunkt: ISO8601DateTime? = null,
    ) {
        loggInfo("Forsøker å sette ny status '$status' på sak med grupperingsid '$grupperingsid'.")

        NyStatusSakByGrupperingsid(
            variables =
                NyStatusSakByGrupperingsid.Variables(
                    grupperingsid = grupperingsid,
                    merkelapp = merkelapp,
                    nyStatus = status,
                    overstyrStatustekstMed = statusTekst,
                    nyLenkeTilSak = nyLenke,
                    tidspunkt = tidspunkt,
                ),
        ).executeOrThrow(
            toResult = NyStatusSakByGrupperingsid.Result::nyStatusSakByGrupperingsid,
            toSuccess = { it as? NyStatusSakByGrupperingsidVellykket },
            onError = { res, err -> Feil.nyStatusSakByGrupperingsid(grupperingsid, merkelapp, status, res, err) },
        )

        loggInfo("Satt ny status '$status' på sak med grupperingsid '$grupperingsid'.")
    }

    suspend fun opprettNyOppgave(
        eksternId: String,
        lenke: String,
        tekst: String,
        virksomhetsnummer: String,
        merkelapp: String,
        tidspunkt: ISO8601DateTime?,
        grupperingsid: String?,
        varslingTittel: String,
        varslingInnhold: String,
    ): ID =
        OpprettNyOppgave(
            variables =
                OpprettNyOppgave.Variables(
                    eksternId = eksternId,
                    lenke = lenke,
                    tekst = tekst,
                    virksomhetsnummer = virksomhetsnummer,
                    merkelapp = merkelapp,
                    tidspunkt = tidspunkt,
                    grupperingsid = grupperingsid,
                    varslingTittel = varslingTittel,
                    varslingInnhold = varslingInnhold,
                ),
        ).also { loggInfo("Forsøker å opprette ny oppgave mot arbeidsgiver-notifikasjoner.") }
            .executeOrThrow(
                toResult = OpprettNyOppgave.Result::nyOppgave,
                toSuccess = { it as? NyOppgaveVellykket },
                onError = Feil::nyOppgave,
            ).id
            .also { loggInfo("Opprettet ny oppgave med id: '$it'.") }

    suspend fun oppgaveUtfoert(id: String) {
        loggInfo("Forsøker å sette oppgave med id '$id' som utført mot arbeidsgiver-notifikasjoner.")

        OppgaveUtfoert(
            variables = OppgaveUtfoert.Variables(id),
        ).execute(
            toResult = OppgaveUtfoert.Result::oppgaveUtfoert,
            toSuccess = { it as? OppgaveUtfoertVellykket },
            onError = { res, err -> Feil.oppgaveUtfoert(id, res, err) },
        )

        loggInfo("Oppgave med id '$id' satt til utført.")
    }

    suspend fun oppgaveUtfoertByEksternIdV2(
        eksternId: String,
        merkelapp: String,
        nyLenke: String? = null,
    ) {
        loggInfo("Forsøker å sette oppgave med ekstern ID '$eksternId' som utført mot arbeidsgiver-notifikasjoner.")

        OppgaveUtfoertByEksternIdV2(
            variables =
                OppgaveUtfoertByEksternIdV2.Variables(
                    eksternId = eksternId,
                    merkelapp = merkelapp,
                    nyLenke = nyLenke,
                ),
        ).execute(
            toResult = OppgaveUtfoertByEksternIdV2.Result::oppgaveUtfoertByEksternId_V2,
            toSuccess = { it as? OppgaveUtfoertByEksternIdV2Vellykket },
            onError = { res, err -> Feil.oppgaveUtfoertByEksternIdV2(eksternId, merkelapp, res, err) },
        )

        loggInfo("Oppgave med ekstern ID '$eksternId' satt til utført.")
    }

    suspend fun softDeleteSak(id: String): ID =
        SoftDeleteSak(
            variables = SoftDeleteSak.Variables(id),
        ).also { loggInfo("Forsøker å slette (soft) sak med id '$id'.") }
            .executeOrThrow(
                toResult = SoftDeleteSak.Result::softDeleteSak,
                toSuccess = { it as? SoftDeleteSakVellykket },
                onError = { res, err -> Feil.softDeleteSak(id, res, err) },
            ).id
            .also { loggInfo("Slettet (soft) sak med id '$id'.") }

    suspend fun softDeleteSakByGrupperingsid(
        grupperingsid: String,
        merkelapp: String,
    ) {
        loggInfo("Forsøker å slette sak med grupperingsid '$grupperingsid' og merkelapp '$merkelapp'.")

        SoftDeleteSakByGrupperingsid(
            variables =
                SoftDeleteSakByGrupperingsid.Variables(
                    grupperingsid = grupperingsid,
                    merkelapp = merkelapp,
                ),
        ).executeOrThrow(
            toResult = SoftDeleteSakByGrupperingsid.Result::softDeleteSakByGrupperingsid,
            toSuccess = { it as? SoftDeleteSakByGrupperingsidVellykket },
            onError = { res, err -> Feil.softDeleteSakByGrupperingsid(grupperingsid, res, err) },
        )

        loggInfo("Slettet sak med grupperingsid '$grupperingsid' og merkelapp '$merkelapp'.")
    }

    suspend fun hardDeleteSak(id: String) {
        loggInfo("Forsøker å slette (hard) sak med id '$id'.")

        HardDeleteSak(
            variables = HardDeleteSak.Variables(id),
        ).executeOrThrow(
            toResult = HardDeleteSak.Result::hardDeleteSak,
            toSuccess = { it as? HardDeleteSakVellykket },
            onError = { res, err -> Feil.hardDeleteSak(id, res, err) },
        )

        loggInfo("Slettet (hard) sak med id '$id'.")
    }

    suspend fun whoami(): String? =
        Whoami()
            .also { loggInfo("Henter 'whoami' info fra arbeidsgiver-notifikasjon-api.") }
            .executeOrThrow(
                toResult = { this },
                toSuccess = { it },
                onError = { _, _ -> throw RuntimeException("Feil ved henting av 'whoami'") },
            ).whoami
            .also { loggInfo("Whoami: '$it'.") }

    suspend fun oppgaveUtgaatt(id: String) {
        loggInfo("Setter oppgave med id '$id' til utgått.")

        OppgaveUtgaatt(
            variables = OppgaveUtgaatt.Variables(id),
        ).execute(
            toResult = OppgaveUtgaatt.Result::oppgaveUtgaatt,
            toSuccess = { it as? OppgaveUtgaattVellykket },
            onError = { res, err -> Feil.oppgaveUtgaatt(id, res, err) },
        )

        loggInfo("Oppgave med id '$id' satt til utgått.")
    }

    suspend fun oppgaveUtgaattByEksternId(
        merkelapp: String,
        eksternId: String,
        nyLenke: String? = null,
    ) {
        loggInfo("Setter oppgave med eksternId '$eksternId' til utgått.")

        OppgaveUtgaattByEksternId(
            variables = OppgaveUtgaattByEksternId.Variables(merkelapp = merkelapp, eksternId = eksternId, nyLenke = nyLenke),
        ).execute(
            toResult = OppgaveUtgaattByEksternId.Result::oppgaveUtgaattByEksternId,
            toSuccess = { it as? OppgaveUtgaattByEksternIdVellykket },
            onError = { res, err -> Feil.oppgaveUtgaattByEksternId(eksternId, res, err) },
        )

        loggInfo("Oppgave med eksternId '$eksternId' satt til utgått.")
    }

    private suspend fun <Data : Any, Result : Any, Success : Result> GraphQLClientRequest<Data>.execute(
        toResult: Data.() -> Result,
        toSuccess: (Result?) -> Success?,
        onError: (Result?, List<GraphQLClientError>?) -> Unit,
    ) {
        val (success, handleError) = executeOrError(toResult, toSuccess, onError)

        if (success == null) {
            handleError()
        }
    }

    private suspend fun <Data : Any, Result : Any, Success : Result> GraphQLClientRequest<Data>.executeOrThrow(
        toResult: Data.() -> Result,
        toSuccess: (Result?) -> Success?,
        onError: (Result?, List<GraphQLClientError>?) -> Nothing,
    ): Success {
        val (success, handleError) = executeOrError(toResult, toSuccess, onError)

        return success ?: handleError()
    }

    private suspend fun <Data : Any, Result : Any, Success : Result, Error : Unit> GraphQLClientRequest<Data>.executeOrError(
        toResult: Data.() -> Result,
        toSuccess: (Result?) -> Success?,
        onError: (Result?, List<GraphQLClientError>?) -> Error,
    ): Pair<Success?, () -> Error> {
        val response =
            graphQLClient.execute(this) {
                bearerAuth(getAccessToken())
            }

        val result = response.data?.toResult()

        return Pair(
            first = runCatching { toSuccess(result) }.getOrNull(),
            second = { onError(result, response.errors) },
        )
    }

    private fun loggInfo(melding: String) {
        logger.info(melding)
        sikkerLogger.info(melding)
    }
}

internal fun createHttpClient(): HttpClient = HttpClient(Apache5)
