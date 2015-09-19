organization := "gatling-kafka"

name := "gatling-kafka"

version := "0.0.8-SNAPSHOT"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "io.gatling" % "gatling-core" % "2.1.7" % "provided",
  ("org.apache.kafka" % "kafka-clients" % "0.8.2.2")
    .exclude("org.slf4j", "slf4j-api")
)

// Gatling contains scala-library
assemblyOption in assembly := (assemblyOption in assembly).value
  .copy(includeScala = false)
