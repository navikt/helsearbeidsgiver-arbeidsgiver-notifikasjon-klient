import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.features.json.JsonFeature
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import no.nav.helse.arbeidsgiver.integrasjoner.AccessTokenProvider
import no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon.ArbeidsgiverNotifikasjonKlient
import java.net.URL

class AccessTokenProviderMock : AccessTokenProvider {
    override fun getToken(): String = "fake token"
}

fun buildClient(
    response: String,
    status: HttpStatusCode = HttpStatusCode.OK,
    headers: Headers = headersOf(HttpHeaders.ContentType, "application/json")
): ArbeidsgiverNotifikasjonKlient {
    val mockEngine = MockEngine {
        respond(
            content = ByteReadChannel(response),
            status = status,
            headers = headers
        )
    }

    return ArbeidsgiverNotifikasjonKlient(
        URL("https://notifikasjon-fake-produsent-api.labs.nais.io/"),
        AccessTokenProviderMock(),
        HttpClient(mockEngine) { install(JsonFeature) }
    )
}
