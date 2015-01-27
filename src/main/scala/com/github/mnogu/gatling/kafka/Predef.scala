package com.github.mnogu.gatling.kafka

import com.github.mnogu.gatling.kafka.config.KafkaProtocol
import com.github.mnogu.gatling.kafka.request.builder.KafkaRequestBuilder
import io.gatling.core.session.Expression

object Predef {
  def kafka = KafkaProtocol.DefaultKafkaProtocol

  def kafka(requestName: Expression[String]) = new KafkaRequestBuilder(requestName)
}