package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.r2dbc.postgresql.codec.Json
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.cases.CaseDocument
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.cases.CaseDocumentWrapper

@WritingConverter
class CaseDocumentEncoder(
  private val objectMapper: ObjectMapper,
) : Converter<List<CaseDocument>, Json> {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: List<CaseDocument>): Json = try {
    log.info("Converting Case Document to JSON")
    val wrapper = CaseDocumentWrapper(source)
    val json = objectMapper.writeValueAsString(wrapper)
    Json.of(json)
  } catch (ex: JsonProcessingException) {
    throw IllegalArgumentException("Error converting Case Document list to JSON", ex)
  }
}

@ReadingConverter
class CaseDocumentDecoder(
  private val objectMapper: ObjectMapper,
) : Converter<Json, List<CaseDocument>> {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: Json): List<CaseDocument>? {
    log.info("Converting JSON to Case Document")
    try {
      val wrapper = objectMapper.readValue(
        source.asString(),
        CaseDocumentWrapper::class.java,
      )
      return wrapper.caseDocuments
    } catch (ex: JsonProcessingException) {
      throw IllegalArgumentException("Error converting JSON to Case Document list", ex)
    }
  }
}
