package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonObject
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.ID
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.enums.SaksStatus
import no.nav.helsearbeidsgiver.utils.json.parseJson
import no.nav.helsearbeidsgiver.utils.json.toJson
import no.nav.helsearbeidsgiver.utils.test.resource.readResource
import kotlin.time.Duration.Companion.days

class ArbeidsgiverNotifikasjonKlientTest :
    FunSpec({
        context(ArbeidsgiverNotifikasjonKlient::opprettNySak.name) {
            test("vellykket - ny sak") {
                val response = "responses/nySak/vellykket.json".readResource()

                val resultat = Mock.opprettNySak(response)

                resultat shouldBe "269752"
            }

            test("vellykket - ny sak uten lenke") {
                val response = "responses/nySak/vellykket.json".readResource()

                val resultat = Mock.opprettNySak(response, medLenke = false)

                resultat shouldBe "269752"
            }

            test("sak finnes fra før - ny sak") {
                val response = "responses/nySak/duplikatGrupperingsid.json".readResource()

                val error =
                    shouldThrowExactly<SakEllerOppgaveDuplikatException> {
                        Mock.opprettNySak(response)
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

                shouldThrowExactly<OpprettNySakException> {
                    Mock.opprettNySak(response)
                }
            }

            test("både resultat og feilmeldinger i samme respons - ny sak") {
                val response =
                    "responses/nySak/vellykket.json"
                        .readResource()
                        .mergeWithErrorsResponse()

                shouldNotThrowAny {
                    Mock.opprettNySak(response)
                }
            }

            test("feilmeldinger i respons - ny sak") {
                val response = "responses/errors.json".readResource()

                shouldThrowExactly<OpprettNySakException> {
                    Mock.opprettNySak(response)
                }
            }
        }

        context(ArbeidsgiverNotifikasjonKlient::nyStatusSakByGrupperingsid.name) {
            test("vellykket - ny status sak") {
                val response = "responses/nyStatusSakByGrupperingsid/vellykket.json".readResource()

                shouldNotThrowAny {
                    Mock.nyStatusSakByGrupperingsid(response)
                }
            }

            test("sak finnes ikke - ny status sak") {
                val response = "responses/nyStatusSakByGrupperingsid/sakFinnesIkke.json".readResource()

                shouldThrowExactly<SakEllerOppgaveFinnesIkkeException> {
                    Mock.nyStatusSakByGrupperingsid(response)
                }
            }

            withData(
                "konflikt",
                "ugyldigMerkelapp",
                "ukjentProdusent",
                "ukjentFeil",
            ) { jsonFilename ->
                val response = "responses/nyStatusSakByGrupperingsid/$jsonFilename.json".readResource()

                shouldThrowExactly<NyStatusSakByGrupperingsidException> {
                    Mock.nyStatusSakByGrupperingsid(response)
                }
            }

            test("både resultat og feilmeldinger i samme respons - ny status sak") {
                val response =
                    "responses/nyStatusSakByGrupperingsid/vellykket.json"
                        .readResource()
                        .mergeWithErrorsResponse()

                shouldNotThrowAny {
                    Mock.nyStatusSakByGrupperingsid(response)
                }
            }

            test("feilmeldinger i respons - ny status sak") {
                val response = "responses/errors.json".readResource()

                shouldThrowExactly<NyStatusSakByGrupperingsidException> {
                    Mock.nyStatusSakByGrupperingsid(response)
                }
            }
        }

        context(ArbeidsgiverNotifikasjonKlient::opprettNyOppgave.name) {
            test("vellykket - ny oppgave") {
                val response = "responses/nyOppgave/vellykket.json".readResource()

                val resultat = Mock.opprettNyOppgave(response)

                resultat shouldBe "752444"
            }

            test("notifikasjon finnes fra før - ny oppgave") {
                val response = "responses/nyOppgave/duplikatEksternIdOgMerkelapp.json".readResource()

                val error =
                    shouldThrowExactly<SakEllerOppgaveDuplikatException> {
                        Mock.opprettNyOppgave(response)
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

                shouldThrowExactly<OpprettNyOppgaveException> {
                    Mock.opprettNyOppgave(response)
                }
            }

            test("både resultat og feilmeldinger i samme respons - ny oppgave") {
                val response =
                    "responses/nyOppgave/vellykket.json"
                        .readResource()
                        .mergeWithErrorsResponse()

                shouldNotThrowAny {
                    Mock.opprettNyOppgave(response)
                }
            }

            test("feilmeldinger i respons - ny oppgave") {
                val response = "responses/errors.json".readResource()

                shouldThrowExactly<OpprettNyOppgaveException> {
                    Mock.opprettNyOppgave(response)
                }
            }
        }

        context(ArbeidsgiverNotifikasjonKlient::oppgaveUtfoertByEksternIdV2.name) {
            test("vellykket - oppgave utfoert") {
                val response = "responses/oppgaveUtfoertByEksternIdV2/vellykket.json".readResource()

                shouldNotThrowAny {
                    Mock.oppgaveUtfoertByEksternIdV2(response)
                }
            }

            test("notifikasjon finnes ikke - oppgave utfoert") {
                val response = "responses/oppgaveUtfoertByEksternIdV2/notifikasjonFinnesIkke.json".readResource()

                shouldThrowExactly<SakEllerOppgaveFinnesIkkeException> {
                    Mock.oppgaveUtfoertByEksternIdV2(response)
                }
            }

            withData(
                "ugyldigMerkelapp",
                "ukjentProdusent",
                "ukjentFeil",
            ) { jsonFilename ->
                val response = "responses/oppgaveUtfoertByEksternIdV2/$jsonFilename.json".readResource()

                shouldThrowExactly<OppgaveUtfoertByEksternIdV2Exception> {
                    Mock.oppgaveUtfoertByEksternIdV2(response)
                }
            }

            test("både resultat og feilmeldinger i samme respons - oppgave utfoert") {
                val response =
                    "responses/oppgaveUtfoertByEksternIdV2/vellykket.json"
                        .readResource()
                        .mergeWithErrorsResponse()

                shouldNotThrowAny {
                    Mock.oppgaveUtfoertByEksternIdV2(response)
                }
            }

            test("feilmeldinger i respons - oppgave utfoert") {
                val response = "responses/errors.json".readResource()

                shouldThrowExactly<OppgaveUtfoertByEksternIdV2Exception> {
                    Mock.oppgaveUtfoertByEksternIdV2(response)
                }
            }
        }

        context(ArbeidsgiverNotifikasjonKlient::oppgaveUtgaattByEksternId.name) {
            test("vellykket - oppgave utgaatt") {
                val response = "responses/oppgaveUtgaattByEksternId/vellykket.json".readResource()

                shouldNotThrowAny {
                    Mock.oppgaveUtgaattByEksternId(response)
                }
            }

            test("notifikasjon finnes ikke - oppgave utgaatt") {
                val response = "responses/oppgaveUtgaattByEksternId/notifikasjonFinnesIkke.json".readResource()

                shouldThrowExactly<SakEllerOppgaveFinnesIkkeException> {
                    Mock.oppgaveUtgaattByEksternId(response)
                }
            }

            withData(
                "oppgavenErAlleredeUtfoert",
                "ugyldigMerkelapp",
                "ukjentProdusent",
                "ukjentFeil",
            ) { jsonFilename ->
                val response = "responses/oppgaveUtgaattByEksternId/$jsonFilename.json".readResource()

                shouldThrowExactly<OppgaveUtgaattByEksternIdException> {
                    Mock.oppgaveUtgaattByEksternId(response)
                }
            }

            test("både resultat og feilmeldinger i samme respons - oppgave utgaatt") {
                val response =
                    "responses/oppgaveUtgaattByEksternId/vellykket.json"
                        .readResource()
                        .mergeWithErrorsResponse()

                shouldNotThrowAny {
                    Mock.oppgaveUtgaattByEksternId(response)
                }
            }

            test("feilmeldinger i respons - oppgave utgaatt") {
                val response = "responses/errors.json".readResource()

                shouldThrowExactly<OppgaveUtgaattByEksternIdException> {
                    Mock.oppgaveUtgaattByEksternId(response)
                }
            }
        }

        context(ArbeidsgiverNotifikasjonKlient::hardDeleteSak.name) {
            test("vellykket - hard delete sak") {
                val response = "responses/hardDeleteSak/vellykket.json".readResource()

                shouldNotThrowAny {
                    Mock.hardDeleteSak(response)
                }
            }

            test("sak finnes ikke - hard delete sak") {
                val response = "responses/hardDeleteSak/sakFinnesIkke.json".readResource()

                shouldThrowExactly<SakEllerOppgaveFinnesIkkeException> {
                    Mock.hardDeleteSak(response)
                }
            }

            withData(
                "ugyldigMerkelapp",
                "ukjentProdusent",
                "ukjentFeil",
            ) { jsonFilename ->
                val response = "responses/hardDeleteSak/$jsonFilename.json".readResource()

                shouldThrowExactly<HardDeleteSakException> {
                    Mock.hardDeleteSak(response)
                }
            }

            test("både resultat og feilmeldinger i samme respons - hard delete sak") {
                val response =
                    "responses/hardDeleteSak/vellykket.json"
                        .readResource()
                        .mergeWithErrorsResponse()

                shouldNotThrowAny {
                    Mock.hardDeleteSak(response)
                }
            }

            test("feilmeldinger i respons - hard delete sak") {
                val response = "responses/errors.json".readResource()

                shouldThrowExactly<HardDeleteSakException> {
                    Mock.hardDeleteSak(response)
                }
            }
        }

        context(ArbeidsgiverNotifikasjonKlient::hardDeleteSakByGrupperingsid.name) {
            test("vellykket - hard delete sak med grupperingsid") {
                val response = "responses/hardDeleteSakByGrupperingsid/vellykket.json".readResource()

                shouldNotThrowAny {
                    Mock.hardDeleteSakByGrupperingsid(response)
                }
            }
            test("sak finnes ikke - hard delete sak med grupperingsid") {
                val response = "responses/hardDeleteSakByGrupperingsid/sakFinnesIkke.json".readResource()

                shouldThrowExactly<SakEllerOppgaveFinnesIkkeException> {
                    Mock.hardDeleteSakByGrupperingsid(response)
                }
            }

            withData(
                "ugyldigMerkelapp",
                "ukjentProdusent",
            ) { jsonFilename ->
                val response = "responses/hardDeleteSakByGrupperingsid/$jsonFilename.json".readResource()

                shouldThrowExactly<HardDeleteSakByGrupperingsidException> {
                    Mock.hardDeleteSakByGrupperingsid(response)
                }
            }

            test("både resultat og feilmeldinger i samme respons - hard delete sak med grupperingsid") {
                val response =
                    "responses/hardDeleteSakByGrupperingsid/vellykket.json"
                        .readResource()
                        .mergeWithErrorsResponse()

                shouldNotThrowAny {
                    Mock.hardDeleteSakByGrupperingsid(response)
                }
            }

            test("feilmeldinger i respons - hard delete sak med grupperingsid") {
                val response = "responses/errors.json".readResource()

                shouldThrowExactly<HardDeleteSakByGrupperingsidException> {
                    Mock.hardDeleteSakByGrupperingsid(response)
                }
            }
        }

        context(ArbeidsgiverNotifikasjonKlient::slettOppgavePaaminnelserByEksternId.name) {
            test("vellykket - slett oppgavepåminnelse") {
                val response = "responses/oppgaveEndrePaaminnelseByEksternId/vellykket.json".readResource()

                shouldNotThrowAny {
                    Mock.slettOppgavePaaminnelserByEksternId(response)
                }
            }

            test("notifikasjon finnes ikke - slett oppgavepåminnelse") {
                val response = "responses/oppgaveEndrePaaminnelseByEksternId/notifikasjonFinnesIkke.json".readResource()

                shouldThrowExactly<SakEllerOppgaveFinnesIkkeException> {
                    Mock.slettOppgavePaaminnelserByEksternId(response)
                }
            }

            test("oppgave utført - slett oppgavepåminnelse") {
                val response = "responses/oppgaveEndrePaaminnelseByEksternId/oppgavenErAlleredeUtfoert.json".readResource()

                shouldThrowExactly<OppgaveAlleredeUtfoertException> {
                    Mock.slettOppgavePaaminnelserByEksternId(response)
                }
            }

            withData(
                "ugyldigPaaminnelseTidspunkt",
                "ugyldigMerkelapp",
                "ukjentProdusent",
                "ukjentFeil",
            ) { jsonFilename ->
                val response = "responses/oppgaveEndrePaaminnelseByEksternId/$jsonFilename.json".readResource()

                shouldThrowExactly<OppgaveEndrePaaminnelseByEksternIdException> {
                    Mock.slettOppgavePaaminnelserByEksternId(response)
                }
            }

            test("både resultat og feilmeldinger i samme respons - slett oppgavepåminnelse") {
                val response =
                    "responses/oppgaveEndrePaaminnelseByEksternId/vellykket.json"
                        .readResource()
                        .mergeWithErrorsResponse()

                shouldNotThrowAny {
                    Mock.slettOppgavePaaminnelserByEksternId(response)
                }
            }

            test("feilmeldinger i respons - slett oppgavepåminnelse") {
                val response = "responses/errors.json".readResource()

                shouldThrowExactly<OppgaveEndrePaaminnelseByEksternIdException> {
                    Mock.slettOppgavePaaminnelserByEksternId(response)
                }
            }
        }

        context(ArbeidsgiverNotifikasjonKlient::endreOppgavePaaminnelserByEksternId.name) {
            test("vellykket - endre oppgavepåminnelse") {
                val response = "responses/oppgaveEndrePaaminnelseByEksternId/vellykket.json".readResource()

                shouldNotThrowAny {
                    Mock.endreOppgavePaaminnelserByEksternId(response)
                }
            }

            test("notifikasjon finnes ikke - endre oppgavepåminnelse") {
                val response = "responses/oppgaveEndrePaaminnelseByEksternId/notifikasjonFinnesIkke.json".readResource()

                shouldThrowExactly<SakEllerOppgaveFinnesIkkeException> {
                    Mock.endreOppgavePaaminnelserByEksternId(response)
                }
            }

            test("oppgave utført - endre oppgavepåminnelse") {
                val response = "responses/oppgaveEndrePaaminnelseByEksternId/oppgavenErAlleredeUtfoert.json".readResource()

                shouldThrowExactly<OppgaveAlleredeUtfoertException> {
                    Mock.endreOppgavePaaminnelserByEksternId(response)
                }
            }

            withData(
                "ugyldigPaaminnelseTidspunkt",
                "ugyldigMerkelapp",
                "ukjentProdusent",
                "ukjentFeil",
            ) { jsonFilename ->
                val response = "responses/oppgaveEndrePaaminnelseByEksternId/$jsonFilename.json".readResource()

                shouldThrowExactly<OppgaveEndrePaaminnelseByEksternIdException> {
                    Mock.endreOppgavePaaminnelserByEksternId(response)
                }
            }

            test("både resultat og feilmeldinger i samme respons - endre oppgavepåminnelse") {
                val response =
                    "responses/oppgaveEndrePaaminnelseByEksternId/vellykket.json"
                        .readResource()
                        .mergeWithErrorsResponse()

                shouldNotThrowAny {
                    Mock.endreOppgavePaaminnelserByEksternId(response)
                }
            }

            test("feilmeldinger i respons - endre oppgavepåminnelse") {
                val response = "responses/errors.json".readResource()

                shouldThrowExactly<OppgaveEndrePaaminnelseByEksternIdException> {
                    Mock.endreOppgavePaaminnelserByEksternId(response)
                }
            }
        }
    })

private object Mock {
    suspend fun opprettNySak(
        response: String,
        medLenke: Boolean = true,
    ): ID =
        mockArbeidsgiverNotifikasjonKlient(response).opprettNySak(
            virksomhetsnummer = "mock virksomhetsnummer",
            grupperingsid = "mock grupperingsid",
            merkelapp = "mock merkelapp",
            lenke = if (medLenke) "mock lenke" else null,
            tittel = "mock tittel",
            statusTekst = "mock statusTekst",
            tilleggsinfo = "mock tilleggsinfo",
            initiellStatus = SaksStatus.UNDER_BEHANDLING,
            hardDeleteOm = 10.days,
        )

    suspend fun nyStatusSakByGrupperingsid(response: String) {
        mockArbeidsgiverNotifikasjonKlient(response).nyStatusSakByGrupperingsid(
            grupperingsid = "mock grupperingsid",
            merkelapp = "mock merkelapp",
            status = SaksStatus.FERDIG,
            statusTekst = "mock statustekst",
            nyLenke = "mock nyLenke",
            tidspunkt = "mock tidspunkt",
            hardDeleteOm = 2.days,
        )
    }

    suspend fun opprettNyOppgave(response: String): ID =
        mockArbeidsgiverNotifikasjonKlient(response).opprettNyOppgave(
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

    suspend fun oppgaveUtfoertByEksternIdV2(response: String) {
        mockArbeidsgiverNotifikasjonKlient(response).oppgaveUtfoertByEksternIdV2(
            eksternId = "mock eksternId",
            merkelapp = "mock merkelapp",
            nyLenke = "mock nyLenke",
        )
    }

    suspend fun oppgaveUtgaattByEksternId(response: String) {
        mockArbeidsgiverNotifikasjonKlient(response).oppgaveUtgaattByEksternId(
            merkelapp = "mock merkelapp",
            eksternId = "mock eksternId",
            nyLenke = "mock nyLenke",
        )
    }

    suspend fun hardDeleteSak(response: String) {
        mockArbeidsgiverNotifikasjonKlient(response).hardDeleteSak(
            id = "mock id",
        )
    }

    suspend fun hardDeleteSakByGrupperingsid(response: String) {
        mockArbeidsgiverNotifikasjonKlient(response).hardDeleteSakByGrupperingsid(
            grupperingsid = "mock id",
            merkelapp = "mock merkelapp",
        )
    }

    suspend fun slettOppgavePaaminnelserByEksternId(response: String) {
        mockArbeidsgiverNotifikasjonKlient(response).slettOppgavePaaminnelserByEksternId(
            merkelapp = "mock merkelapp",
            eksternId = "mock eksternId",
        )
    }

    suspend fun endreOppgavePaaminnelserByEksternId(response: String) {
        mockArbeidsgiverNotifikasjonKlient(response).endreOppgavePaaminnelserByEksternId(
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

private fun String.mergeWithErrorsResponse(): String {
    val result = parseJson() as JsonObject
    val errors = "responses/errors.json".readResource().parseJson() as JsonObject

    return (result + errors).toJson().toString()
}
