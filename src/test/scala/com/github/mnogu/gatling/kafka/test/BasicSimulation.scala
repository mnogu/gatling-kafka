package com.github.mnogu.gatling.kafka.test

import io.gatling.core.Predef._
import org.apache.kafka.clients.producer.ProducerConfig
import scala.concurrent.duration._

import com.github.mnogu.gatling.kafka.Predef._

class BasicSimulation extends Simulation {
  val kafkaConf = kafka
    .topic("test")
    .properties(
      Map(
        ProducerConfig.ACKS_CONFIG -> "1",
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG ->
          "org.apache.kafka.common.serialization.ByteArraySerializer",
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG ->
          "org.apache.kafka.clients.producer.ByteArraySerializer"))

  val scn = scenario("Kafka Test")
    .exec(kafka("request").send("foo"))
  // You can also use feeder
  //
  //val scn = scenario("Kafka Test")
  //  .feed(csv("test.csv").circular)
  //  .exec(kafka("request").send("${foo}"))

  setUp(
    scn
      .inject(constantUsersPerSec(10) during(90 seconds)))
    .protocols(kafkaConf)
}
