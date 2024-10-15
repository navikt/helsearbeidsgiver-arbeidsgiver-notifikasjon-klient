package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

import com.expediagroup.graphql.client.types.GraphQLClientError
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.enums.SaksStatus
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.harddeletesak.HardDeleteSakResultat
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussak.NyStatusSakResultat
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutfoert.OppgaveUtfoertResultat
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutgaatt.NotifikasjonFinnesIkke
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutgaatt.OppgaveUtgaattResultat
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutgaatt.OppgavenErAlleredeUtfoert
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutgaatt.UgyldigMerkelapp
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutgaatt.UkjentProdusent
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.NyOppgaveResultat
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnysak.NySakResultat
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.softdeletesak.SoftDeleteSakResultat
import no.nav.helsearbeidsgiver.utils.log.logger
import no.nav.helsearbeidsgiver.utils.log.sikkerLogger
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.harddeletesak.SakFinnesIkke as FinnesIkkeHardDeleteSak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.harddeletesak.UgyldigMerkelapp as UgyldigMerkelappHardDeleteSak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.harddeletesak.UkjentProdusent as UkjentProdusentHardDeleteSak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussak.Konflikt as KonfliktNyStatusSak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussak.SakFinnesIkke as FinnesIkkeNyStatusSak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussak.UgyldigMerkelapp as UgyldigMerkelappNyStatusSak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussak.UkjentProdusent as UkjentProdusentNyStatusSak
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussakbygrupperingsid.Konflikt as KonfliktNyStatusSakByGrupperingsid
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussakbygrupperingsid.NyStatusSakResultat as NyStatusSakByGrupperingsidResultat
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussakbygrupperingsid.SakFinnesIkke as SakFinnesIkkeNyStatusSakByGrupperingsid
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussakbygrupperingsid.UgyldigMerkelapp as UgyldigMerkelappByGrupperingsid
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussakbygrupperingsid.UkjentProdusent as UkjentProdusentByGrupperingsid
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutfoert.NotifikasjonFinnesIkke as NotifikasjonFinnesIkkeOppgaveUtfoert
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutfoert.UgyldigMerkelapp as UgyldigMerkelappOppgaveUtfoert
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutfoert.UkjentProdusent as UkjentProdusentOppgaveUtfoert
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutfoertbyeksternidv2.NotifikasjonFinnesIkke as NotifikasjonFinnesIkkeOppgaveUtfoertByEksternIdV2
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutfoertbyeksternidv2.OppgaveUtfoertResultat as OppgaveUtfoertByEksternIdV2Resultat
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutfoertbyeksternidv2.UgyldigMerkelapp as UgyldigMerkelappOppgaveUtfoertByEksternIdV2
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutfoertbyeksternidv2.UkjentProdusent as UkjentProdusentOppgaveUtfoertByEksternIdV2
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutgaattbyeksternid.NotifikasjonFinnesIkke as NotifikasjonFinnesIkkeByEksternId
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutgaattbyeksternid.OppgaveUtgaattResultat as OppgaveUtgaattByEksternIdResultat
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutgaattbyeksternid.OppgavenErAlleredeUtfoert as OppgavenErAlleredeUtfoertByEksternId
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutgaattbyeksternid.UgyldigMerkelapp as UgyldigMerkelappByEksternId
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutgaattbyeksternid.UkjentProdusent as UkjentProdusentByEksternId
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.DuplikatEksternIdOgMerkelapp as DuplikatEksternIdOgMerkelappNyOppgave
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.UgyldigMerkelapp as UgyldigMerkelappNyOppgave
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.UgyldigMottaker as UgyldigMottakerNyOppgave
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.UgyldigPaaminnelseTidspunkt as UgyldigPaaminnelseTidspunktNyOppgave
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
    private val logger = ArbeidsgiverNotifikasjonKlient::class.logger()
    private val sikkerLogger = sikkerLogger()

    fun opprettNySak(
        resultat: NySakResultat?,
        feil: List<GraphQLClientError>?,
    ): Nothing {
        val feilmelding =
            when (resultat) {
                is DuplikatGrupperingsidNySak -> resultat.feilmelding
                is UgyldigMerkelappNySak -> resultat.feilmelding
                is UgyldigMottakerNySak -> resultat.feilmelding
                is UkjentProdusentNySak -> resultat.feilmelding
                is UkjentRolleNySak -> resultat.feilmelding
                else -> null
            }

        if (feilmelding != null) {
            feilmelding.loggFeilmelding()
            throw OpprettNySakException(feilmelding)
        } else {
            loggError("Klarte ikke opprette ny sak: $feil")
            throw OpprettNySakException(feil.ukjentFeil())
        }
    }

    fun nyStatusSak(
        id: String,
        nyStatus: SaksStatus,
        resultat: NyStatusSakResultat?,
        feil: List<GraphQLClientError>?,
    ): Nothing {
        if (resultat is FinnesIkkeNyStatusSak) {
            throw SakEllerOppgaveFinnesIkkeException(resultat.feilmelding)
        } else {
            val feilmelding =
                when (resultat) {
                    is KonfliktNyStatusSak -> resultat.feilmelding
                    is UgyldigMerkelappNyStatusSak -> resultat.feilmelding
                    is UkjentProdusentNyStatusSak -> resultat.feilmelding
                    else -> null
                }

            if (feilmelding != null) {
                feilmelding.loggFeilmelding()
                throw NyStatusSakException(id, nyStatus, feilmelding)
            } else {
                loggError("Klarte ikke endre status på sak: $feil")
                throw NyStatusSakException(id, nyStatus, feil.ukjentFeil())
            }
        }
    }

    fun nyStatusSakByGrupperingsid(
        grupperingsid: String,
        merkelapp: String,
        nyStatus: SaksStatus,
        resultat: NyStatusSakByGrupperingsidResultat?,
        feil: List<GraphQLClientError>?,
    ): Nothing {
        if (resultat is SakFinnesIkkeNyStatusSakByGrupperingsid) {
            throw SakEllerOppgaveFinnesIkkeException(resultat.feilmelding)
        } else {
            val feilmelding =
                when (resultat) {
                    is KonfliktNyStatusSakByGrupperingsid -> resultat.feilmelding
                    is UgyldigMerkelappByGrupperingsid -> resultat.feilmelding
                    is UkjentProdusentByGrupperingsid -> resultat.feilmelding
                    else -> null
                }

            if (feilmelding != null) {
                feilmelding.loggFeilmelding()
                throw NyStatusSakByGrupperingsidException(grupperingsid, merkelapp, nyStatus, feilmelding)
            } else {
                loggError("Klarte ikke endre status på sak (fra grupperingsid): $feil")
                throw NyStatusSakByGrupperingsidException(grupperingsid, merkelapp, nyStatus, feil.ukjentFeil())
            }
        }
    }

    fun nyOppgave(
        resultat: NyOppgaveResultat?,
        feil: List<GraphQLClientError>?,
    ): Nothing {
        val feilmelding =
            when (resultat) {
                is DuplikatEksternIdOgMerkelappNyOppgave -> resultat.feilmelding
                is UgyldigMerkelappNyOppgave -> resultat.feilmelding
                is UgyldigMottakerNyOppgave -> resultat.feilmelding
                is UgyldigPaaminnelseTidspunktNyOppgave -> resultat.feilmelding
                is UkjentProdusentNyOppgave -> resultat.feilmelding
                is UkjentRolleNyOppgave -> resultat.feilmelding
                else -> null
            }

        if (feilmelding != null) {
            feilmelding.loggFeilmelding()
            throw OpprettNyOppgaveException(feilmelding)
        } else {
            loggError("Klarte ikke opprette ny oppgave: $feil")
            throw OpprettNyOppgaveException(feil.ukjentFeil())
        }
    }

    fun oppgaveUtfoert(
        id: String,
        resultat: OppgaveUtfoertResultat?,
        feil: List<GraphQLClientError>?,
    ): Nothing {
        if (resultat is NotifikasjonFinnesIkkeOppgaveUtfoert) {
            throw SakEllerOppgaveFinnesIkkeException(resultat.feilmelding)
        } else {
            val feilmelding =
                when (resultat) {
                    is UgyldigMerkelappOppgaveUtfoert -> resultat.feilmelding
                    is UkjentProdusentOppgaveUtfoert -> resultat.feilmelding
                    else -> null
                }

            if (feilmelding != null) {
                feilmelding.loggFeilmelding()
                throw OppgaveUtfoertException(id, feilmelding)
            } else {
                loggError("Klarte ikke sette oppgave som utført: $feil")
                throw OppgaveUtfoertException(id, feil.ukjentFeil())
            }
        }
    }

    fun oppgaveUtfoertByEksternIdV2(
        eksternId: String,
        merkelapp: String,
        resultat: OppgaveUtfoertByEksternIdV2Resultat?,
        feil: List<GraphQLClientError>?,
    ): Nothing {
        if (resultat is NotifikasjonFinnesIkkeOppgaveUtfoertByEksternIdV2) {
            throw SakEllerOppgaveFinnesIkkeException(resultat.feilmelding)
        } else {
            val feilmelding =
                when (resultat) {
                    is UgyldigMerkelappOppgaveUtfoertByEksternIdV2 -> resultat.feilmelding
                    is UkjentProdusentOppgaveUtfoertByEksternIdV2 -> resultat.feilmelding
                    else -> null
                }

            if (feilmelding != null) {
                feilmelding.loggFeilmelding()
                throw OppgaveUtfoertByEksternIdV2Exception(eksternId, merkelapp, feilmelding)
            } else {
                loggError("Klarte ikke sette oppgave som utført: $feil")
                throw OppgaveUtfoertByEksternIdV2Exception(eksternId, merkelapp, feil.ukjentFeil())
            }
        }
    }

    fun oppgaveUtgaatt(
        id: String,
        resultat: OppgaveUtgaattResultat?,
        feil: List<GraphQLClientError>?,
    ): Nothing {
        if (resultat is NotifikasjonFinnesIkke) {
            throw SakEllerOppgaveFinnesIkkeException(resultat.feilmelding)
        } else {
            val feilmelding =
                when (resultat) {
                    is UgyldigMerkelapp -> resultat.feilmelding
                    is UkjentProdusent -> resultat.feilmelding
                    is OppgavenErAlleredeUtfoert -> resultat.feilmelding
                    else -> null
                }

            if (feilmelding != null) {
                feilmelding.loggFeilmelding()
                throw OppgaveUtgaattException(id, feilmelding)
            } else {
                loggError("Klarte ikke sette oppgave som utgått: $feil")
                throw OppgaveUtgaattException(id, feil.ukjentFeil())
            }
        }
    }

    fun oppgaveUtgaattByEksternId(
        eksternId: String,
        resultat: OppgaveUtgaattByEksternIdResultat?,
        feil: List<GraphQLClientError>?,
    ): Nothing {
        if (resultat is NotifikasjonFinnesIkkeByEksternId) {
            throw SakEllerOppgaveFinnesIkkeException(resultat.feilmelding)
        } else {
            val feilmelding =
                when (resultat) {
                    is UgyldigMerkelappByEksternId -> resultat.feilmelding
                    is UkjentProdusentByEksternId -> resultat.feilmelding
                    is OppgavenErAlleredeUtfoertByEksternId -> resultat.feilmelding
                    else -> null
                }

            if (feilmelding != null) {
                feilmelding.loggFeilmelding()
                throw OppgaveUtgaattByEksternIdException(eksternId, feilmelding)
            } else {
                loggError("Klarte ikke sette oppgave som utgått: $feil")
                throw OppgaveUtgaattByEksternIdException(eksternId, feil.ukjentFeil())
            }
        }
    }

    fun softDeleteSak(
        id: String,
        resultat: SoftDeleteSakResultat?,
        feil: List<GraphQLClientError>?,
    ): Nothing {
        val feilmelding =
            when (resultat) {
                is SakFinnesIkkeSoftDeleteSak -> resultat.feilmelding
                is UgyldigMerkelappSoftDeleteSak -> resultat.feilmelding
                is UkjentProdusentSoftDeleteSak -> resultat.feilmelding
                else -> null
            }

        if (feilmelding != null) {
            feilmelding.loggFeilmelding()
            throw SoftDeleteSakException(id, feilmelding)
        } else {
            loggError("Klarte ikke softdelete sak: $feil")
            throw SoftDeleteSakException(id, feil.ukjentFeil())
        }
    }

    fun softDeleteSakByGrupperingsid(
        grupperingsid: String,
        resultat: SoftDeleteSakByGrupperingsidResultat?,
        feil: List<GraphQLClientError>?,
    ): Nothing {
        val feilmelding =
            when (resultat) {
                is SakFinnesIkkeSoftDeleteSakByGrupperingsid -> resultat.feilmelding
                is UgyldigMerkelappSoftDeleteSakByGrupperingsid -> resultat.feilmelding
                is UkjentProdusentSoftDeleteSakByGrupperingsid -> resultat.feilmelding
                else -> null
            }

        if (feilmelding != null) {
            feilmelding.loggFeilmelding()
            throw SoftDeleteSakByGrupperingsidException(grupperingsid, feilmelding)
        } else {
            loggError("Klarte ikke softdelete sak (fra grupperingsid): $feil")
            throw SoftDeleteSakByGrupperingsidException(grupperingsid, feil.ukjentFeil())
        }
    }

    fun hardDeleteSak(
        id: String,
        resultat: HardDeleteSakResultat?,
        feil: List<GraphQLClientError>?,
    ): Nothing {
        val feilmelding =
            when (resultat) {
                is FinnesIkkeHardDeleteSak -> resultat.feilmelding
                is UgyldigMerkelappHardDeleteSak -> resultat.feilmelding
                is UkjentProdusentHardDeleteSak -> resultat.feilmelding
                else -> null
            }

        if (feilmelding != null) {
            feilmelding.loggFeilmelding()
            throw HardDeleteSakException(id, feilmelding)
        } else {
            loggError("Klarte ikke harddelete sak $resultat: med feil $feil")
            throw HardDeleteSakException(id, feil.ukjentFeil())
        }
    }

    private fun String.loggFeilmelding() {
        "Feilmelding: $this".also {
            logger.error(it)
            sikkerLogger.error(it)
        }
    }

    private fun loggWarning(feilmelding: String) {
        logger.warn(feilmelding)
        sikkerLogger.warn(feilmelding)
    }

    private fun loggError(feilmelding: String) {
        logger.error(feilmelding)
        sikkerLogger.error(feilmelding)
    }

    private fun List<GraphQLClientError>?.ukjentFeil(): String = "Ukjent feil: $this"
}

