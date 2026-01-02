package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.r2dbc.postgresql.codec.Json
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.offence.Verdict
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.offence.VerdictWrapper

@WritingConverter
class VerdictEncoder(
  private val objectMapper: ObjectMapper,
) : Converter<List<Verdict>, Json> {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: List<Verdict>): Json = try {
    log.info("Converting Verdict to JSON")
    val wrapper = VerdictWrapper(source)
    val json = objectMapper.writeValueAsString(wrapper)
    Json.of(json)
  } catch (ex: JsonProcessingException) {
    throw IllegalArgumentException("Error converting Verdict list to JSON", ex)
  }
}

@ReadingConverter
class VerdictDecoder(
  private val objectMapper: ObjectMapper,
) : Converter<Json, List<Verdict>> {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: Json): List<Verdict>? {
    log.info("Converting JSON to Verdict")
    try {
      val wrapper = objectMapper.readValue(
        source.asString(),
        VerdictWrapper::class.java,
      )
      return wrapper.verdicts
    } catch (ex: JsonProcessingException) {
      throw IllegalArgumentException("Error converting JSON to Verdict list", ex)
    }
  }
}
