package com.github.mnogu.gatling.kafka.action

import com.github.mnogu.gatling.kafka.protocol.{KafkaComponents, KafkaProtocol}
import com.github.mnogu.gatling.kafka.request.builder.{Avro4sAttributes, KafkaAttributes}
import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ScenarioContext
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer.KafkaProducer

import scala.collection.JavaConverters._


class KafkaAvro4sActionBuilder[T](avro4sAttributes: Avro4sAttributes[T]) extends ActionBuilder {

  override def build( ctx: ScenarioContext, next: Action ): Action = {
    import ctx.{coreComponents, protocolComponentsRegistry, system, throttled}

    val kafkaComponents: KafkaComponents = protocolComponentsRegistry.components(KafkaProtocol.KafkaProtocolKey)

    val producer = new KafkaProducer[Nothing,GenericRecord]( kafkaComponents.kafkaProtocol.properties.asJava )

    system.registerOnTermination(producer.close())

    new KafkaAvro4sRequestAction(
      producer,
      avro4sAttributes,
      coreComponents,
      kafkaComponents.kafkaProtocol,
      throttled,
      next
    )

  }

}