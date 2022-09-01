package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.Whoami

suspend fun ArbeidsgiverNotifikasjonKlient.whoami(): String? {
    val query = Whoami()

    logger.info("Henter 'whoami' info fra arbeidsgiver-notifikasjon-api")

    val resultat = execute(query)

    logger.info("Whoami: ${resultat.data?.whoami}")
    return resultat.data?.whoami
}
