package no.nav.helsearbeidsgiver.arbeidsgivernotifikasjon

import com.expediagroup.graphql.client.serialization.GraphQLClientKotlinxSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.harddeletesak.DefaultHardDeleteSakResultatImplementation
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.harddeletesak.HardDeleteSakResultat
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nysak.DefaultNySakResultatImplementation
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nysak.NySakResultat
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussakbygrupperingsid.DefaultNyStatusSakResultatImplementation
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.nystatussakbygrupperingsid.NyStatusSakResultat
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveendrepaaminnelsebyeksternid.DefaultOppgaveEndrePaaminnelseResultatImplementation
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveendrepaaminnelsebyeksternid.OppgaveEndrePaaminnelseResultat
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutfoertbyeksternidv2.DefaultOppgaveUtfoertResultatImplementation
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutfoertbyeksternidv2.OppgaveUtfoertResultat
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutgaattbyeksternid.DefaultOppgaveUtgaattResultatImplementation
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.oppgaveutgaattbyeksternid.OppgaveUtgaattResultat
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.DefaultNyOppgaveResultatImplementation
import no.nav.helsearbeidsgiver.arbeidsgivernotifkasjon.graphql.generated.opprettnyoppgave.NyOppgaveResultat

// Brukes for å unngå serialiseringsfeil ved ukjente responstyper. For mer info, se
// https://opensource.expediagroup.com/graphql-kotlin/docs/client/client-features/#polymorphic-types-support
internal val serializerWithFallback =
    GraphQLClientKotlinxSerializer(jsonBuilder = {
        serializersModule =
            SerializersModule {
                polymorphic(NySakResultat::class) {
                    defaultDeserializer { DefaultNySakResultatImplementation.serializer() }
                }
                polymorphic(NyStatusSakResultat::class) {
                    defaultDeserializer { DefaultNyStatusSakResultatImplementation.serializer() }
                }
                polymorphic(NyOppgaveResultat::class) {
                    defaultDeserializer { DefaultNyOppgaveResultatImplementation.serializer() }
                }
                polymorphic(OppgaveUtfoertResultat::class) {
                    defaultDeserializer { DefaultOppgaveUtfoertResultatImplementation.serializer() }
                }
                polymorphic(OppgaveUtgaattResultat::class) {
                    defaultDeserializer { DefaultOppgaveUtgaattResultatImplementation.serializer() }
                }
                polymorphic(HardDeleteSakResultat::class) {
                    defaultDeserializer { DefaultHardDeleteSakResultatImplementation.serializer() }
                }
                polymorphic(OppgaveEndrePaaminnelseResultat::class) {
                    defaultDeserializer { DefaultOppgaveEndrePaaminnelseResultatImplementation.serializer() }
                }
            }
    })
