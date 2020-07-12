name := "gatling-kafka"

organization := "com.github.mnogu"

version := "0.3.2-SNAPSHOT"

scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  "io.gatling" % "gatling-core" % "3.3.1" % "provided",
  ("org.apache.kafka" % "kafka-clients" % "2.4.0")
    // Gatling contains slf4j-api
    .exclude("org.slf4j", "slf4j-api")
)

// Gatling contains scala-library
assemblyOption in assembly := (assemblyOption in assembly).value
  .copy(includeScala = false)
