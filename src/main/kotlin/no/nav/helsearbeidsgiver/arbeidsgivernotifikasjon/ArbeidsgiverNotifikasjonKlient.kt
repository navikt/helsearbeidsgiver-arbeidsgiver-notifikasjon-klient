package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import org.slf4j.LoggerFactory
import java.net.URL

class ArbeidsgiverNotifikasjonKlient(
    url: URL,
    httpClient: HttpClient,
    private val getAccessToken: () -> String
) {
    internal val logger = LoggerFactory.getLogger(this::class.java)

    private val graphQLClient = GraphQLKtorClient(
        url = url,
        httpClient = httpClient
    )

    suspend fun <T : Any> execute(query: GraphQLClientRequest<T>): GraphQLClientResponse<T> =
        graphQLClient.execute(query) {
            bearerAuth(getAccessToken())
        }
}
