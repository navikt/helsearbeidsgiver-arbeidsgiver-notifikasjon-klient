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
            val response = "responses/nySak/vellykket.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            val resultat =
                arbeidsgiverNotifikasjonKlient.opprettNySak(
                    virksomhetsnummer = "mock virksomhetsnummer",
                    grupperingsid = "mock grupperingsid",
                    merkelapp = "mock merkelapp",
                    lenke = "mock lenke",
                    tittel = "mock tittel",
                    statusTekst = "mock statusTekst",
                    tilleggsinfo = "mock tilleggsinfo",
                    initiellStatus = SaksStatus.UNDER_BEHANDLING,
                    hardDeleteOm = 10.days,
                )

            resultat shouldBe "269752"
        }

        test("vellykket - ny sak uten lenke") {
            val response = "responses/nySak/vellykket.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            val resultat =
                arbeidsgiverNotifikasjonKlient.opprettNySak(
                    virksomhetsnummer = "mock virksomhetsnummer",
                    grupperingsid = "mock grupperingsid",
                    merkelapp = "mock merkelapp",
                    lenke = null,
                    tittel = "mock tittel",
                    statusTekst = "mock statusTekst",
                    tilleggsinfo = "mock tilleggsinfo",
                    initiellStatus = SaksStatus.UNDER_BEHANDLING,
                    hardDeleteOm = 10.days,
                )

            resultat shouldBe "269752"
        }

        test("sak finnes fra før - ny sak") {
            val response = "responses/nySak/duplikatGrupperingsid.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            val error =
                shouldThrowExactly<SakEllerOppgaveDuplikatException> {
                    arbeidsgiverNotifikasjonKlient.opprettNySak(
                        virksomhetsnummer = "mock virksomhetsnummer",
                        grupperingsid = "mock grupperingsid",
                        merkelapp = "mock merkelapp",
                        lenke = "mock lenke",
                        tittel = "mock tittel",
                        statusTekst = "mock statusTekst",
                        tilleggsinfo = "mock tilleggsinfo",
                        initiellStatus = SaksStatus.UNDER_BEHANDLING,
                        hardDeleteOm = 10.days,
                    )
                }

            error.eksisterendeId shouldBe "1221"
        }

        withData(
            "duplikatGrupperingsidEtterDelete",
            "ugyldigMerkelapp",
            "ugyldigMottaker",
            "ukjentProdusent",
            "ukjentRolle",
            "ukjentFeil",
        ) { jsonFilename ->
            val response = "responses/nySak/$jsonFilename.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            shouldThrowExactly<OpprettNySakException> {
                arbeidsgiverNotifikasjonKlient.opprettNySak(
                    virksomhetsnummer = "mock virksomhetsnummer",
                    grupperingsid = "mock grupperingsid",
                    merkelapp = "mock merkelapp",
                    lenke = "mock lenke",
                    tittel = "mock tittel",
                    statusTekst = "mock statusTekst",
                    tilleggsinfo = "mock tilleggsinfo",
                    initiellStatus = SaksStatus.UNDER_BEHANDLING,
                    hardDeleteOm = 10.days,
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
                    virksomhetsnummer = "mock virksomhetsnummer",
                    eksternId = "mock eksternId",
                    grupperingsid = "mock grupperingsid",
                    merkelapp = "mock merkelapp",
                    lenke = "mock lenke",
                    tekst = "mock tekst",
                    tidspunkt = "mock tidspunkt",
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

        test("notifikasjon finnes fra før - ny oppgave") {
            val response = "responses/nyOppgave/duplikatEksternIdOgMerkelapp.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            val error =
                shouldThrowExactly<SakEllerOppgaveDuplikatException> {
                    arbeidsgiverNotifikasjonKlient.opprettNyOppgave(
                        virksomhetsnummer = "mock virksomhetsnummer",
                        eksternId = "mock eksternId",
                        grupperingsid = "mock grupperingsid",
                        merkelapp = "mock merkelapp",
                        lenke = "mock lenke",
                        tekst = "mock tekst",
                        tidspunkt = "mock tidspunkt",
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

            error.eksisterendeId shouldBe "5677"
        }

        withData(
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
                    virksomhetsnummer = "mock virksomhetsnummer",
                    eksternId = "mock eksternId",
                    grupperingsid = "mock grupperingsid",
                    merkelapp = "mock merkelapp",
                    lenke = "mock lenke",
                    tekst = "mock tekst",
                    tidspunkt = "mock tidspunkt",
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

    context(ArbeidsgiverNotifikasjonKlient::softDeleteSakByGrupperingsid.name) {
        test("vellykket - soft delete sak") {
            val response = "responses/softDeleteSakByGrupperingsid/vellykket.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            shouldNotThrowAny {
                arbeidsgiverNotifikasjonKlient.softDeleteSakByGrupperingsid(
                    grupperingsid = "mock id",
                    merkelapp = "heia",
                )
            }
        }
        test("sak finnes ikke - soft delete sak") {
            val response = "responses/softDeleteSakByGrupperingsid/sakFinnesIkke.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            shouldThrowExactly<SakEllerOppgaveFinnesIkkeException> {
                arbeidsgiverNotifikasjonKlient.softDeleteSakByGrupperingsid(
                    grupperingsid = "mock id",
                    merkelapp = "heia",
                )
            }
        }

        withData(
            "ugyldigMerkelapp",
            "ukjentProdusent",
        ) { jsonFilename ->
            val response = "responses/softDeleteSakByGrupperingsid/$jsonFilename.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            shouldThrowExactly<SoftDeleteSakByGrupperingsidException> {
                arbeidsgiverNotifikasjonKlient.softDeleteSakByGrupperingsid(
                    grupperingsid = "mock id",
                    merkelapp = "heia",
                )
            }
        }
    }

    context(ArbeidsgiverNotifikasjonKlient::slettOppgavePaaminnelserByEksternId.name) {
        test("vellykket - slett oppgavepåminnelse") {
            val response = "responses/oppgaveEndrePaaminnelseByEksternId/vellykket.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            shouldNotThrowAny {
                arbeidsgiverNotifikasjonKlient.slettOppgavePaaminnelserByEksternId(
                    merkelapp = "mock merkelapp",
                    eksternId = "mock eksternId",
                )
            }
        }

        test("notifikasjon finnes ikke - slett oppgavepåminnelse") {
            val response = "responses/oppgaveEndrePaaminnelseByEksternId/notifikasjonFinnesIkke.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            shouldThrowExactly<SakEllerOppgaveFinnesIkkeException> {
                arbeidsgiverNotifikasjonKlient.slettOppgavePaaminnelserByEksternId(
                    merkelapp = "mock merkelapp",
                    eksternId = "mock eksternId",
                )
            }
        }

        test("oppgave utført - slett oppgavepåminnelse") {
            val response = "responses/oppgaveEndrePaaminnelseByEksternId/oppgavenErAlleredeUtfoert.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            shouldThrowExactly<OppgaveAlleredeUtfoertException> {
                arbeidsgiverNotifikasjonKlient.slettOppgavePaaminnelserByEksternId(
                    merkelapp = "mock merkelapp",
                    eksternId = "mock eksternId",
                )
            }
        }

        withData(
            "ugyldigPaaminnelseTidspunkt",
            "ugyldigMerkelapp",
            "ukjentProdusent",
            "ukjentFeil",
        ) { jsonFilename ->
            val response = "responses/oppgaveEndrePaaminnelseByEksternId/$jsonFilename.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            shouldThrowExactly<OppgaveEndrePaaminnelseByEksternIdException> {
                arbeidsgiverNotifikasjonKlient.slettOppgavePaaminnelserByEksternId(
                    merkelapp = "mock merkelapp",
                    eksternId = "mock eksternId",
                )
            }
        }
    }

    context(ArbeidsgiverNotifikasjonKlient::endreOppgavePaaminnelserByEksternId.name) {
        test("vellykket - endre oppgavepåminnelse") {
            val response = "responses/oppgaveEndrePaaminnelseByEksternId/vellykket.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            shouldNotThrowAny {
                arbeidsgiverNotifikasjonKlient.endreOppgavePaaminnelserByEksternId(
                    merkelapp = "mock merkelapp",
                    eksternId = "mock eksternId",
                    paaminnelse =
                        Paaminnelse(
                            tittel = "mock tittel",
                            innhold = "mock innhold",
                            tidMellomOppgaveopprettelseOgPaaminnelse = "P28D",
                        ),
                )
            }
        }

        test("notifikasjon finnes ikke - endre oppgavepåminnelse") {
            val response = "responses/oppgaveEndrePaaminnelseByEksternId/notifikasjonFinnesIkke.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            shouldThrowExactly<SakEllerOppgaveFinnesIkkeException> {
                arbeidsgiverNotifikasjonKlient.endreOppgavePaaminnelserByEksternId(
                    merkelapp = "mock merkelapp",
                    eksternId = "mock eksternId",
                    paaminnelse =
                        Paaminnelse(
                            tittel = "mock tittel",
                            innhold = "mock innhold",
                            tidMellomOppgaveopprettelseOgPaaminnelse = "P28D",
                        ),
                )
            }
        }

        test("oppgave utført - endre oppgavepåminnelse") {
            val response = "responses/oppgaveEndrePaaminnelseByEksternId/oppgavenErAlleredeUtfoert.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            shouldThrowExactly<OppgaveAlleredeUtfoertException> {
                arbeidsgiverNotifikasjonKlient.endreOppgavePaaminnelserByEksternId(
                    merkelapp = "mock merkelapp",
                    eksternId = "mock eksternId",
                    paaminnelse =
                        Paaminnelse(
                            tittel = "mock tittel",
                            innhold = "mock innhold",
                            tidMellomOppgaveopprettelseOgPaaminnelse = "P28D",
                        ),
                )
            }
        }

        withData(
            "ugyldigPaaminnelseTidspunkt",
            "ugyldigMerkelapp",
            "ukjentProdusent",
            "ukjentFeil",
        ) { jsonFilename ->
            val response = "responses/oppgaveEndrePaaminnelseByEksternId/$jsonFilename.json".readResource()
            val arbeidsgiverNotifikasjonKlient = mockArbeidsgiverNotifikasjonKlient(response)

            shouldThrowExactly<OppgaveEndrePaaminnelseByEksternIdException> {
                arbeidsgiverNotifikasjonKlient.endreOppgavePaaminnelserByEksternId(
                    merkelapp = "mock merkelapp",
                    eksternId = "mock eksternId",
                    paaminnelse =
                        Paaminnelse(
                            tittel = "mock tittel",
                            innhold = "mock innhold",
                            tidMellomOppgaveopprettelseOgPaaminnelse = "P28D",
                        ),
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
