package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

import com.expediagroup.graphql.client.types.GraphQLClientError
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.enums.SaksStatus
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.harddeletesak.HardDeleteSakResultat
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussak.NyStatusSakResultat
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutfoert.OppgaveUtfoertResultat
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.NyOppgaveResultat
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnysak.NySakResultat
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.softdeletesak.SoftDeleteSakResultat
import org.slf4j.LoggerFactory
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.harddeletesak.SakFinnesIkke as FinnesIkkeHardDeleteSak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.harddeletesak.UgyldigMerkelapp as UgyldigMerkelappHardDeleteSak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.harddeletesak.UkjentProdusent as UkjentProdusentHardDeleteSak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussak.Konflikt as KonfliktNyStatusSak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussak.SakFinnesIkke as FinnesIkkeNyStatusSak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussakbygrupperingsid.Konflikt as KonfliktNyStatusSakByGrupperingsid
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussakbygrupperingsid.NyStatusSakResultat as NyStatusSakByGrupperingsidResultat
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussakbygrupperingsid.SakFinnesIkke as SakFinnesIkkeNyStatusSakByGrupperingsid
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutfoert.NotifikasjonFinnesIkke as NotifikasjonFinnesIkkeOppgaveUtfoert
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutfoert.UgyldigMerkelapp as UgyldigMerkelappOppgaveUtfoert
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutfoert.UkjentProdusent as UkjentProdusentOppgaveUtfoert
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.DuplikatEksternIdOgMerkelapp as DuplikatEksternIdOgMerkelappNyOppgave
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.UgyldigMerkelapp as UgyldigMerkelappNyOppgave
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.UgyldigMottaker as UgyldigMottakerNyOppgave
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.UkjentProdusent as UkjentProdusentNyOppgave
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.UkjentRolle as UkjentRolleNyOppgave
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnysak.DuplikatGrupperingsid as DuplikatGrupperingsidNySak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnysak.UgyldigMerkelapp as UgyldigMerkelappNySak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnysak.UgyldigMottaker as UgyldigMottakerNySak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnysak.UkjentProdusent as UkjentProdusentNySak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnysak.UkjentRolle as UkjentRolleNySak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.softdeletesak.SakFinnesIkke as SakFinnesIkkeSoftDeleteSak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.softdeletesak.UgyldigMerkelapp as UgyldigMerkelappSoftDeleteSak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.softdeletesak.UkjentProdusent as UkjentProdusentSoftDeleteSak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.softdeletesakbygrupperingsid.SakFinnesIkke as SakFinnesIkkeSoftDeleteSakByGrupperingsid
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.softdeletesakbygrupperingsid.SoftDeleteSakResultat as SoftDeleteSakByGrupperingsidResultat
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.softdeletesakbygrupperingsid.UgyldigMerkelapp as UgyldigMerkelappSoftDeleteSakByGrupperingsid
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.softdeletesakbygrupperingsid.UkjentProdusent as UkjentProdusentSoftDeleteSakByGrupperingsid

internal object Feil {
    private val logger = LoggerFactory.getLogger(ArbeidsgiverNotifikasjonKlient::class.java)

    fun hardDeleteSak(id: String, resultat: HardDeleteSakResultat?, feil: List<GraphQLClientError>?): Nothing {
        val feilmelding = when (resultat) {
            is UgyldigMerkelappHardDeleteSak -> resultat.feilmelding
            is FinnesIkkeHardDeleteSak -> resultat.feilmelding
            is UkjentProdusentHardDeleteSak -> resultat.feilmelding
            else -> null
        }

        if (feilmelding != null) {
            feilmelding.loggFeil()
            throw HardDeleteSakException(id, feilmelding)
        } else {
            logger.error("Kunne ikke harddelete sak $feil: med feil $feil")
            throw HardDeleteSakException(id, feil.ukjentFeil())
        }
    }

    fun nyStatusSak(id: String, resultat: NyStatusSakResultat?, feil: List<GraphQLClientError>?): Nothing {
        val feilmelding = when (resultat) {
            is FinnesIkkeNyStatusSak -> resultat.feilmelding
            is KonfliktNyStatusSak -> resultat.feilmelding
            else -> null
        }

        if (feilmelding != null) {
            feilmelding.loggFeil()
            throw NyStatusSakException(id, feilmelding)
        } else {
            logger.error("Kunne ikke opprette ny sak: $feil")
            throw NyStatusSakException(id, feil.ukjentFeil())
        }
    }

    fun nyStatusSakByGrupperingsid(
        grupperingsid: String,
        nyStatus: SaksStatus,
        resultat: NyStatusSakByGrupperingsidResultat?,
        feil: List<GraphQLClientError>?
    ): Nothing {
        val feilmelding = when (resultat) {
            is SakFinnesIkkeNyStatusSakByGrupperingsid -> resultat.feilmelding
            is KonfliktNyStatusSakByGrupperingsid -> resultat.feilmelding
            else -> null
        }

        if (feilmelding != null) {
            feilmelding.loggFeil()
            throw NyStatusSakByGrupperingsidException(grupperingsid, nyStatus, feilmelding)
        } else {
            logger.error("Kunne ikke opprette ny sak (fra grupperingsid): $feil")
            throw NyStatusSakByGrupperingsidException(grupperingsid, nyStatus, feil.ukjentFeil())
        }
    }

