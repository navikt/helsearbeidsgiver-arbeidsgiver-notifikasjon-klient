package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

import com.expediagroup.graphql.client.types.GraphQLClientError
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.enums.SaksStatus
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.harddeletesak.DefaultHardDeleteSakResultatImplementation
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.harddeletesak.HardDeleteSakResultat
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.harddeletesak.HardDeleteSakVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nysak.DefaultNySakResultatImplementation
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nysak.DuplikatGrupperingsidEtterDelete
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nysak.NySakResultat
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nysak.NySakVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussakbygrupperingsid.DefaultNyStatusSakResultatImplementation
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussakbygrupperingsid.NyStatusSakResultat
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussakbygrupperingsid.NyStatusSakVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveendrepaaminnelsebyeksternid.DefaultOppgaveEndrePaaminnelseResultatImplementation
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutfoertbyeksternidv2.DefaultOppgaveUtfoertResultatImplementation
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutfoertbyeksternidv2.OppgaveUtfoertResultat
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutfoertbyeksternidv2.OppgaveUtfoertVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutgaattbyeksternid.DefaultOppgaveUtgaattResultatImplementation
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutgaattbyeksternid.OppgaveUtgaattResultat
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutgaattbyeksternid.OppgaveUtgaattVellykket
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.DefaultNyOppgaveResultatImplementation
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.NyOppgaveResultat
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.NyOppgaveVellykket
import no.nav.helsearbeidsgiver.utils.log.logger
import no.nav.helsearbeidsgiver.utils.log.sikkerLogger
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.harddeletesak.SakFinnesIkke as SakFinnesIkkeHardDeleteSak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.harddeletesak.UgyldigMerkelapp as UgyldigMerkelappHardDeleteSak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.harddeletesak.UkjentProdusent as UkjentProdusentHardDeleteSak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nysak.DuplikatGrupperingsid as DuplikatGrupperingsidNySak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nysak.UgyldigMerkelapp as UgyldigMerkelappNySak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nysak.UgyldigMottaker as UgyldigMottakerNySak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nysak.UkjentProdusent as UkjentProdusentNySak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nysak.UkjentRolle as UkjentRolleNySak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussakbygrupperingsid.Konflikt as KonfliktNyStatusSakByGrupperingsid
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussakbygrupperingsid.SakFinnesIkke as SakFinnesIkkeNyStatusSakByGrupperingsid
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussakbygrupperingsid.UgyldigMerkelapp as UgyldigMerkelappNyStatusSakByGrupperingsid
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussakbygrupperingsid.UkjentProdusent as UkjentProdusentNyStatusSakByGrupperingsid
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveendrepaaminnelsebyeksternid.NotifikasjonFinnesIkke as NotifikasjonFinnesIkkeOppgaveEndrePaaminnelseByEksternId
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveendrepaaminnelsebyeksternid.OppgaveEndrePaaminnelseResultat as OppgaveEndrePaaminnelseResultatOppgaveEndrePaaminnelseByEksternId
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveendrepaaminnelsebyeksternid.OppgaveEndrePaaminnelseVellykket as OppgaveEndrePaaminnelseVellykketOppgaveEndrePaaminnelseByEksternId
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveendrepaaminnelsebyeksternid.OppgavenErAlleredeUtfoert as OppgavenErAlleredeUtfoertOppgaveEndrePaaminnelseByEksternId
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveendrepaaminnelsebyeksternid.UgyldigMerkelapp as UgyldigMerkelappOppgaveEndrePaaminnelseByEksternId
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveendrepaaminnelsebyeksternid.UgyldigPaaminnelseTidspunkt as UgyldigPaaminnelseTidspunktOppgaveEndrePaaminnelseByEksternId
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveendrepaaminnelsebyeksternid.UkjentProdusent as UkjentProdusentOppgaveEndrePaaminnelseByEksternId
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutfoertbyeksternidv2.NotifikasjonFinnesIkke as NotifikasjonFinnesIkkeOppgaveUtfoertByEksternIdV2
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutfoertbyeksternidv2.UgyldigMerkelapp as UgyldigMerkelappOppgaveUtfoertByEksternIdV2
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutfoertbyeksternidv2.UkjentProdusent as UkjentProdusentOppgaveUtfoertByEksternIdV2
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutgaattbyeksternid.NotifikasjonFinnesIkke as NotifikasjonFinnesIkkeOppgaveUtgaattByEksternId
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutgaattbyeksternid.OppgavenErAlleredeUtfoert as OppgavenErAlleredeUtfoertOppgaveUtgaattByEksternId
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutgaattbyeksternid.UgyldigMerkelapp as UgyldigMerkelappOppgaveUtgaattByEksternId
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutgaattbyeksternid.UkjentProdusent as UkjentProdusentOppgaveUtgaattByEksternId
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.DuplikatEksternIdOgMerkelapp as DuplikatEksternIdOgMerkelappNyOppgave
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.UgyldigMerkelapp as UgyldigMerkelappNyOppgave
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.UgyldigMottaker as UgyldigMottakerNyOppgave
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.UgyldigPaaminnelseTidspunkt as UgyldigPaaminnelseTidspunktNyOppgave
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.UkjentProdusent as UkjentProdusentNyOppgave
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.UkjentRolle as UkjentRolleNyOppgave

