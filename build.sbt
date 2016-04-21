name := """akka-ecample"""

version := "1.0"

scalaVersion := "2.11.6"

val json4sVersion = "3.3.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.4",
  //"com.typesafe.akka" %% "akka-agent" % "2.4.4",
  //"com.typesafe.akka" %% "akka-camel" % "2.4.4",
  //"com.typesafe.akka" %% "akka-cluster" % "2.4.4",
  //"com.typesafe.akka" %% "akka-cluster-metrics" % "2.4.4",
  //"com.typesafe.akka" %% "akka-cluster-sharding" % "2.4.4",
  //"com.typesafe.akka" %% "akka-cluster-tools" % "2.4.4",
  //"com.typesafe.akka" %% "akka-contrib" % "2.4.4",
  //"com.typesafe.akka" %% "akka-multi-node-testkit" % "2.4.4" % Test,
  //"com.typesafe.akka" %% "akka-osgi" % "2.4.4",
  //"com.typesafe.akka" %% "akka-persistence" % "2.4.4",
  //"com.typesafe.akka" %% "akka-persistence-tck" % "2.4.4",
  //"com.typesafe.akka" %% "akka-remote" % "2.4.4",
  "com.typesafe.akka" %% "akka-slf4j" % "2.4.4",
  //"com.typesafe.akka" %% "akka-stream" % "2.4.4",
  //"com.typesafe.akka" %% "akka-stream-testkit" % "2.4.4" % Test,
  "com.typesafe.akka" %% "akka-testkit" % "2.4.4" % Test,
  //"com.typesafe.akka" %% "akka-distributed-data-experimental" % "2.4.4",
  //"com.typesafe.akka" %% "akka-typed-experimental" % "2.4.4",
  //"com.typesafe.akka" %% "akka-persistence-query-experimental" % "2.4.4",
  //"com.typesafe.akka" %% "akka-typed-experimental" % "2.4.4",

  "com.typesafe.akka" %% "akka-http-core" % "2.4.4",
  "com.typesafe.akka" %% "akka-http-testkit" % "2.4.4",
  "com.typesafe.akka" %% "akka-http-experimental" % "2.4.4",
  "com.typesafe.akka" %% "akka-http-jackson-experimental" % "2.4.4",
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.4",
  "com.typesafe.akka" %% "akka-http-xml-experimental" % "2.4.4",

  "org.json4s" %% "json4s-core" % json4sVersion,
  "org.json4s" %% "json4s-jackson" % json4sVersion,
  "de.heikoseeberger" %% "akka-http-json4s" % "1.4.1",

  // -- kafka --
  "org.apache.kafka" % "kafka-clients" % "0.8.2.2",
  "org.apache.kafka" % "kafka_2.11" % "0.8.2.2",
  "org.apache.kafka" % "kafka_2.11" % "0.8.2.2" classifier("test"),
  "org.apache.kafka" % "kafka-clients" % "0.8.2.2" % Test,

  "org.reactivemongo" %% "reactivemongo" % "0.11.11",
  "com.github.simplyscala" %% "scalatest-embedmongo" % "0.2.2" % "test",

  "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0",
  "ch.qos.logback" %  "logback-classic" % "1.1.7",

  "org.scalatest" %% "scalatest" % "2.2.6" % "test")





