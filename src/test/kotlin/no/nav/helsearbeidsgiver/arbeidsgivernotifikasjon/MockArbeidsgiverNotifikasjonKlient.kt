package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.mockk.every
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.enums.Sendevindu
import no.nav.helsearbeidsgiver.utils.test.mock.mockStatic

fun mockArbeidsgiverNotifikasjonKlient(
    content: String,
    altinn3Ressurs: Altinn3Ressurs = Altinn3Ressurs.INNTEKTSMELDING,
): ArbeidsgiverNotifikasjonKlient {
    val mockEngine =
        MockEngine {
            respond(
                content = content,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }

    return mockStatic(::createHttpClient) {
        every { createHttpClient() } returns HttpClient(mockEngine)
        ArbeidsgiverNotifikasjonKlient("https://url", altinn3Ressurs, { "fake token" }, Sendevindu.NKS_AAPNINGSTID)
    }
}
