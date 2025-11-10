package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.expediagroup.graphql.client.types.GraphQLClientError
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache5.Apache5
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.bearerAuth
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.HardDeleteSak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.HardDeleteSakByGrupperingsid
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.ID
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.ISO8601DateTime
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.NySak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.NyStatusSakByGrupperingsid
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.OppgaveEndrePaaminnelseByEksternId
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.OppgaveUtfoertByEksternIdV2
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.OppgaveUtgaattByEksternId
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.OpprettNyOppgave
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.Whoami
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.enums.NyTidStrategi
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.enums.SaksStatus
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.harddeletesak.HardDeleteSakVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.inputs.FutureTemporalInput
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.inputs.HardDeleteUpdateInput
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nysak.NySakVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussakbygrupperingsid.NyStatusSakVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveendrepaaminnelsebyeksternid.OppgaveEndrePaaminnelseVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutfoertbyeksternidv2.OppgaveUtfoertVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutgaattbyeksternid.OppgaveUtgaattVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.NyOppgaveVellykket
import no.nav.helsearbeidsgiver.utils.json.toJson
import no.nav.helsearbeidsgiver.utils.log.logger
import no.nav.helsearbeidsgiver.utils.log.sikkerLogger
import no.nav.helsearbeidsgiver.utils.pipe.orDefault
import java.net.URI
import kotlin.time.Duration
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.harddeletesakbygrupperingsid.HardDeleteSakVellykket as HardDeleteSakByGrupperingsidVellykket

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
            serializer = serializerWithFallback,
        )

    suspend fun opprettNySak(
        virksomhetsnummer: String,
        grupperingsid: String,
        merkelapp: String,
        lenke: String?,
        tittel: String,
        statusTekst: String?,
        tilleggsinfo: String? = null,
        initiellStatus: SaksStatus,
        hardDeleteOm: Duration,
    ): ID =
        NySak(
            variables =
                NySak.Variables(
                    grupperingsid = grupperingsid,
                    merkelapp = merkelapp,
                    virksomhetsnummer = virksomhetsnummer,
                    tittel = tittel,
                    tilleggsinformasjon = tilleggsinfo,
                    lenke = lenke,
                    initiellStatus = initiellStatus,
                    overstyrStatustekstMed = statusTekst,
                    hardDeleteOm = hardDeleteOm.tilDagerIso8601(),
                ),
        ).also { loggInfo("Forsøker å opprette ny sak.") }
            .execute(
                toResult = NySak.Result::nySak,
                toSuccess = { it as? NySakVellykket },
                onError = { res, err -> Feil.nySak(grupperingsid, res, err) },
            ).id
            .also { loggInfo("Opprettet ny sak med id '$it'.") }

    suspend fun nyStatusSakByGrupperingsid(
        grupperingsid: String,
        merkelapp: String,
        status: SaksStatus,
        statusTekst: String? = null,
        nyLenke: String? = null,
        tidspunkt: ISO8601DateTime? = null,
        hardDeleteOm: Duration? = null,
    ) {
        loggInfo("Forsøker å sette ny status '$status' på sak med grupperingsid '$grupperingsid'.")

        NyStatusSakByGrupperingsid(
            variables =
                NyStatusSakByGrupperingsid.Variables(
                    grupperingsid = grupperingsid,
                    merkelapp = merkelapp,
                    nyStatus = status,
                    overstyrStatustekstMed = statusTekst,
                    nyLenke = nyLenke,
                    tidspunkt = tidspunkt,
                    hardDeleteOppdatering =
                        hardDeleteOm?.let {
                            HardDeleteUpdateInput(
                                nyTid =
                                    FutureTemporalInput(
                                        om = it.tilDagerIso8601(),
                                    ),
                                strategi = NyTidStrategi.OVERSKRIV,
                            )
                        },
                ),
        ).execute(
            toResult = NyStatusSakByGrupperingsid.Result::nyStatusSakByGrupperingsid,
            toSuccess = { it as? NyStatusSakVellykket },
            onError = { res, err -> Feil.nyStatusSakByGrupperingsid(grupperingsid, merkelapp, status, res, err) },
        )

        loggInfo("Satt ny status '$status' på sak med grupperingsid '$grupperingsid'.")
    }

    suspend fun opprettNyOppgave(
        virksomhetsnummer: String,
        eksternId: String,
        grupperingsid: String?,
        merkelapp: String,
        lenke: String,
        tekst: String,
        tidspunkt: ISO8601DateTime?,
        varslingTittel: String,
        varslingInnhold: String,
        paaminnelse: Paaminnelse?,
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
                    paaminnelseInput = paaminnelse?.tilPaaminnelseInput(),
                ),
        ).also { loggInfo("Forsøker å opprette ny oppgave.") }
            .execute(
                toResult = OpprettNyOppgave.Result::nyOppgave,
                toSuccess = { it as? NyOppgaveVellykket },
                onError = Feil::nyOppgave,
            ).id
            .also { loggInfo("Opprettet ny oppgave med id: '$it'.") }

    suspend fun oppgaveUtfoertByEksternIdV2(
        eksternId: String,
        merkelapp: String,
        nyLenke: String? = null,
    ) {
        loggInfo("Forsøker å sette oppgave med ekstern ID '$eksternId' som utført.")

        OppgaveUtfoertByEksternIdV2(
            variables =
                OppgaveUtfoertByEksternIdV2.Variables(
                    eksternId = eksternId,
                    merkelapp = merkelapp,
                    nyLenke = nyLenke,
                ),
        ).execute(
            toResult = OppgaveUtfoertByEksternIdV2.Result::oppgaveUtfoertByEksternId_V2,
            toSuccess = { it as? OppgaveUtfoertVellykket },
            onError = { res, err -> Feil.oppgaveUtfoertByEksternIdV2(eksternId, merkelapp, res, err) },
        )

        loggInfo("Oppgave med ekstern ID '$eksternId' satt til utført.")
    }

    suspend fun oppgaveUtgaattByEksternId(
        merkelapp: String,
        eksternId: String,
        nyLenke: String? = null,
    ) {
        loggInfo("Setter oppgave med eksternId '$eksternId' til utgått.")

        OppgaveUtgaattByEksternId(
            variables =
                OppgaveUtgaattByEksternId.Variables(
                    merkelapp = merkelapp,
                    eksternId = eksternId,
                    nyLenke = nyLenke,
                ),
        ).execute(
            toResult = OppgaveUtgaattByEksternId.Result::oppgaveUtgaattByEksternId,
            toSuccess = { it as? OppgaveUtgaattVellykket },
            onError = { res, err -> Feil.oppgaveUtgaattByEksternId(eksternId, res, err) },
        )

        loggInfo("Oppgave med eksternId '$eksternId' satt til utgått.")
    }

    suspend fun hardDeleteSakByGrupperingsid(
        grupperingsid: String,
        merkelapp: String,
    ) {
        loggInfo("Forsøker å slette (hard) sak med grupperingsid '$grupperingsid' og merkelapp '$merkelapp'.")

        HardDeleteSakByGrupperingsid(
            variables =
                HardDeleteSakByGrupperingsid.Variables(
                    grupperingsid = grupperingsid,
                    merkelapp = merkelapp,
                ),
        ).execute(
            toResult = HardDeleteSakByGrupperingsid.Result::hardDeleteSakByGrupperingsid,
            toSuccess = { it as? HardDeleteSakByGrupperingsidVellykket },
            onError = { res, err -> Feil.hardDeleteSakByGrupperingsid(grupperingsid, res, err) },
        )

        loggInfo("Slettet (hard) sak med grupperingsid '$grupperingsid' og merkelapp '$merkelapp'.")
    }

    suspend fun hardDeleteSak(id: String) {
        loggInfo("Forsøker å slette (hard) sak med id '$id'.")

        HardDeleteSak(
            variables = HardDeleteSak.Variables(id),
        ).execute(
            toResult = HardDeleteSak.Result::hardDeleteSak,
            toSuccess = { it as? HardDeleteSakVellykket },
            onError = { res, err -> Feil.hardDeleteSak(id, res, err) },
        )

        loggInfo("Slettet (hard) sak med id '$id'.")
    }

    suspend fun endreOppgavePaaminnelserByEksternId(
        merkelapp: String,
        eksternId: String,
        paaminnelse: Paaminnelse?,
        idempotencyKey: String? = null,
    ) {
        loggInfo("Forsøker å endre påminnelser for oppgave med eksternId '$eksternId'.")

        OppgaveEndrePaaminnelseByEksternId(
            variables =
                OppgaveEndrePaaminnelseByEksternId.Variables(
                    merkelapp = merkelapp,
                    eksternId = eksternId,
                    idempotencyKey = idempotencyKey,
                    paaminnelse = paaminnelse?.tilPaaminnelseInput(),
                ),
        ).execute(
            toResult = OppgaveEndrePaaminnelseByEksternId.Result::oppgaveEndrePaaminnelseByEksternId,
            toSuccess = { it as? OppgaveEndrePaaminnelseVellykket },
            onError = { res, err -> Feil.endreOppgavePaaminnelserByEksternId(eksternId, res, err) },
        )

        loggInfo("Endret påminnelser for oppgave med eksternId '$eksternId'.")
    }

    suspend fun slettOppgavePaaminnelserByEksternId(
        merkelapp: String,
        eksternId: String,
    ) {
        loggInfo("Forsøker å slette påminnelser for oppgave med eksternId '$eksternId'.")

        OppgaveEndrePaaminnelseByEksternId(
            variables =
                OppgaveEndrePaaminnelseByEksternId.Variables(
                    merkelapp = merkelapp,
                    eksternId = eksternId,
                ),
        ).execute(
            toResult = OppgaveEndrePaaminnelseByEksternId.Result::oppgaveEndrePaaminnelseByEksternId,
            toSuccess = { it as? OppgaveEndrePaaminnelseVellykket },
            onError = { res, err -> Feil.endreOppgavePaaminnelserByEksternId(eksternId, res, err) },
        )

        loggInfo("Slettet påminnelser for oppgave med eksternId '$eksternId'.")
    }

    // Brukes til debugging
    suspend fun whoami(): String? =
        Whoami()
            .also { loggInfo("Henter 'whoami' info fra arbeidsgiver-notifikasjon-api.") }
            .execute(
                toResult = { it },
                toSuccess = { it },
                onError = { _, _ -> throw RuntimeException("Feil ved henting av 'whoami'.") },
            ).whoami
            .also { loggInfo("Whoami: '$it'.") }

    private suspend fun <Data : Any, Result : Any, Success : Result> GraphQLClientRequest<Data>.execute(
        toResult: (Data) -> Result,
        toSuccess: (Result) -> Success?,
        onError: (Result?, String?) -> Nothing,
    ): Success {
        val response =
            graphQLClient.execute(this) {
                bearerAuth(getAccessToken())
            }

        val result = response.data?.let { toResult(it) }
        val errors = response.errors?.ifEmpty { null }?.toJsonStr()

        val success = result?.runCatching { toSuccess(this) }?.getOrNull()
        if (success != null && errors != null) {
            "Fikk respons fra arbeidsgiver-notifikasjon-api med både resultat og feil. Logger feil og fortsetter.".let {
                logger.error(it)
                sikkerLogger.error("$it\nFeil: $errors")
            }
        }

        return success ?: onError(result, errors)
    }

    private fun loggInfo(melding: String) {
        logger.info(melding)
        sikkerLogger.info(melding)
    }
}

