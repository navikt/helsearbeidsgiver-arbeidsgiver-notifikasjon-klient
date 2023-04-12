package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

import kotlinx.coroutines.runBlocking
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.enums.SaksStatus
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ArbeidsgiverNotifikasjonKlientTest() {

    @Test
    fun `Forventer gyldig respons fra whoami`() {
        val response = readResource("whoami/gyldig.json")
        val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)
        val resultat = runBlocking { arbeidsgiverNotifikasjonKlient.whoami() }
        val expected = "ut laborum aut laborum quas eos maxime"
        assertEquals(expected, resultat)
    }

    @Test
    fun `Forventer gyldig respons fra softDeleteSak`() {
        val response = readResource("softDeleteSak/gyldig.json")
        val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)
        val resultat = runBlocking { arbeidsgiverNotifikasjonKlient.softDeleteSak("id") }
        val expected = "1"
        assertEquals(expected, resultat)
    }

    @Test
    fun `Forventer gyldig respons fra hardDeleteSak`() {
        val response = readResource("hardDeleteSak/gyldig.json")
        val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)
        val resultat = runBlocking { arbeidsgiverNotifikasjonKlient.hardDeleteSak("id") }
        val expected = "1"
        assertEquals(expected, resultat)
    }

    @Test
    fun `Forventer gyldig respons fra nyStatusSak`() {
        val response = readResource("nyStatusSak/gyldig.json")
        val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)
        val resultat = runBlocking {
            arbeidsgiverNotifikasjonKlient.nyStatusSak(
                "id",
                "https://test.no",
                SaksStatus.FERDIG,
                "Ny statustekst"
            )
        }
        val expected = "1"
        assertEquals(expected, resultat)
    }

    @Test
    fun `Forventer gyldig respons fra softDeleteSakByGrupperingsid`() {
        val response = readResource("softDeleteSakByGrupperingsid/gyldig.json")
        val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)
        val resultat = runBlocking { arbeidsgiverNotifikasjonKlient.softDeleteSakByGrupperingsid("id", "M") }
        val expected = "1"
        assertEquals(expected, resultat)
    }

    @Test
    fun `Forventer gyldig respons fra opprettNyOppgave`() {
        val response = readResource("nyOppgave/gyldig.json")
        val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

        val resultat = runBlocking {
            arbeidsgiverNotifikasjonKlient.opprettNyOppgave(
                eksternId = "id",
                lenke = "https://lenke.no",
                tekst = "test",
                virksomhetsnummer = "874568112",
                merkelapp = "Refusjon",
                tidspunkt = LocalDateTime.now().toString(),
                grupperingsid = null
            )
        }

        val expected = "1"
        assertEquals(expected, resultat)
    }

    @Test
    fun `Forventer gyldig respons fra opprettNySak`() {
        val response = readResource("opprettNySak/gyldig.json")
        val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)
        val resultat = runBlocking {
            arbeidsgiverNotifikasjonKlient.opprettNySak(
                grupperingsid = "id",
                lenke = "https://lenke.no",
                tittel = "test",
                virksomhetsnummer = "874568112",
                merkelapp = "Refusjon",
                harddeleteOm = "P1Y",
                statusTekst = "Ny status"
            )
        }
        val expected = "1"
        assertEquals(expected, resultat)
    }

    @Test
    fun `UgyldigMerkelapp respons fra opprettNyOppgave`() {
        val response = readResource("nyOppgave/ugyldigMerkelapp.json")
        val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)
        assertFailsWith(
            exceptionClass = OpprettNyOppgaveException::class,
            block = {
                runBlocking {
                    arbeidsgiverNotifikasjonKlient.opprettNyOppgave(
                        eksternId = "id",
                        lenke = "https://lenke.no",
                        tekst = "test",
                        virksomhetsnummer = "874568112",
                        merkelapp = "Refusjon",
                        tidspunkt = LocalDateTime.now().toString(),
                        grupperingsid = null
                    )
                }
            }
        )
    }
}

private fun readResource(filename: String) =
    ClassLoader.getSystemResource("responses/$filename").readText()