class SakEllerOppgaveFinnesIkkeException(
    feilmelding: String,
) : Exception("Sak/oppgave finnes ikke. Trolig slettet pga. levetid. Feilmelding: '$feilmelding'.")

class OpprettNySakException(
    feilmelding: String?,
) : Exception("Opprettelse av ny sak mot arbeidsgiver-notifikasjon-api feilet: $feilmelding")

class NyStatusSakException(
    id: String,
    nyStatus: SaksStatus,
    feilmelding: String?,
) : Exception("Ny status '$nyStatus' for sak med id '$id' mot arbeidsgiver-notifikasjon-api feilet med: $feilmelding")

class NyStatusSakByGrupperingsidException(
    grupperingsid: String,
    merkelapp: String,
    nyStatus: SaksStatus,
    feilmelding: String?,
) : Exception(
        "Ny status '$nyStatus' for sak med grupperingsid '$grupperingsid' og merkelapp '$merkelapp' mot " +
            "arbeidsgiver-notifikasjon-api feilet med: $feilmelding",
    )

class OpprettNyOppgaveException(
    feilmelding: String?,
) : Exception("Opprettelse av ny oppgave mot arbeidsgiver-notifikasjon-api feilet: $feilmelding")

class OppgaveUtfoertException(
    id: String,
    feilmelding: String?,
) : Exception("Utføring av oppgave med id '$id' mot arbeidsgiver-notifikasjon-api feilet: $feilmelding")

