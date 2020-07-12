ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/mnogu/gatling-kafka"),
    "scm:git:github.com/mnogu/gatling-kafka.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id    = "mnogu",
    name  = "Muneyuki Noguchi",
    email = "nogu.dev@gmail.com",
    url   = url("https://github.com/mnogu")
  )
)

ThisBuild / description := "Some descripiton about your project."
ThisBuild / licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/mnogu/gatling-kafka"))

// Remove all additional repository other than Maven Central from POM
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle := true