internal object Feil {
    private val logger = ArbeidsgiverNotifikasjonKlient::class.logger()
    private val sikkerLogger = sikkerLogger()

    fun nySak(
        grupperingsid: String,
        resultat: NySakResultat,
        feil: List<GraphQLClientError>,
    ): Nothing {
        val feilmelding =
            when (resultat) {
                is DuplikatGrupperingsidNySak ->
                    throw SakEllerOppgaveDuplikatException(resultat.idTilEksisterende, resultat.feilmelding)
                is DuplikatGrupperingsidEtterDelete -> resultat.feilmelding
                is UgyldigMerkelappNySak -> resultat.feilmelding
                is UgyldigMottakerNySak -> resultat.feilmelding
                is UkjentProdusentNySak -> resultat.feilmelding
                is UkjentRolleNySak -> resultat.feilmelding
                is DefaultNySakResultatImplementation,
                is NySakVellykket,
                -> feilmeldingUkjent(feil)
            }

        loggFeilmelding(feilmelding)
        throw OpprettNySakException(grupperingsid, feilmelding)
    }

    fun nyStatusSakByGrupperingsid(
        grupperingsid: String,
        merkelapp: String,
        nyStatus: SaksStatus,
        resultat: NyStatusSakResultat,
        feil: List<GraphQLClientError>,
    ): Nothing {
        val feilmelding =
            when (resultat) {
                is SakFinnesIkkeNyStatusSakByGrupperingsid ->
                    throw SakEllerOppgaveFinnesIkkeException(resultat.feilmelding)

                is KonfliktNyStatusSakByGrupperingsid -> resultat.feilmelding
                is UgyldigMerkelappNyStatusSakByGrupperingsid -> resultat.feilmelding
                is UkjentProdusentNyStatusSakByGrupperingsid -> resultat.feilmelding
                is DefaultNyStatusSakResultatImplementation,
                is NyStatusSakVellykket,
                -> feilmeldingUkjent(feil)
            }

        loggFeilmelding(feilmelding)
        throw NyStatusSakByGrupperingsidException(grupperingsid, merkelapp, nyStatus, feilmelding)
    }

