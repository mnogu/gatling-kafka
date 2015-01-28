package com.github.mnogu.gatling.kafka.action

import akka.actor.ActorRef
import com.github.mnogu.gatling.kafka.config.KafkaProtocol
import com.github.mnogu.gatling.kafka.request.builder.KafkaAttributes
import io.gatling.core.action.{Failable, Interruptable}
import io.gatling.core.result.message.{KO, OK}
import io.gatling.core.result.writer.DataWriterClient
import io.gatling.core.session._
import io.gatling.core.util.TimeHelper.nowMillis
import io.gatling.core.validation.Validation
import org.apache.kafka.clients.producer._

import scala.collection.JavaConverters._
import scala.util.control.NonFatal

class KafkaRequestAction(
  val kafkaAttributes: KafkaAttributes,
  val kafkaProtocol: KafkaProtocol,
  val next: ActorRef)
  extends Interruptable with Failable with DataWriterClient {

  def executeOrFail(session: Session): Validation[Unit] =
    kafkaAttributes.requestName(session).map { resolvedRequestName =>
      kafkaAttributes.payload(session).map { resolvedPayload =>
        sendRequest(resolvedRequestName, resolvedPayload, session)
      }
    }

  private def sendRequest(requestName: String, payload: String, session: Session): Unit = {
    val producer = new KafkaProducer(kafkaProtocol.properties.asJava)
    val record = new ProducerRecord(kafkaProtocol.topic, payload.getBytes)

    val requestStartDate = nowMillis
    val requestEndDate = nowMillis
    // send the request
    val future = producer.send(record)

    val responseStartDate = nowMillis
    try {
      // retrieve the response
      future.get
    } catch {
      case NonFatal(exc) =>
        val responseEndDate = nowMillis
        producer.close()
        // log the outcome
        writeRequestData(
          session,
          requestName,
          requestStartDate,
          requestEndDate,
          responseStartDate,
          responseEndDate,
          KO,
          Some(exc.getMessage))

        // calling the next action in the chain
        next ! session
        return
    }
    val responseEndDate = nowMillis
    producer.close()

    // log the outcome
    writeRequestData(
      session,
      requestName,
      requestStartDate,
      requestEndDate,
      responseStartDate,
      responseEndDate,
      OK
    )

    // calling the next action in the chain
    next ! session
  }
}