    fun oppgaveUtfoert(id: String, resultat: OppgaveUtfoertResultat?, feil: List<GraphQLClientError>?): Nothing {
        val feilmelding = when (resultat) {
            is UkjentProdusentOppgaveUtfoert -> resultat.feilmelding
            is UgyldigMerkelappOppgaveUtfoert -> resultat.feilmelding
            is NotifikasjonFinnesIkkeOppgaveUtfoert -> resultat.feilmelding
            else -> null
        }

        if (feilmelding != null) {
            feilmelding.loggFeil()
            throw OppgaveUtfoertException(id, feilmelding)
        } else {
            logger.error("Kunne ikke opprette utføre oppgave: $feil")
            throw OppgaveUtfoertException(id, feil.ukjentFeil())
        }
    }

    fun opprettNySak(resultat: NySakResultat?, feil: List<GraphQLClientError>?): Nothing {
        val feilmelding = when (resultat) {
            is UgyldigMerkelappNySak -> resultat.feilmelding
            is UgyldigMottakerNySak -> resultat.feilmelding
            is UkjentProdusentNySak -> resultat.feilmelding
            is UkjentRolleNySak -> resultat.feilmelding
            is DuplikatGrupperingsidNySak -> resultat.feilmelding
            else -> null
        }

        if (feilmelding != null) {
            feilmelding.loggFeil()
            throw OpprettNySakException(feilmelding)
        } else {
            logger.error("Kunne ikke opprette ny sak: $feil")
            throw OpprettNySakException(feil.ukjentFeil())
        }
    }

    fun nyOppgave(resultat: NyOppgaveResultat?, feil: List<GraphQLClientError>?): Nothing {
        val feilmelding = when (resultat) {
            is UgyldigMottakerNyOppgave -> resultat.feilmelding
            is UkjentRolleNyOppgave -> resultat.feilmelding
            is UgyldigMerkelappNyOppgave -> resultat.feilmelding
            is UkjentProdusentNyOppgave -> resultat.feilmelding
            is DuplikatEksternIdOgMerkelappNyOppgave -> resultat.feilmelding
            else -> null
        }

        if (feilmelding != null) {
            feilmelding.loggFeil()
            throw OpprettNyOppgaveException(feilmelding)
        } else {
            logger.error("Kunne ikke opprette ny oppgave: $feil")
            throw OpprettNyOppgaveException(feil.ukjentFeil())
        }
    }

    fun softDeleteSak(id: String, resultat: SoftDeleteSakResultat?, feil: List<GraphQLClientError>?): Nothing {
        val feilmelding = when (resultat) {
            is UgyldigMerkelappSoftDeleteSak -> resultat.feilmelding
            is SakFinnesIkkeSoftDeleteSak -> resultat.feilmelding
            is UkjentProdusentSoftDeleteSak -> resultat.feilmelding
            else -> null
        }

        if (feilmelding != null) {
            feilmelding.loggFeil()
            throw SoftDeleteSakException(id, feilmelding)
        } else {
            logger.error("Kunne ikke softdelete sak: $feil")
            throw SoftDeleteSakException(id, feil.ukjentFeil())
        }
    }

    fun softDeleteSakByGrupperingsid(
        grupperingsid: String,
        resultat: SoftDeleteSakByGrupperingsidResultat?,
        feil: List<GraphQLClientError>?
    ): Nothing {
        val feilmelding = when (resultat) {
            is UgyldigMerkelappSoftDeleteSakByGrupperingsid -> resultat.feilmelding
            is SakFinnesIkkeSoftDeleteSakByGrupperingsid -> resultat.feilmelding
            is UkjentProdusentSoftDeleteSakByGrupperingsid -> resultat.feilmelding
            else -> null
        }

        if (feilmelding != null) {
            feilmelding.loggFeil()
            throw SoftDeleteSakByGrupperingsidException(grupperingsid, feilmelding)
        } else {
            logger.error("Kunne ikke softdelete sak (fra grupperingsid): $feil")
            throw SoftDeleteSakByGrupperingsidException(grupperingsid, feil.ukjentFeil())
        }
    }

    private fun String.loggFeil() {
        logger.error("Feilmelding $this")
    }

    private fun List<GraphQLClientError>?.ukjentFeil(): String =
        "ukjent feil: $this"
}

class HardDeleteSakException(id: String, feilmelding: String?) :
    Exception("Sletting av sak $id mot arbeidsgiver-notifikasjon-api feilet: $feilmelding")

class NyStatusSakException(id: String, feilmelding: String?) :
    Exception("Ny status for sak $id arbeidsgiver-notifikasjon-api feilet med: $feilmelding")

class NyStatusSakByGrupperingsidException(grupperingsid: String, status: SaksStatus, feilmelding: String?) :
    Exception("Ny status $status for sak $grupperingsid arbeidsgiver-notifikasjon-api feilet med: $feilmelding")

class OppgaveUtfoertException(id: String, feilmelding: String?) :
    Exception("Utføring av oppgave $id mot arbeidsgiver-notifikasjon-api feilet: $feilmelding")

class OpprettNySakException(feilmelding: String?) :
    Exception("Opprettelse av ny sak mot arbeidsgiver-notifikasjon-api feilet: $feilmelding")

class OpprettNyOppgaveException(feilmelding: String?) :
    Exception("Opprettelse av ny oppgave mot arbeidsgiver-notifikasjon-api feilet: $feilmelding")

class SoftDeleteSakException(id: String, feilmelding: String?) :
    Exception("Sletting av sak $id mot arbeidsgiver-notifikasjon-api feilet: $feilmelding")

class SoftDeleteSakByGrupperingsidException(grupperingsid: String, feilmelding: String?) :
    Exception("Sletting av sak $grupperingsid mot arbeidsgiver-notifikasjon-api feilet: $feilmelding")
