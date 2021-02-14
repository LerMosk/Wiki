lazy val buildSettings = Seq(
  name := "WikiDump",
  version := "0.1",
  scalaVersion := "2.12.10"
)

val akkaVersion = "2.6.0"
val json4sVersion = "3.6.5"
val log4jVersion = "2.11.2"
val akkHttpVersion = "10.1.10"
val mongoDriverVersion = "2.7.0"

javacOptions ++= Seq("-encoding", "UTF-8", "-source", "1.8", "-target", "1.8")
scalacOptions += "-target:jvm-1.8"

lazy val main = (project in file("."))
  .settings(
    buildSettings,
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % akkHttpVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "net.logstash.logback" % "logstash-logback-encoder" % "5.3",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.apache.logging.log4j" % "log4j-core" % log4jVersion,
      "org.apache.logging.log4j" % "log4j-api" % log4jVersion,
      "org.json4s" %% "json4s-jackson" % json4sVersion,
      "org.json4s" %% "json4s-ext" % json4sVersion,
      "org.mongodb.scala" %% "mongo-scala-driver" % mongoDriverVersion,
      "org.mongodb.scala" %% "mongo-scala-bson" % mongoDriverVersion
    ),
    mainClass in assembly := Some("wiki.ApiBoot")
  )