import kotlinx.coroutines.runBlocking
import no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon.OpprettNyOppgaveException
import no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon.nyStatusSak
import no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon.opprettNyOppgave
import no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon.opprettNySak
import no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon.softDeleteSak
import no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon.softDeleteSakByGrupperingsid
import no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon.whoami
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.enums.SaksStatus
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ArbeidsgiverNotifikasjonKlientTest() {
    private fun getResourceAsText(filename: String) =
        this::class.java.classLoader.getResource("responses/$filename")!!.readText()

    @Test
    fun `Forventer gyldig respons fra whoami`() {
        val response = getResourceAsText("whoami/gyldig.json")
        val arbeidsgiverNotifikasjonKlient = buildClient(response)
        val resultat = runBlocking { arbeidsgiverNotifikasjonKlient.whoami() }
        val expected = "ut laborum aut laborum quas eos maxime"
        assertEquals(expected, resultat)
    }

    @Test
    fun `Forventer gyldig respons fra softDeleteSak`() {
        val response = getResourceAsText("softDeleteSak/gyldig.json")
        val arbeidsgiverNotifikasjonKlient = buildClient(response)
        val resultat = runBlocking { arbeidsgiverNotifikasjonKlient.softDeleteSak("id") }
        val expected = "1"
        assertEquals(expected, resultat)
    }

    @Test
    fun `Forventer gyldig respons fra nyStatusSak`() {
        val response = getResourceAsText("nyStatusSak/gyldig.json")
        val arbeidsgiverNotifikasjonKlient = buildClient(response)
        val resultat = runBlocking { arbeidsgiverNotifikasjonKlient.nyStatusSak("id", SaksStatus.UNDER_BEHANDLING) }
        val expected = "1"
        assertEquals(expected, resultat)
    }

    @Test
    fun `Forventer gyldig respons fra softDeleteSakByGrupperingsid`() {
        val response = getResourceAsText("softDeleteSakByGrupperingsid/gyldig.json")
        val arbeidsgiverNotifikasjonKlient = buildClient(response)
        val resultat = runBlocking { arbeidsgiverNotifikasjonKlient.softDeleteSakByGrupperingsid("id", "M") }
        val expected = "1"
        assertEquals(expected, resultat)
    }

    @Test
    fun `Forventer gyldig respons fra opprettNyOppgave`() {
        val response = getResourceAsText("nyOppgave/gyldig.json")
        val arbeidsgiverNotifikasjonKlient = buildClient(response)

        val resultat = runBlocking {
            arbeidsgiverNotifikasjonKlient.opprettNyOppgave(
                eksternId = "id",
                lenke = "https://lenke.no",
                tekst = "test",
                virksomhetsnummer = "874568112",
                merkelapp = "Refusjon",
                tidspunkt = LocalDateTime.now().toString()
            )
        }

        val expected = "1"
        assertEquals(expected, resultat)
    }

    @Test
    fun `Forventer gyldig respons fra opprettNySak`() {
        val response = getResourceAsText("opprettNySak/gyldig.json")
        val arbeidsgiverNotifikasjonKlient = buildClient(response)
        val resultat = runBlocking {
            arbeidsgiverNotifikasjonKlient.opprettNySak(
                grupperingsid = "id",
                lenke = "https://lenke.no",
                tittel = "test",
                virksomhetsnummer = "874568112",
                merkelapp = "Refusjon"
            )
        }
        val expected = "1"
        assertEquals(expected, resultat)
    }

    @Test
    fun `UgyldigMerkelapp respons fra opprettNyOppgave`() {
        val response = getResourceAsText("nyOppgave/ugyldigMerkelapp.json")
        val arbeidsgiverNotifikasjonKlient = buildClient(response)
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
                        tidspunkt = LocalDateTime.now().toString()
                    )
                }
            }
        )
    }
}
