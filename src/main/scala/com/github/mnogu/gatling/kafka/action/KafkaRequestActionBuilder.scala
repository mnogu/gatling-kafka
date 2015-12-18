package com.github.mnogu.gatling.kafka.action

import akka.actor.ActorDSL.actor
import akka.actor.ActorRef
import com.github.mnogu.gatling.kafka.config.KafkaProtocol
import com.github.mnogu.gatling.kafka.request.builder.KafkaAttributes
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.config.Protocols
import org.apache.kafka.clients.producer.KafkaProducer

import scala.collection.JavaConverters._

class KafkaRequestActionBuilder[K,V](kafkaAttributes: KafkaAttributes[K,V])
  extends ActionBuilder {

  override def registerDefaultProtocols(protocols: Protocols): Protocols =
    protocols + KafkaProtocol.DefaultKafkaProtocol

  def build(next: ActorRef, protocols: Protocols): ActorRef = {
    val kafkaProtocol = protocols.getProtocol[KafkaProtocol].getOrElse(
      throw new UnsupportedOperationException("Kafka Protocol wasn't registered"))
    val producer = new KafkaProducer[K,V](
      kafkaProtocol.properties.asJava)
    actor(actorName("kafkaRequest"))(new KafkaRequestAction(
      producer, kafkaAttributes, kafkaProtocol, next))
  }
}