class OppgaveUtfoertByEksternIdV2Exception(
    eksternId: String,
    merkelapp: String,
    feilmelding: String?,
) : Exception(
        "Utføring av oppgave med ekstern ID '$eksternId' og merkelapp '$merkelapp' mot " +
            "arbeidsgiver-notifikasjon-api feilet: $feilmelding",
    )

class OppgaveUtgaattException(
    id: String,
    feilmelding: String?,
) : Exception("Oppdatering av oppgave med id '$id' til utgått mot arbeidsgiver-notifikasjon-api feilet: $feilmelding")

class OppgaveUtgaattByEksternIdException(
    eksternId: String,
    feilmelding: String?,
) : Exception(
        "Oppdatering av oppgave med eksternId '$eksternId' til " +
            "utgått mot arbeidsgiver-notifikasjon-api feilet: $feilmelding",
    )

class SoftDeleteSakException(
    id: String,
    feilmelding: String?,
) : Exception("Sletting (soft) av sak med id '$id' mot arbeidsgiver-notifikasjon-api feilet: $feilmelding")

class SoftDeleteSakByGrupperingsidException(
    grupperingsid: String,
    feilmelding: String?,
) : Exception(
        "Sletting (soft) av sak med grupperingsid '$grupperingsid' mot arbeidsgiver-notifikasjon-api feilet: $feilmelding",
    )

class HardDeleteSakException(
    id: String,
    feilmelding: String?,
) : Exception("Sletting (hard) av sak med id '$id' mot arbeidsgiver-notifikasjon-api feilet: $feilmelding")
