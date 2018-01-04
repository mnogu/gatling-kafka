name := "gatling-kafka"

organization := "com.github.mnogu"

version := "0.1.2-RC1"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "io.gatling" % "gatling-core" % "2.3.0" % "provided",
  ("org.apache.kafka" % "kafka-clients" % "0.11.0.1")
    // Gatling contains slf4j-api
    .exclude("org.slf4j", "slf4j-api")
)

// Gatling contains scala-library
assemblyOption in assembly := (assemblyOption in assembly).value
  .copy(includeScala = false)
