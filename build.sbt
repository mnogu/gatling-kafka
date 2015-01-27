name := "GatlingKafka"

version := "1.0"

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
  "io.gatling" % "gatling-core" % "2.1.3",
  // Gatling 2.1.3 depends on Scala 2.11
  // and Apache Kafka 0.8.1.1 doesn't support Scala 2.11
  "org.apache.kafka" % "kafka-clients" % "0.8.2-beta"
)

scalacOptions += "-feature"