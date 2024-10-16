package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

import kotlinx.coroutines.runBlocking
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.enums.SaksStatus
import no.nav.helsearbeidsgiver.utils.test.resource.readResource
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.days

class ArbeidsgiverNotifikasjonKlientTest {
    @Test
    fun `Forventer gyldig respons fra opprettNySak`() {
        val response = "responses/opprettNySak/gyldig.json".readResource()
        val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

        val resultat =
            runBlocking {
                arbeidsgiverNotifikasjonKlient.opprettNySak(
                    virksomhetsnummer = "874568112",
                    merkelapp = "Refusjon",
                    grupperingsid = "id",
                    lenke = "https://lenke.no",
                    tittel = "test",
                    statusTekst = "Ny status",
                    initiellStatus = SaksStatus.UNDER_BEHANDLING,
                    harddeleteOm = 180.days,
                )
            }
        val expected = "1"
        assertEquals(expected, resultat)
    }

    @Test
    fun `Forventer gyldig respons fra nyStatusSak`() {
        val response = "responses/nyStatusSak/gyldig.json".readResource()
        val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

        assertDoesNotThrow {
            runBlocking {
                arbeidsgiverNotifikasjonKlient.nyStatusSak(
                    id = "id",
                    status = SaksStatus.FERDIG,
                    statusTekst = "Ny statustekst",
                    nyLenkeTilSak = "https://test.no",
                )
            }
        }
    }

    @Test
    fun `nyStatusSak - dersom sak ikke finnes så kastes egendefinert exception`() {
        val response = "responses/nyStatusSak/finnesIkke.json".readResource()
        val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

        assertThrows<SakEllerOppgaveFinnesIkkeException> {
            runBlocking {
                arbeidsgiverNotifikasjonKlient.nyStatusSak(
                    id = "mock id",
                    status = SaksStatus.FERDIG,
                    statusTekst = "mock statusTekst",
                    nyLenkeTilSak = "mock nyLenke",
                )
            }
        }
    }

    @Test
    fun `Forventer gyldig respons fra nyStatusSakByGrupperingsid`() {
        val response = "responses/nyStatusSakByGrupperingsid/gyldig.json".readResource()
        val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

        assertDoesNotThrow {
            runBlocking {
                arbeidsgiverNotifikasjonKlient.nyStatusSakByGrupperingsid(
                    grupperingsid = "mock grupperingsid",
                    merkelapp = "mock merkelapp",
                    status = SaksStatus.FERDIG,
                    statusTekst = "mock statustekst",
                    nyLenke = "mock nyLenke",
                )
            }
        }
    }

    @Test
    fun `nyStatusSakByGrupperingsid - dersom sak ikke finnes så kastes egendefinert exception`() {
        val response = "responses/nyStatusSakByGrupperingsid/finnesIkke.json".readResource()
        val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

        assertThrows<SakEllerOppgaveFinnesIkkeException> {
            runBlocking {
                arbeidsgiverNotifikasjonKlient.nyStatusSakByGrupperingsid(
                    grupperingsid = "mock grupperingsid",
                    merkelapp = "mock merkelapp",
                    status = SaksStatus.FERDIG,
                    statusTekst = "mock statustekst",
                    nyLenke = "mock nyLenke",
                )
            }
        }
    }

    @Test
    fun `Forventer gyldig respons fra opprettNyOppgave`() {
        val response = "responses/nyOppgave/gyldig.json".readResource()
        val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

        val resultat =
            runBlocking {
                arbeidsgiverNotifikasjonKlient.opprettNyOppgave(
                    eksternId = "id",
                    lenke = "https://lenke.no",
                    tekst = "test",
                    virksomhetsnummer = "874568112",
                    merkelapp = "Refusjon",
                    tidspunkt = LocalDateTime.now().toString(),
                    grupperingsid = null,
                    varslingTittel = "Du har fått oppgave",
                    varslingInnhold = "Logg på nav.no",
                )
            }

        val expected = "1"
        assertEquals(expected, resultat)
    }

