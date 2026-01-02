package uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.converters

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.r2dbc.postgresql.codec.Json
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.common.Address
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.business.common.AddressWrapper

class AddressEncoder(
  private val objectMapper: ObjectMapper,
) : Converter<List<Address>, Json> {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: List<Address>): Json = try {
    log.info("Converting Address to JSON")
    val wrapper = AddressWrapper(source)
    val json = objectMapper.writeValueAsString(wrapper)
    Json.of(json)
  } catch (ex: JsonProcessingException) {
    throw IllegalArgumentException("Error converting Address list to JSON", ex)
  }
}

@ReadingConverter
class AddressDecoder(
  private val objectMapper: ObjectMapper,
) : Converter<Json, List<Address>> {

  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }

  override fun convert(source: Json): List<Address>? {
    log.info("Converting JSON to Address")
    try {
      val wrapper = objectMapper.readValue(
        source.asString(),
        AddressWrapper::class.java,
      )
      return wrapper.addresses
    } catch (ex: JsonProcessingException) {
      throw IllegalArgumentException("Error converting JSON to Address list", ex)
    }
  }
}
