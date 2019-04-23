package com.github.mnogu.gatling.kafka.protocol

import io.gatling.core.CoreComponents
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.protocol.{Protocol, ProtocolKey}
import org.apache.kafka.common.serialization.Serializer

object KafkaProtocol {

  def apply(configuration: GatlingConfiguration): KafkaProtocol = KafkaProtocol (
    topic = "",
    properties = Map(),
    None,
    None
  )

  val KafkaProtocolKey = new ProtocolKey[KafkaProtocol, KafkaComponents] {

    type Protocol = KafkaProtocol
    type Components = KafkaComponents

    def protocolClass: Class[io.gatling.core.protocol.Protocol] = classOf[KafkaProtocol].asInstanceOf[Class[io.gatling.core.protocol.Protocol]]

    def defaultProtocolValue(configuration: GatlingConfiguration): KafkaProtocol = KafkaProtocol(configuration)

    def newComponents(coreComponents: CoreComponents): KafkaProtocol => KafkaComponents = {

      kafkaProtocol => {
        val kafkaComponents = KafkaComponents (
          kafkaProtocol
        )

        kafkaComponents
      }
    }
  }
}

case class KafkaProtocol(
  topic: String,
  properties: Map[String, Object],
  keySerializerOpt: Option[Serializer[_]],
  valueSerializerOpt: Option[Serializer[_]]) extends Protocol {

  def topic(topic: String): KafkaProtocol = copy(topic = topic)
  def properties(properties: Map[String, Object]): KafkaProtocol = copy(properties = properties)
  def keySerializer(keySerializer: Serializer[_]): KafkaProtocol = copy(keySerializerOpt = Some(keySerializer))
  def valueSerializer(valueSerializer: Serializer[_]): KafkaProtocol = copy(valueSerializerOpt = Some(valueSerializer))
}
