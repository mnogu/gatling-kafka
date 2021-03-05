package com.github.mnogu.gatling.kafka.test

import io.gatling.core.Predef._
import org.apache.kafka.clients.producer.ProducerConfig
import scala.concurrent.duration._

import com.github.mnogu.gatling.kafka.Predef._

class FeederKeyValueSimulation extends Simulation {
  val kafkaConf = kafka
    .topic("test")
    .properties(
      Map(
        ProducerConfig.ACKS_CONFIG -> "1",
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG ->
          "org.apache.kafka.common.serialization.StringSerializer",
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG ->
          "org.apache.kafka.common.serialization.StringSerializer"))

  val scn = scenario("Kafka Test")
    .feed(csv("kv.csv").circular)
    // You can also set the key that will be included in the record.
    //
    // The content of the CSV file above would be like this:
    //   key,value
    //   k1,v1
    //   k2,v2
    //   k3,v3
    //   ...
    //
    // And each line corresponds to a record sent to Kafka.
    .exec(kafka("request").send[String, String]("${key}", "${value}"))

  setUp(
    scn
      .inject(constantUsersPerSec(10) during(90.seconds)))
    .protocols(kafkaConf)
}