internal fun createHttpClient(): HttpClient =
    HttpClient(Apache5) {
        expectSuccess = true

        install(HttpRequestRetry) {
            retryOnException(
                maxRetries = 5,
                retryOnTimeout = true,
            )
            exponentialDelay()
        }

        install(HttpTimeout) {
            connectTimeoutMillis = 10_000
            requestTimeoutMillis = 10_000
            socketTimeoutMillis = 10_000
        }
    }

/** [Les om ISO8601-durations](https://en.wikipedia.org/wiki/ISO_8601#Durations) */
private fun Duration.tilDagerIso8601(): String = "P${inWholeDays}D"

private fun List<GraphQLClientError>.toJsonStr(): String =
    map {
        mapOf(
            it.messageJson(),
            it.locationsJson(),
            it.pathJson(),
            it.extensionsJson(),
        ).toJson()
    }.toJson(JsonElement.serializer())
        .toString()

private fun GraphQLClientError.messageJson(): Pair<String, JsonElement> = ::message.name to message.toJson()

private fun GraphQLClientError.locationsJson(): Pair<String, JsonElement> =
    ::locations.name to
        locations
            ?.map {
                mapOf(
                    it::line.name to it.line.toJson(Int.serializer()),
                    it::column.name to it.column.toJson(Int.serializer()),
                ).toJson()
            }?.toJson(JsonElement.serializer())
            .orDefault(JsonNull)

private fun GraphQLClientError.pathJson(): Pair<String, JsonElement> =
    ::path.name to
        path
            ?.map { it.toString() }
            ?.toJson(String.serializer())
            .orDefault(JsonNull)

private fun GraphQLClientError.extensionsJson(): Pair<String, JsonElement> =
    ::extensions.name to
        extensions
            ?.mapValues {
                it.value
                    ?.toString()
                    ?.toJson()
                    .orDefault(JsonNull)
            }?.toJson()
            .orDefault(JsonNull)
