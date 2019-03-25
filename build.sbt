organization := "io.sqooba"
scalaVersion := "2.11.12"
version      := "0.4.2"
name         := "sq-conf"

homepage := Some(url("https://github.com/Sqooba/sq-conf"))
licenses := Seq("Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

crossScalaVersions := Seq("2.12.8", "2.11.12")

libraryDependencies ++= Seq(
  "com.typesafe"                %   "config"                  % "1.3.3",
  "com.typesafe.scala-logging"  %%  "scala-logging"           % "3.7.2",
  "ch.qos.logback"              %   "logback-classic"         % "1.2.3"             % Test,
  "org.scalatest"               %%  "scalatest"               % "3.0.3"             % Test,
  "org.mockito"                 %   "mockito-all"             % "1.10.19"           % Test
)

excludeDependencies ++= Seq("org.slf4j" % "slf4j-log4j12", "log4j" % "log4j")

testOptions in Test += Tests.Argument("-l", "ExternalSpec")
parallelExecution := false

lazy val External = config("ext").extend(Test)
configs(External)
inConfig(External)(Defaults.testTasks)
testOptions in External -= Tests.Argument("-l", "ExternalSpec")
testOptions in External += Tests.Argument("-n", "ExternalSpec")

val artUser = sys.env.get("ARTIFACTORY_CREDS_USR").getOrElse("")
val artPass = sys.env.get("ARTIFACTORY_CREDS_PSW").getOrElse("")

credentials += Credentials("Artifactory Realm", "artifactory-v2.sqooba.io", artUser, artPass)


publishMavenStyle := true
publishArtifact in Test := false
pomIncludeRepository := { _ => false }
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}
/*
publishTo := {
  val realm = "Artifactory Realm"
  val artBaseUrl = "https://artifactory-v2.sqooba.io/artifactory"
  if (isSnapshot.value) {
    Some(realm at s"$artBaseUrl/libs-snapshot-local;build.timestamp=" + new java.util.Date().getTime)
  } else {
    Some(realm at s"$artBaseUrl/libs-release-local")
  }
}
*/
scmInfo := Some(
  ScmInfo(
    url("https://github.com/Sqooba/sq-conf"),
    "scm:git@github.com:Sqooba/sq-conf.git"
  )
)
developers := List(
  Developer("pietrotull", "Pietari Kettunen", "kettunen@gmail.com", url("https://github.com/pietrotull"))
)