name := "gatling-kafka"

organization := "com.github.mnogu"

version := "0.1.3-SNAPSHOT"

scalaVersion := "2.12.6"

lazy val gatingVersion = "2.3.1"
lazy val kafkaVersion = "1.0.1"
lazy val avro4sVersion = "1.9.0"

libraryDependencies ++= Seq(
  "com.sksamuel.avro4s" %% "avro4s-core" % avro4sVersion,
  "io.gatling" % "gatling-core" % gatingVersion % "provided",
  ("org.apache.kafka" % "kafka-clients" % kafkaVersion)
    // Gatling contains slf4j-api
    .exclude("org.slf4j", "slf4j-api")
)

// Gatling contains scala-library
assemblyOption in assembly := (assemblyOption in assembly).value
  .copy(includeScala = false)

