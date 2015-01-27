package com.github.mnogu.gatling.kafka.action

import akka.actor.ActorDSL.actor
import akka.actor.ActorRef
import com.github.mnogu.gatling.kafka.config.KafkaProtocol
import com.github.mnogu.gatling.kafka.request.builder.KafkaAttributes
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.config.Protocols

class KafkaRequestActionBuilder(kafkaAttributes: KafkaAttributes)
  extends ActionBuilder {

  override def registerDefaultProtocols(protocols: Protocols): Protocols =
    protocols + KafkaProtocol.DefaultKafkaProtocol

  def build(next: ActorRef, protocols: Protocols): ActorRef = {
    val kafkaProtocol = protocols.getProtocol[KafkaProtocol].getOrElse(
      throw new UnsupportedOperationException("Kafka Protocol wasn't registered"))
    actor(actorName("kafkaRequest"))(new KafkaRequestAction(kafkaAttributes, kafkaProtocol, next))
  }
}