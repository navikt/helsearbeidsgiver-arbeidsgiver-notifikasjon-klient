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
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.OpprettNyOppgave
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.OpprettNySak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.SoftDeleteSak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.SoftDeleteSakByGrupperingsid
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.Whoami
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.enums.SaksStatus
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.harddeletesak.HardDeleteSakVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussak.NyStatusSakVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutfoert.OppgaveUtfoertVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.NyOppgaveVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnysak.NySakVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.softdeletesak.SoftDeleteSakVellykket
import org.slf4j.LoggerFactory
import java.net.URL
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussakbygrupperingsid.NyStatusSakVellykket as NyStatusSakByGrupperingsidVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.softdeletesakbygrupperingsid.SoftDeleteSakVellykket as SoftDeleteSakByGrupperingsidVellykket

class ArbeidsgiverNotifikasjonKlient(
    url: String,
    private val getAccessToken: () -> String,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private val graphQLClient = GraphQLKtorClient(
        url = URL(url),
        httpClient = createHttpClient(),
    )

    suspend fun opprettNySak(
        grupperingsid: String,
        merkelapp: String,
        virksomhetsnummer: String,
        tittel: String,
        lenke: String,
        statusTekst: String?,
        harddeleteOm: String,
    ): ID =
        OpprettNySak(
            variables = OpprettNySak.Variables(
                grupperingsid = grupperingsid,
                merkelapp = merkelapp,
                virksomhetsnummer = virksomhetsnummer,
                tittel = tittel,
                lenke = lenke,
                statusTekst = statusTekst,
                harddeleteOm = harddeleteOm,
            ),
        )
            .also { logger.info("Forsøker å opprette ny sak mot arbeidsgiver-notifikasjoner") }
            .execute(
                toResult = OpprettNySak.Result::nySak,
                toSuccess = { it as? NySakVellykket },
                onError = Feil::opprettNySak,
            )
            .id
            .also { logger.info("Opprettet ny sak $it") }

    suspend fun nyStatusSak(
        id: String,
        status: SaksStatus,
        statusTekst: String? = null,
        nyLenkeTilSak: String? = null,
    ): ID =
        NyStatusSak(
            variables = NyStatusSak.Variables(
                nyStatusSakId = id,
                status = status,
                statusTekst = statusTekst,
                nyLenkeTilSak = nyLenkeTilSak,
            ),
        )
            .also { logger.info("Forsøker å sette ny status '$status' for sak $id") }
            .execute(
                toResult = NyStatusSak.Result::nyStatusSak,
                toSuccess = { it as? NyStatusSakVellykket },
                onError = { res, err -> Feil.nyStatusSak(id, res, err) },
            )
            .id
            .also { logger.info("Satt ny status '$status' for sak $it") }

    suspend fun nyStatusSakByGrupperingsid(grupperingsid: String, merkelapp: String, nyStatus: SaksStatus): ID =
        NyStatusSakByGrupperingsid(
            variables = NyStatusSakByGrupperingsid.Variables(
                grupperingsid = grupperingsid,
                merkelapp = merkelapp,
                nyStatus = nyStatus,
            ),
        )
            .also { logger.info("Forsøker å sette ny status '$nyStatus' for grupperingsid $grupperingsid") }
            .execute(
                toResult = NyStatusSakByGrupperingsid.Result::nyStatusSakByGrupperingsid,
                toSuccess = { it as? NyStatusSakByGrupperingsidVellykket },
                onError = { res, err -> Feil.nyStatusSakByGrupperingsid(grupperingsid, nyStatus, res, err) },
            )
            .id
            .also { logger.info("Satt ny status '$nyStatus' for sak $it") }

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
            variables = OpprettNyOppgave.Variables(
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
        )
            .also { logger.info("Forsøker å opprette ny oppgave mot arbeidsgiver-notifikasjoner") }
            .execute(
                toResult = OpprettNyOppgave.Result::nyOppgave,
                toSuccess = { it as? NyOppgaveVellykket },
                onError = Feil::nyOppgave,
            )
            .id
            .also { logger.info("Opprettet ny oppgave med id: $it") }

    suspend fun oppgaveUtfoert(id: String): ID =
        OppgaveUtfoert(
            variables = OppgaveUtfoert.Variables(id),
        )
            .also { logger.info("Forsøker å opprette ny sak mot arbeidsgiver-notifikasjoner") }
            .execute(
                toResult = OppgaveUtfoert.Result::oppgaveUtfoert,
                toSuccess = { it as? OppgaveUtfoertVellykket },
                onError = { res, err -> Feil.oppgaveUtfoert(id, res, err) },
            )
            .id
            .also { logger.info("Oppgave utført $it") }

    suspend fun softDeleteSak(id: String): ID =
        SoftDeleteSak(
            variables = SoftDeleteSak.Variables(id),
        )
            .also { logger.info("Forsøker å slette sak $id") }
            .execute(
                toResult = SoftDeleteSak.Result::softDeleteSak,
                toSuccess = { it as? SoftDeleteSakVellykket },
                onError = { res, err -> Feil.softDeleteSak(id, res, err) },
            )
            .id
            .also { logger.info("Slettet sak $it") }

    suspend fun softDeleteSakByGrupperingsid(grupperingsid: String, merkelapp: String): ID =
        SoftDeleteSakByGrupperingsid(
            variables = SoftDeleteSakByGrupperingsid.Variables(
                grupperingsid = grupperingsid,
                merkelapp = merkelapp,
            ),
        )
            .also { logger.info("Forsøker å slette sak med grupperingsid $grupperingsid og merkelapp $merkelapp") }
            .execute(
                toResult = SoftDeleteSakByGrupperingsid.Result::softDeleteSakByGrupperingsid,
                toSuccess = { it as? SoftDeleteSakByGrupperingsidVellykket },
                onError = { res, err -> Feil.softDeleteSakByGrupperingsid(grupperingsid, res, err) },
            )
            .id
            .also { logger.info("Slettet sak $it") }

    suspend fun hardDeleteSak(id: String): ID =
        HardDeleteSak(
            variables = HardDeleteSak.Variables(id),
        )
            .also { logger.info("Forsøker å slette sak $id") }
            .execute(
                toResult = HardDeleteSak.Result::hardDeleteSak,
                toSuccess = { it as? HardDeleteSakVellykket },
                onError = { res, err -> Feil.hardDeleteSak(id, res, err) },
            )
            .id
            .also { logger.info("Slettet sak $it") }

    suspend fun whoami(): String? =
        Whoami()
            .also { logger.info("Henter 'whoami' info fra arbeidsgiver-notifikasjon-api") }
            .execute(
                toResult = { this },
                toSuccess = { it },
                onError = { _, _ -> throw RuntimeException("Feil ved henting av 'whoami'") },
            )
            .whoami
            .also { logger.info("Whoami: $it") }

    private suspend fun <Data : Any, Result : Any, Success : Result> GraphQLClientRequest<Data>.execute(
        toResult: Data.() -> Result,
        toSuccess: (Result?) -> Success?,
        onError: (Result?, List<GraphQLClientError>?) -> Nothing,
    ): Success {
        val response = graphQLClient.execute(this) {
            bearerAuth(getAccessToken())
        }

        val result = response.data?.toResult()

        return runCatching { toSuccess(result) }
            .getOrNull()
            ?: onError(result, response.errors)
    }
}

internal fun createHttpClient(): HttpClient =
    HttpClient(Apache5)
