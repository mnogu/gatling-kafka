# Gatling-Kafka

An unofficial [Gatling](http://gatling.io/) stress test library
for [Apache Kafka](http://kafka.apache.org/) 0.8 protocol.

This library supports the Kafka producer API only
and doesn't support the Kafka consumer API.

## Usage

### Cloning this repository

    $ git clone https://github.com/mnogu/gatling-kafka.git
    $ cd gatling-kafka

### Creating a jar file

Install [sbt](http://www.scala-sbt.org/) 0.13 if you don't have.
And create a jar file:

    $ sbt assembly

If you want to change the version of Gatling used to create a jar file,
change the following line in [`build.sbt`](build.sbt):

```scala
"io.gatling" % "gatling-core" % "2.1.3" % "provided",
```

and run `sbt assembly`.

If you don't want to include kafka-clients library to the jar file,
change a line on kafka-clients in [`build.sbt`](build.sbt) from

```scala
("org.apache.kafka" % "kafka-clients" % "0.8.2.0")
```

to

```scala
("org.apache.kafka" % "kafka-clients" % "0.8.2.0" % "provided")
```

and run `sbt assembly`.

Note that Apache Kafka 0.8.1.1 or below doesn't contain kafka-clients library.

### Putting the jar file to lib directory

Put the jar file to `lib` directory in Gatling:

    $ cp target/scala-2.11/gatling-kafka-assembly-*.jar /path/to/gatling-charts-highcharts-bundle-2.1.*/lib

If you edited `build.sbt` in order not to include kafka-clients library
to the jar file, you also need to copy kafka-clients library to `lib` directory:

    $ cp /path/to/kafka-clients-*.jar /path/to/gatling-charts-highcharts-bundle-2.1.*/lib


###  Creating a simulation file

    $ cd /path/to/gatling-charts-highcharts-bundle-2.1.*
    $ vi user-files/simulations/KafkaSimulation.scala

Here is a sample simulation file:

```scala
import io.gatling.core.Predef._
import org.apache.kafka.clients.producer.ProducerConfig
import scala.concurrent.duration._

import com.github.mnogu.gatling.kafka.Predef._

class KafkaSimulation extends Simulation {
  val kafkaConf = kafka
    // Kafka topic name
    .topic("test")
    // Kafka producer configs
    .properties(
      Map(
        ProducerConfig.ACKS_CONFIG -> "1",
        // list of Kafka broker hostname and port pairs
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
        // Required since Apache Kafka 0.8.2.0
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG ->
          "org.apache.kafka.common.serialization.ByteArraySerializer",
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG ->
          "org.apache.kafka.common.serialization.ByteArraySerializer"))

  val scn = scenario("Kafka Test")
    .exec(
      kafka("request")
        // message to send
        .send("foo"))

  // You can also use feeder
  //
  //val scn = scenario("Kafka Test")
  //  .feed(csv("test.csv").circular)
  //  .exec(kafka("request").send("${value}"))

  setUp(
    scn
      .inject(constantUsersPerSec(10) during(90 seconds)))
    .protocols(kafkaConf)
}
```

Here is another sample simulation file:

```scala
import io.gatling.core.Predef._
import org.apache.kafka.clients.producer.ProducerConfig
import scala.concurrent.duration._

import com.github.mnogu.gatling.kafka.Predef._

class KafkaSimulation extends Simulation {
  val kafkaConf = kafka
    .topic("test")
    .properties(
      Map(
        ProducerConfig.ACKS_CONFIG -> "1",
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG ->
          "org.apache.kafka.common.serialization.ByteArraySerializer",
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG ->
          "org.apache.kafka.common.serialization.ByteArraySerializer"))

  val scn = scenario("Kafka Test")
    .feed(csv("test.csv").circular)
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
    .exec(kafka("request").send("${key}", "${value}"))

  setUp(
    scn
      .inject(constantUsersPerSec(10) during(90 seconds)))
    .protocols(kafkaConf)
}
```

### Running a stress test

After starting an Apache Kafka server, run a stress test:

    $ bin/gatling.sh

## License

Apache License, Version 2.0
