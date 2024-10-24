package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.enums.SaksStatus
import no.nav.helsearbeidsgiver.utils.test.resource.readResource
import kotlin.time.Duration.Companion.days

class ArbeidsgiverNotifikasjonKlientTest : FunSpec({
    context(ArbeidsgiverNotifikasjonKlient::opprettNySak.name) {
        test("vellykket - ny sak") {
            val response = "responses/opprettNySak/vellykket.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            val resultat =
                arbeidsgiverNotifikasjonKlient.opprettNySak(
                    virksomhetsnummer = "mock virksomhetsnummer",
                    merkelapp = "mock merkelapp",
                    grupperingsid = "mock grupperingsid",
                    lenke = "mock lenke",
                    tittel = "mock tittel",
                    statusTekst = "mock statusTekst",
                    initiellStatus = SaksStatus.UNDER_BEHANDLING,
                    harddeleteOm = 10.days,
                )

            resultat shouldBe "269752"
        }

        withData(
            "duplikatGrupperingsid",
            "ugyldigMerkelapp",
            "ugyldigMottaker",
            "ukjentProdusent",
            "ukjentRolle",
            "ukjentFeil",
        ) { jsonFilename ->
            val response = "responses/opprettNySak/$jsonFilename.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            shouldThrowExactly<OpprettNySakException> {
                arbeidsgiverNotifikasjonKlient.opprettNySak(
                    virksomhetsnummer = "mock virksomhetsnummer",
                    merkelapp = "mock merkelapp",
                    grupperingsid = "mock grupperingsid",
                    lenke = "mock lenke",
                    tittel = "mock tittel",
                    statusTekst = "mock statusTekst",
                    initiellStatus = SaksStatus.UNDER_BEHANDLING,
                    harddeleteOm = 10.days,
                )
            }
        }
    }

    context(ArbeidsgiverNotifikasjonKlient::nyStatusSakByGrupperingsid.name) {
        test("vellykket - ny status sak") {
            val response = "responses/nyStatusSakByGrupperingsid/vellykket.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            shouldNotThrowAny {
                arbeidsgiverNotifikasjonKlient.nyStatusSakByGrupperingsid(
                    grupperingsid = "mock grupperingsid",
                    merkelapp = "mock merkelapp",
                    status = SaksStatus.FERDIG,
                    statusTekst = "mock statustekst",
                    nyLenke = "mock nyLenke",
                    tidspunkt = "mock tidspunkt",
                )
            }
        }

        test("sak finnes ikke - ny status sak") {
            val response = "responses/nyStatusSakByGrupperingsid/sakFinnesIkke.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            shouldThrowExactly<SakEllerOppgaveFinnesIkkeException> {
                arbeidsgiverNotifikasjonKlient.nyStatusSakByGrupperingsid(
                    grupperingsid = "mock grupperingsid",
                    merkelapp = "mock merkelapp",
                    status = SaksStatus.FERDIG,
                    statusTekst = "mock statustekst",
                    nyLenke = "mock nyLenke",
                    tidspunkt = "mock tidspunkt",
                )
            }
        }

        withData(
            "konflikt",
            "ugyldigMerkelapp",
            "ukjentProdusent",
            "ukjentFeil",
        ) { jsonFilename ->
            val response = "responses/nyStatusSakByGrupperingsid/$jsonFilename.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            shouldThrowExactly<NyStatusSakByGrupperingsidException> {
                arbeidsgiverNotifikasjonKlient.nyStatusSakByGrupperingsid(
                    grupperingsid = "mock grupperingsid",
                    merkelapp = "mock merkelapp",
                    status = SaksStatus.FERDIG,
                    statusTekst = "mock statustekst",
                    nyLenke = "mock nyLenke",
                    tidspunkt = "mock tidspunkt",
                )
            }
        }
    }

    context(ArbeidsgiverNotifikasjonKlient::opprettNyOppgave.name) {
        test("vellykket - ny oppgave") {
            val response = "responses/nyOppgave/vellykket.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            val resultat =
                arbeidsgiverNotifikasjonKlient.opprettNyOppgave(
                    eksternId = "mock eksternId",
                    lenke = "mock lenke",
                    tekst = "mock tekst",
                    virksomhetsnummer = "mock virksomhetsnummer",
                    merkelapp = "mock merkelapp",
                    tidspunkt = "mock tidspunkt",
                    grupperingsid = "mock grupperingsid",
                    varslingTittel = "mock varslingTittel",
                    varslingInnhold = "mock varslingInnhold",
                    paaminnelse =
                        Paaminnelse(
                            tittel = "mock tittel",
                            innhold = "mock innhold",
                            tidMellomOppgaveopprettelseOgPaaminnelse = "P10D",
                        ),
                )

            resultat shouldBe "752444"
        }

        withData(
            "duplikatEksternIdOgMerkelapp",
            "ugyldigMerkelapp",
            "ugyldigMottaker",
            "ugyldigPaaminnelseTidspunkt",
            "ukjentProdusent",
            "ukjentRolle",
            "ukjentFeil",
        ) { jsonFilename ->
            val response = "responses/nyOppgave/$jsonFilename.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            shouldThrowExactly<OpprettNyOppgaveException> {
                arbeidsgiverNotifikasjonKlient.opprettNyOppgave(
                    eksternId = "mock eksternId",
                    lenke = "mock lenke",
                    tekst = "mock tekst",
                    virksomhetsnummer = "mock virksomhetsnummer",
                    merkelapp = "mock merkelapp",
                    tidspunkt = "mock tidspunkt",
                    grupperingsid = "mock grupperingsid",
                    varslingTittel = "mock varslingTittel",
                    varslingInnhold = "mock varslingInnhold",
                    paaminnelse =
                        Paaminnelse(
                            tittel = "mock tittel",
                            innhold = "mock innhold",
                            tidMellomOppgaveopprettelseOgPaaminnelse = "P10D",
                        ),
                )
            }
        }
    }

    context(ArbeidsgiverNotifikasjonKlient::oppgaveUtfoertByEksternIdV2.name) {
        test("vellykket - oppgave utfoert") {
            val response = "responses/oppgaveUtfoertByEksternIdV2/vellykket.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            shouldNotThrowAny {
                arbeidsgiverNotifikasjonKlient.oppgaveUtfoertByEksternIdV2(
                    eksternId = "mock eksternId",
                    merkelapp = "mock merkelapp",
                    nyLenke = "mock nyLenke",
                )
            }
        }

        test("notifikasjon finnes ikke - oppgave utfoert") {
            val response = "responses/oppgaveUtfoertByEksternIdV2/notifikasjonFinnesIkke.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            shouldThrowExactly<SakEllerOppgaveFinnesIkkeException> {
                arbeidsgiverNotifikasjonKlient.oppgaveUtfoertByEksternIdV2(
                    eksternId = "mock eksternId",
                    merkelapp = "mock merkelapp",
                    nyLenke = "mock nyLenke",
                )
            }
        }

        withData(
            "ugyldigMerkelapp",
            "ukjentProdusent",
            "ukjentFeil",
        ) { jsonFilename ->
            val response = "responses/oppgaveUtfoertByEksternIdV2/$jsonFilename.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            shouldThrowExactly<OppgaveUtfoertByEksternIdV2Exception> {
                arbeidsgiverNotifikasjonKlient.oppgaveUtfoertByEksternIdV2(
                    eksternId = "mock eksternId",
                    merkelapp = "mock merkelapp",
                    nyLenke = "mock nyLenke",
                )
            }
        }
    }

    context(ArbeidsgiverNotifikasjonKlient::oppgaveUtgaattByEksternId.name) {
        test("vellykket - oppgave utgaatt") {
            val response = "responses/oppgaveUtgaattByEksternId/vellykket.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            shouldNotThrowAny {
                arbeidsgiverNotifikasjonKlient.oppgaveUtgaattByEksternId(
                    merkelapp = "mock merkelapp",
                    eksternId = "mock eksternId",
                    nyLenke = "mock nyLenke",
                )
            }
        }

        test("notifikasjon finnes ikke - oppgave utgatt") {
            val response = "responses/oppgaveUtgaattByEksternId/notifikasjonFinnesIkke.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            shouldThrowExactly<SakEllerOppgaveFinnesIkkeException> {
                arbeidsgiverNotifikasjonKlient.oppgaveUtgaattByEksternId(
                    merkelapp = "mock merkelapp",
                    eksternId = "mock eksternId",
                    nyLenke = "mock nyLenke",
                )
            }
        }

        withData(
            "oppgavenErAlleredeUtfoert",
            "ugyldigMerkelapp",
            "ukjentProdusent",
            "ukjentFeil",
        ) { jsonFilename ->
            val response = "responses/oppgaveUtgaattByEksternId/$jsonFilename.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            shouldThrowExactly<OppgaveUtgaattByEksternIdException> {
                arbeidsgiverNotifikasjonKlient.oppgaveUtgaattByEksternId(
                    merkelapp = "mock merkelapp",
                    eksternId = "mock eksternId",
                    nyLenke = "mock nyLenke",
                )
            }
        }
    }

    context(ArbeidsgiverNotifikasjonKlient::hardDeleteSak.name) {
        test("vellykket - hard delete sak") {
            val response = "responses/hardDeleteSak/vellykket.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            shouldNotThrowAny {
                arbeidsgiverNotifikasjonKlient.hardDeleteSak(
                    id = "mock id",
                )
            }
        }

        test("sak finnes ikke - hard delete sak") {
            val response = "responses/hardDeleteSak/sakFinnesIkke.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            shouldThrowExactly<SakEllerOppgaveFinnesIkkeException> {
                arbeidsgiverNotifikasjonKlient.hardDeleteSak(
                    id = "mock id",
                )
            }
        }

        withData(
            "ugyldigMerkelapp",
            "ukjentProdusent",
            "ukjentFeil",
        ) { jsonFilename ->
            val response = "responses/hardDeleteSak/$jsonFilename.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            shouldThrowExactly<HardDeleteSakException> {
                arbeidsgiverNotifikasjonKlient.hardDeleteSak(
                    id = "mock id",
                )
            }
        }
    }

    test("tom response gir egen feil") {
        val response = "responses/tomResponse.json".readResource()
        val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

        shouldThrowExactly<TomResponseException> {
            // Hvilken metode som kalles er irrelevant
            arbeidsgiverNotifikasjonKlient.hardDeleteSak(
                id = "mock id",
            )
        }
    }
})
