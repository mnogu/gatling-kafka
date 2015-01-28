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

object KafkaRequestAction extends DataWriterClient {
  def reportUnbuildableRequest(
      requestName: String,
      session: Session,
      errorMessage: String): Unit = {
    val now = nowMillis
    writeRequestData(
      session, requestName, now, now, now, now, KO, Some(errorMessage))
  }
}

class KafkaRequestAction(
  val kafkaAttributes: KafkaAttributes,
  val kafkaProtocol: KafkaProtocol,
  val next: ActorRef)
  extends Interruptable with Failable with DataWriterClient {

  def executeOrFail(session: Session): Validation[Unit] =
    kafkaAttributes.requestName(session).flatMap { resolvedRequestName =>

      val outcome = sendRequest(
        resolvedRequestName, kafkaAttributes.payload, session)
      outcome.onFailure(
        errorMessage => KafkaRequestAction.reportUnbuildableRequest(
          resolvedRequestName, session, errorMessage))
      outcome
    }

  private def sendRequest(
      requestName: String,
      payload: Expression[String],
      session: Session): Validation[Unit] = {

    payload(session).map { resolvedPayload =>
      val producer = new KafkaProducer(kafkaProtocol.properties.asJava)
      val record = new ProducerRecord(
        kafkaProtocol.topic, resolvedPayload.getBytes)

      val requestStartDate = nowMillis
      val requestEndDate = nowMillis
      // send the request
      val future = producer.send(record)

      val responseStartDate = nowMillis
      future.get
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
}
