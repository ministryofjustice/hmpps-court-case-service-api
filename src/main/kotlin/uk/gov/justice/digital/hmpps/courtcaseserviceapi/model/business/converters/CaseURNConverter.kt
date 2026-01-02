package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.r2dbc.postgresql.codec.Json
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.cases.CaseURN
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.cases.CaseURNWrapper

@WritingConverter
class CaseURNsEncoder(
  private val objectMapper: ObjectMapper,
) : Converter<List<CaseURN>, Json> {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: List<CaseURN>): Json = try {
    log.info("Converting Case URN's to JSON")
    val wrapper = CaseURNWrapper(source)
    val json = objectMapper.writeValueAsString(wrapper)
    Json.of(json)
  } catch (ex: JsonProcessingException) {
    throw IllegalArgumentException("Error converting Case URN's list to JSON", ex)
  }
}

@ReadingConverter
class CaseURNDecoder(
  private val objectMapper: ObjectMapper,
) : Converter<Json, List<CaseURN>> {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: Json): List<CaseURN>? {
    log.info("Converting JSON to Case URN's")
    try {
      val wrapper = objectMapper.readValue(
        source.asString(),
        CaseURNWrapper::class.java,
      )
      return wrapper.caseURNs
    } catch (ex: JsonProcessingException) {
      throw IllegalArgumentException("Error converting JSON to Case URN's list", ex)
    }
  }
}