    fun nyOppgave(
        resultat: NyOppgaveResultat,
        feil: List<GraphQLClientError>,
    ): Nothing {
        val feilmelding =
            when (resultat) {
                is DuplikatEksternIdOgMerkelappNyOppgave ->
                    throw SakEllerOppgaveDuplikatException(resultat.idTilEksisterende, resultat.feilmelding)
                is UgyldigMerkelappNyOppgave -> resultat.feilmelding
                is UgyldigMottakerNyOppgave -> resultat.feilmelding
                is UgyldigPaaminnelseTidspunktNyOppgave -> resultat.feilmelding
                is UkjentProdusentNyOppgave -> resultat.feilmelding
                is UkjentRolleNyOppgave -> resultat.feilmelding
                is DefaultNyOppgaveResultatImplementation,
                is NyOppgaveVellykket,
                -> feilmeldingUkjent(feil)
            }

        loggFeilmelding(feilmelding)
        throw OpprettNyOppgaveException(feilmelding)
    }

    fun oppgaveUtfoertByEksternIdV2(
        eksternId: String,
        merkelapp: String,
        resultat: OppgaveUtfoertResultat,
        feil: List<GraphQLClientError>,
    ): Nothing {
        val feilmelding =
            when (resultat) {
                is NotifikasjonFinnesIkkeOppgaveUtfoertByEksternIdV2 ->
                    throw SakEllerOppgaveFinnesIkkeException(resultat.feilmelding)
                is UgyldigMerkelappOppgaveUtfoertByEksternIdV2 -> resultat.feilmelding
                is UkjentProdusentOppgaveUtfoertByEksternIdV2 -> resultat.feilmelding
                is DefaultOppgaveUtfoertResultatImplementation,
                is OppgaveUtfoertVellykket,
                -> feilmeldingUkjent(feil)
            }

        loggFeilmelding(feilmelding)
        throw OppgaveUtfoertByEksternIdV2Exception(eksternId, merkelapp, feilmelding)
    }

    fun oppgaveUtgaattByEksternId(
        eksternId: String,
        resultat: OppgaveUtgaattResultat,
        feil: List<GraphQLClientError>,
    ): Nothing {
        val feilmelding =
            when (resultat) {
                is NotifikasjonFinnesIkkeOppgaveUtgaattByEksternId ->
                    throw SakEllerOppgaveFinnesIkkeException(resultat.feilmelding)
                is OppgavenErAlleredeUtfoertOppgaveUtgaattByEksternId -> resultat.feilmelding
                is UgyldigMerkelappOppgaveUtgaattByEksternId -> resultat.feilmelding
                is UkjentProdusentOppgaveUtgaattByEksternId -> resultat.feilmelding
                is DefaultOppgaveUtgaattResultatImplementation,
                is OppgaveUtgaattVellykket,
                -> feilmeldingUkjent(feil)
            }

        loggFeilmelding(feilmelding)
        throw OppgaveUtgaattByEksternIdException(eksternId, feilmelding)
    }

    fun hardDeleteSak(
        id: String,
        resultat: HardDeleteSakResultat,
        feil: List<GraphQLClientError>,
    ): Nothing {
        val feilmelding =
            when (resultat) {
                is SakFinnesIkkeHardDeleteSak ->
                    throw SakEllerOppgaveFinnesIkkeException(resultat.feilmelding)

                is UgyldigMerkelappHardDeleteSak -> resultat.feilmelding
                is UkjentProdusentHardDeleteSak -> resultat.feilmelding
                is DefaultHardDeleteSakResultatImplementation,
                is HardDeleteSakVellykket,
                -> feilmeldingUkjent(feil)
            }

        loggFeilmelding(feilmelding)
        throw HardDeleteSakException(id, feilmelding)
    }

