name := "gatling-kafka"

organization := "com.github.mnogu"

version := "0.1.2-SNAPSHOT"

scalaVersion := "2.11.8"

enablePlugins(GatlingPlugin)

val gatlingVersion = "2.2.3"

libraryDependencies ++= Seq(
  "io.gatling" % "gatling-core" % "2.2.3" % "provided",
  "io.gatling"            % "gatling-test-framework"    % gatlingVersion % "test",
  "io.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVersion % "test",

  ("org.apache.kafka" % "kafka-clients" % "0.10.1.1")
    // Gatling contains slf4j-api
    .exclude("org.slf4j", "slf4j-api")
)

// Gatling contains scala-library
assemblyOption in assembly := (assemblyOption in assembly).value
  .copy(includeScala = false)
