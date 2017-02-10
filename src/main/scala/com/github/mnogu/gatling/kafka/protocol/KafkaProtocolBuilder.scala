package com.github.mnogu.gatling.kafka.protocol

import io.gatling.core.config.GatlingConfiguration


object KafkaProtocolBuilder {

  implicit def toKafkaProtocol(builder: KafkaProtocolBuilder): KafkaProtocol = builder.build

  def apply(configuration: GatlingConfiguration) : KafkaProtocolBuilder =
    KafkaProtocolBuilder(KafkaProtocol(configuration))

}


case class KafkaProtocolBuilder(kafkaProtocol: KafkaProtocol) {

  def build = kafkaProtocol

}