    fun slettOppgavePaaminnelserByEksternId(
        eksternId: String,
        resultat: OppgaveEndrePaaminnelseResultatOppgaveEndrePaaminnelseByEksternId,
        feil: List<GraphQLClientError>,
    ): Nothing {
        val feilmelding =
            when (resultat) {
                is NotifikasjonFinnesIkkeOppgaveEndrePaaminnelseByEksternId ->
                    throw SakEllerOppgaveFinnesIkkeException(resultat.feilmelding)

                is OppgavenErAlleredeUtfoertOppgaveEndrePaaminnelseByEksternId ->
                    throw OppgaveUtfoertEndrePaaminnelseException(resultat.feilmelding)

                is UgyldigMerkelappOppgaveEndrePaaminnelseByEksternId -> resultat.feilmelding
                is UgyldigPaaminnelseTidspunktOppgaveEndrePaaminnelseByEksternId -> resultat.feilmelding
                is UkjentProdusentOppgaveEndrePaaminnelseByEksternId -> resultat.feilmelding
                is DefaultOppgaveEndrePaaminnelseResultatImplementation,
                is OppgaveEndrePaaminnelseVellykketOppgaveEndrePaaminnelseByEksternId,
                -> feilmeldingUkjent(feil)
            }

        loggFeilmelding(feilmelding)
        throw OppgaveEndrePaaminnelseByEksternIdException(eksternId, feilmelding)
    }

    private fun feilmeldingUkjent(feil: List<GraphQLClientError>): String =
        "Klarte ikke kalle arbeidsgiver-notifikasjon pga. ukjent feil: '$feil'"

    private fun loggFeilmelding(feilmelding: String) {
        "Feilmelding: $feilmelding".also {
            logger.error(it)
            sikkerLogger.error(it)
        }
    }
}

class SakEllerOppgaveDuplikatException(
    val eksisterendeId: String,
    feilmelding: String,
) : Exception("Sak/oppgave finnes fra før. Feilmelding: '$feilmelding'.")

class SakEllerOppgaveFinnesIkkeException(
    feilmelding: String,
) : Exception("Sak/oppgave finnes ikke. Trolig slettet pga. levetid. Feilmelding: '$feilmelding'.")

class OppgaveUtfoertEndrePaaminnelseException(
    feilmelding: String,
) : Exception("Oppgaven er utført, som gjør at det ikke er mulig å endre påminnelsene. Feilmelding: '$feilmelding'.")

class OpprettNySakException(
    grupperingsid: String,
    feilmelding: String,
) : Exception(
        "Opprettelse av ny sak med grupperingsid '$grupperingsid' mot arbeidsgiver-notifikasjon-api feilet: $feilmelding",
    )

class NyStatusSakByGrupperingsidException(
    grupperingsid: String,
    merkelapp: String,
    nyStatus: SaksStatus,
    feilmelding: String,
) : Exception(
        "Ny status '$nyStatus' for sak med grupperingsid '$grupperingsid' og merkelapp '$merkelapp' mot " +
            "arbeidsgiver-notifikasjon-api feilet: $feilmelding",
    )

class OpprettNyOppgaveException(
    feilmelding: String?,
) : Exception("Opprettelse av ny oppgave mot arbeidsgiver-notifikasjon-api feilet: $feilmelding")

class OppgaveUtfoertByEksternIdV2Exception(
    eksternId: String,
    merkelapp: String,
    feilmelding: String,
) : Exception(
        "Utføring av oppgave med ekstern ID '$eksternId' og merkelapp '$merkelapp' mot " +
            "arbeidsgiver-notifikasjon-api feilet: $feilmelding",
    )

class OppgaveUtgaattByEksternIdException(
    eksternId: String,
    feilmelding: String,
) : Exception(
        "Oppdatering av oppgave med eksternId '$eksternId' til " +
            "utgått mot arbeidsgiver-notifikasjon-api feilet: $feilmelding",
    )

class HardDeleteSakException(
    id: String,
    feilmelding: String,
) : Exception("Sletting (hard) av sak med id '$id' mot arbeidsgiver-notifikasjon-api feilet: $feilmelding")

class TomResponseException : Exception("Fikk tom response fra arbeidsgiver-notifikasjon.")

class OppgaveEndrePaaminnelseByEksternIdException(
    eksternId: String,
    feilmelding: String,
) : Exception(
        "Oppdatering av påminnelser for oppgave med eksternId '$eksternId' " +
            "mot arbeidsgiver-notifikasjon-api feilet: $feilmelding",
    )
