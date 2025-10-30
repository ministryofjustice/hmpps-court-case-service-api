package uk.gov.justice.digital.hmpps.courtcaseserviceapi.listener

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.listener.DomainEvent
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.model.listener.SQSMessage
import uk.gov.justice.digital.hmpps.courtcaseserviceapi.service.listener.notifiers.IEventProcessor
import java.time.LocalDateTime

const val PIC_NEW_OFFENDER_EVENT_QUEUE_CONFIG_KEY = "picnewoffendereventsqueue"

@Component
class NewOffenderDetailsEventListener(
  val context: ApplicationContext,
  val objectMapper: ObjectMapper,
) {

  private companion object {
    val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    const val MESSAGE_AGE_THRESHOLD: Long = 2L
  }

  @SqsListener(PIC_NEW_OFFENDER_EVENT_QUEUE_CONFIG_KEY, factory = "hmppsQueueContainerFactoryProxy")
  fun onDomainEvent(rawMessage: String) {
    LOG.debug("Enter onDomainEvent")
    val sqsMessage = objectMapper.readValue<SQSMessage>(rawMessage)
    LOG.debug("Received message: type:${sqsMessage.type} message:${sqsMessage.message}")

    if (sqsMessage.timeStamp?.isAfter(LocalDateTime.now().minusDays(MESSAGE_AGE_THRESHOLD)) == true) {
      when (sqsMessage.type) {
        "Notification" -> {
          val domainEvent = objectMapper.readValue<DomainEvent>(sqsMessage.message)
          try {
            getEventProcessor(domainEvent)?.process(domainEvent)
          } catch (e: Exception) {
            LOG.error(
              "Failed to process know domain event type:${domainEvent.eventType}",
              e,
            )
            throw e
          }
        }
      }
    }
  }

  fun getEventProcessor(domainEvent: DomainEvent): IEventProcessor? =
    context.getBean(domainEvent.eventType).let { it as IEventProcessor }
}