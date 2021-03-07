package com.github.mnogu.gatling.kafka.test

import java.util

import com.github.mnogu.gatling.kafka.Predef._
import io.gatling.core.Predef._
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.header.Header
import org.apache.kafka.common.header.internals.RecordHeader

import scala.concurrent.duration._

class KeyValueHeadersSimulation extends Simulation {
  val kafkaConf = kafka
    // Kafka topic name
    .topic("test")
    // Kafka producer configs
    .properties(
      Map(
        ProducerConfig.ACKS_CONFIG -> "1",
        // list of Kafka broker hostname and port pairs
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",

        // in most cases, StringSerializer or ByteArraySerializer
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG ->
          "org.apache.kafka.common.serialization.ByteArraySerializer",
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG ->
          "org.apache.kafka.common.serialization.ByteArraySerializer"))

  //Headers
  val header: Header = new RecordHeader("key", "valueAsBytes".getBytes())
  val headers: util.List[Header] = new util.ArrayList
  headers.add(header)

  // Push kafka with key, value and headers
  val scn = scenario("Kafka Test")
    .exec(
      kafka("request")
        // message to send
        .send("key".getBytes(),"value".getBytes(),headers))

  setUp(
    scn
      .inject(constantUsersPerSec(10) during(90 seconds)))
    .protocols(kafkaConf)
}
