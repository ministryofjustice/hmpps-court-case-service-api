package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.r2dbc.postgresql.codec.Json
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.offence.Plea
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.offence.PleaWrapper

@WritingConverter
class PleaEncoder(
  private val objectMapper: ObjectMapper,
) : Converter<List<Plea>, Json> {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: List<Plea>): Json = try {
    log.info("Converting Plea to JSON")
    val wrapper = PleaWrapper(source)
    val json = objectMapper.writeValueAsString(wrapper)
    Json.of(json)
  } catch (ex: JsonProcessingException) {
    throw IllegalArgumentException("Error converting Plea list to JSON", ex)
  }
}

@ReadingConverter
class PleaDecoder(
  private val objectMapper: ObjectMapper,
) : Converter<Json, List<Plea>> {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: Json): List<Plea>? {
    log.info("Converting JSON to Plea")
    try {
      val wrapper = objectMapper.readValue(
        source.asString(),
        PleaWrapper::class.java,
      )
      return wrapper.pleas
    } catch (ex: JsonProcessingException) {
      throw IllegalArgumentException("Error converting JSON to Plea list", ex)
    }
  }
}
