package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders.Authorization
import org.slf4j.LoggerFactory
import java.net.URL

interface AccessTokenProvider {
    fun getToken(): String
}

class ArbeidsgiverNotifikasjonKlient(
    url: URL,
    private val accessTokenProvider: AccessTokenProvider,
    httpClient: HttpClient
) {
    private val graphQLClient = GraphQLKtorClient(
        url = url,
        httpClient = httpClient
    )

    suspend fun <T : Any> execute(query: GraphQLClientRequest<T>): GraphQLClientResponse<T> =
        graphQLClient.execute(query) {
            header(Authorization, "Bearer ${accessTokenProvider.getToken()}")
        }

    val logger: org.slf4j.Logger = LoggerFactory.getLogger(this::class.java)
}
