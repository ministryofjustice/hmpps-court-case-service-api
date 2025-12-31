package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters

import com.fasterxml.jackson.databind.ObjectMapper
import io.r2dbc.postgresql.codec.Json
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.cases.CaseDocument

@WritingConverter
class CaseDocumentEncoder : Converter<CaseDocument, Json> {
  private val objMapper = ObjectMapper()

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: CaseDocument): Json {
    log.info("Converting Case Document to JSON")
    return Json.of(objMapper.writeValueAsString(source))
  }
}

@ReadingConverter
class CaseDocumentDecoder : Converter<Json, CaseDocument> {
  private val objectMapper = ObjectMapper()

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: Json): CaseDocument {
    log.info("Converting JSON to Case Document")
    return objectMapper.readValue(source.asString(), CaseDocument::class.java)
  }
}