    @Test
    fun `UgyldigMerkelapp respons fra opprettNyOppgave`() {
        val response = "responses/nyOppgave/ugyldigMerkelapp.json".readResource()
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
                        grupperingsid = null,
                        varslingTittel = "Du har fått oppgave",
                        varslingInnhold = "Logg på nav.no",
                    )
                }
            },
        )
    }

    @Test
    fun `Forventer gyldig respons fra oppgaveUtfoert`() {
        val response = "responses/oppgaveUtfoert/gyldig.json".readResource()
        val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

        assertDoesNotThrow {
            runBlocking {
                arbeidsgiverNotifikasjonKlient.oppgaveUtfoert(
                    id = "mock id",
                )
            }
        }
    }

    @Test
    fun `oppgaveUtfoert - dersom sak ikke finnes så kastes egendefinert exception`() {
        val response = "responses/oppgaveUtfoert/finnesIkke.json".readResource()
        val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

        assertThrows<SakEllerOppgaveFinnesIkkeException> {
            runBlocking {
                arbeidsgiverNotifikasjonKlient.oppgaveUtfoert(
                    id = "mock id",
                )
            }
        }
    }

    @Test
    fun `Forventer gyldig respons fra oppgaveUtfoertByEksternIdV2`() {
        val response = "responses/oppgaveUtfoertByEksternIdV2/gyldig.json".readResource()
        val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

        assertDoesNotThrow {
            runBlocking {
                arbeidsgiverNotifikasjonKlient.oppgaveUtfoertByEksternIdV2(
                    eksternId = "mock eksternId",
                    merkelapp = "mock merkelapp",
                    nyLenke = "mock nyLenke",
                )
            }
        }
    }

    @Test
    fun `oppgaveUtfoertByEksternIdV2 - dersom sak ikke finnes så kastes egendefinert exception`() {
        val response = "responses/oppgaveUtfoertByEksternIdV2/finnesIkke.json".readResource()
        val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

        assertThrows<SakEllerOppgaveFinnesIkkeException> {
            runBlocking {
                arbeidsgiverNotifikasjonKlient.oppgaveUtfoertByEksternIdV2(
                    eksternId = "mock eksternId",
                    merkelapp = "mock merkelapp",
                    nyLenke = "mock nyLenke",
                )
            }
        }
    }

    @Test
    fun `Forventer gyldig respons fra softDeleteSak`() {
        val response = "responses/softDeleteSak/gyldig.json".readResource()
        val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

        assertDoesNotThrow {
            runBlocking { arbeidsgiverNotifikasjonKlient.softDeleteSak("id") }
        }
    }

    @Test
    fun `Forventer gyldig respons fra softDeleteSakByGrupperingsid`() {
        val response = "responses/softDeleteSakByGrupperingsid/gyldig.json".readResource()
        val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

        assertDoesNotThrow {
            runBlocking { arbeidsgiverNotifikasjonKlient.softDeleteSakByGrupperingsid("id", "M") }
        }
    }

    @Test
    fun `Forventer gyldig respons fra hardDeleteSak`() {
        val response = "responses/hardDeleteSak/gyldig.json".readResource()
        val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

        assertDoesNotThrow {
            runBlocking { arbeidsgiverNotifikasjonKlient.hardDeleteSak("id") }
        }
    }

    @Test
    fun `Forventer gyldig respons fra oppgaveUtgaatt)`() {
        val response = "responses/oppgaveUtgaatt/gyldig.json".readResource()
        val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

        assertDoesNotThrow {
            runBlocking {
                arbeidsgiverNotifikasjonKlient.oppgaveUtgaatt("mock id")
            }
        }
    }

    @Test
    fun `oppgaveUtgaatt - dersom sak ikke finnes så kastes egendefinert exception`() {
        val response = "responses/oppgaveUtgaatt/finnesIkke.json".readResource()
        val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

        assertThrows<SakEllerOppgaveFinnesIkkeException> {
            runBlocking {
                arbeidsgiverNotifikasjonKlient.oppgaveUtgaatt("mock id")
            }
        }
    }

    @Test
    fun `Forventer gyldig respons fra oppgaveUtgaattByEksternId`() {
        val response = "responses/oppgaveUtgaattByEksternId/gyldig.json".readResource()
        val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

        assertDoesNotThrow {
            runBlocking {
                arbeidsgiverNotifikasjonKlient.oppgaveUtgaattByEksternId("Inntektsmelding sykepenger", "id")
            }
        }
    }

    @Test
    fun `oppgaveUtgaattByEksternId - dersom sak ikke finnes så kastes egendefinert exception`() {
        val response = "responses/oppgaveUtgaattByEksternId/finnesIkke.json".readResource()
        val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

        assertThrows<SakEllerOppgaveFinnesIkkeException> {
            runBlocking {
                arbeidsgiverNotifikasjonKlient.oppgaveUtgaattByEksternId("Inntektsmelding sykepenger", "id")
            }
        }
    }

    @Test
    fun `UgyldigMerkelapp respons fra oppgaveUtgaattByEksternId`() {
        val response = "responses/oppgaveUtgaattByEksternId/ugyldigMerkelapp.json".readResource()
        val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

        assertFailsWith(
            exceptionClass = OppgaveUtgaattByEksternIdException::class,
            block = {
                runBlocking {
                    arbeidsgiverNotifikasjonKlient.oppgaveUtgaattByEksternId(
                        merkelapp = "Refusjon",
                        eksternId = "id",
                    )
                }
            },
        )
    }

    @Test
    fun `Forventer gyldig respons fra whoami`() {
        val response = "responses/whoami/gyldig.json".readResource()
        val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)
        val resultat = runBlocking { arbeidsgiverNotifikasjonKlient.whoami() }
        val expected = "ut laborum aut laborum quas eos maxime"
        assertEquals(expected, resultat)
    }
}
