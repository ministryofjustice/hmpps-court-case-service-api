package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.r2dbc.postgresql.codec.Json
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.hearing.HearingOutcome
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.hearing.HearingOutcomeWrapper

@WritingConverter
class HearingOutcomeEncoder(
  private val objectMapper: ObjectMapper,
) : Converter<List<HearingOutcome>, Json> {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: List<HearingOutcome>): Json = try {
    log.info("Converting HearingOutcome to JSON")
    val wrapper = HearingOutcomeWrapper(source)
    val json = objectMapper.writeValueAsString(wrapper)
    Json.of(json)
  } catch (ex: JsonProcessingException) {
    throw IllegalArgumentException("Error converting HearingOutcome list to JSON", ex)
  }
}

@ReadingConverter
class HearingOutcomeDecoder(
  private val objectMapper: ObjectMapper,
) : Converter<Json, List<HearingOutcome>> {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: Json): List<HearingOutcome>? {
    log.info("Converting JSON to HearingOutcome")
    try {
      val wrapper = objectMapper.readValue(
        source.asString(),
        HearingOutcomeWrapper::class.java,
      )
      return wrapper.hearingOutcomes
    } catch (ex: JsonProcessingException) {
      throw IllegalArgumentException("Error converting JSON to HearingOutcome list", ex)
    }
  }
}
