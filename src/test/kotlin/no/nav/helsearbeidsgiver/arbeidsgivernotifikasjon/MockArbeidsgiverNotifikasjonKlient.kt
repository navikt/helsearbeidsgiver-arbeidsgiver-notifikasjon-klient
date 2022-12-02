package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf

fun mockArbeidsgiverNotifikasjonKlient(content: String): ArbeidsgiverNotifikasjonKlient {
    val mockEngine = MockEngine {
        respond(
            content = content,
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        )
    }
    return ArbeidsgiverNotifikasjonKlient("https://url", HttpClient(mockEngine)) { "fake token" }
}
