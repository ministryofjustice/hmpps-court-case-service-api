package uk.gov.justice.digital.hmpps.courtcaseserviceapi.config

import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.dialect.PostgresDialect
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.CaseDocumentDecoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.CaseDocumentEncoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.CaseMarkerDecoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.CaseMarkerEncoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.CaseURNDecoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.CaseURNsEncoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.CourtRoomDecoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.CourtRoomEncoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.HearingCaseNoteDecoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.HearingCaseNoteEncoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.HearingOutcomeDecoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.HearingOutcomeEncoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.JudicialResultDecoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.JudicialResultEncoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.PersonDecoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.PersonEncoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.PleaDecoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.PleaEncoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.VerdictDecoder
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters.VerdictEncoder

@Configuration
@EnableR2dbcRepositories
class R2dbcConfiguration(private val connectionFactory: ConnectionFactory) : AbstractR2dbcConfiguration() {

  override fun connectionFactory(): ConnectionFactory = connectionFactory

  @Bean
  override fun r2dbcCustomConversions(): R2dbcCustomConversions {
    val addConverters = ArrayList<Any>().apply { addDefaultAndCustomConverters() }
    return R2dbcCustomConversions(storeConversions, addConverters)
  }

  private fun ArrayList<Any>.addDefaultAndCustomConverters() {
    addAll(PostgresDialect.INSTANCE.converters)
    addAll(R2dbcCustomConversions.STORE_CONVERTERS)
    addAll(
      listOf(
        HearingCaseNoteEncoder(),
        HearingCaseNoteDecoder(),
        HearingOutcomeEncoder(),
        HearingOutcomeDecoder(),
        VerdictEncoder(),
        VerdictDecoder(),
        PleaEncoder(),
        PleaDecoder(),
        JudicialResultEncoder(),
        JudicialResultDecoder(),
        PersonEncoder(),
        PersonDecoder(),
        CaseMarkerEncoder(),
        CaseMarkerDecoder(),
        CourtRoomEncoder(),
        CourtRoomDecoder(),
        CaseURNsEncoder(),
        CaseURNDecoder(),
        CaseDocumentEncoder(),
        CaseDocumentDecoder(),
      ),
    )
  }
}
