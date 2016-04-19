name := "gatling-kafka"

organization := "com.github.mnogu"

version := "0.1.1-SNAPSHOT"

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
  "io.gatling" % "gatling-core" % "2.1.7" % "provided",
  // Gatling 2.1.3 depends on Scala 2.11
  // and Apache Kafka 0.8.1.1 doesn't support Scala 2.11
  ("org.apache.kafka" % "kafka-clients" % "0.8.2.0")
    // Gatling contains slf4j-api
    .exclude("org.slf4j", "slf4j-api")
)

// Gatling contains scala-library
assemblyOption in assembly := (assemblyOption in assembly).value
  .copy(includeScala = false)
