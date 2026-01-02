package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.r2dbc.postgresql.codec.Json
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.hearing.HearingCaseNote
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.hearing.HearingCaseNoteWrapper

@WritingConverter
class HearingCaseNoteEncoder(
  private val objectMapper: ObjectMapper,
) : Converter<List<HearingCaseNote>, Json> {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: List<HearingCaseNote>): Json = try {
    log.info("Converting Hearing Case Note to JSON")
    val wrapper = HearingCaseNoteWrapper(source)
    val json = objectMapper.writeValueAsString(wrapper)
    Json.of(json)
  } catch (ex: JsonProcessingException) {
    throw IllegalArgumentException("Error converting HearingCaseNote list to JSON", ex)
  }
}

@ReadingConverter
class HearingCaseNoteDecoder(
  private val objectMapper: ObjectMapper,
) : Converter<Json, List<HearingCaseNote>> {
  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: Json): List<HearingCaseNote>? {
    log.info("Converting JSON to Hearing Case Note")
    try {
      val wrapper = objectMapper.readValue(
        source.asString(),
        HearingCaseNoteWrapper::class.java,
      )
      return wrapper.caseNotes
    } catch (ex: JsonProcessingException) {
      throw IllegalArgumentException("Error converting JSON to HearingCaseNote list", ex)
    }
  }
}
