package com.github.mnogu.gatling.kafka.action

import akka.actor.ActorDSL.actor
import akka.actor.ActorRef
import com.github.mnogu.gatling.kafka.config.KafkaProtocol
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.config.Protocols
import io.gatling.core.session.Expression

class KafkaRequestActionBuilder(requestName: Expression[String])
  extends ActionBuilder {

  override def registerDefaultProtocols(protocols: Protocols): Protocols =
    protocols + KafkaProtocol.DefaultKafkaProtocol

  def build(next: ActorRef, protocols: Protocols): ActorRef = {
    val kafkaProtocol = protocols.getProtocol[KafkaProtocol].getOrElse(
      throw new UnsupportedOperationException("Kafka Protocol wasn't registered"))
    actor(actorName("kafkaRequest"))(new KafkaRequestAction(requestName, kafkaProtocol, next))
  }
}