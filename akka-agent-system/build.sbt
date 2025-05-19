name := "akka-agent-system"
version := "0.1.0"
scalaVersion := "3.3.6"

resolvers += "Akka library repository".at("https://repo.akka.io/maven")
val AkkaVersion = "2.8.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % AkkaVersion, // Added cluster dependency
  "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % Test,
  "ch.qos.logback" % "logback-classic" % "1.4.7"
)

fork := true

// Compile / mainClass := Some("com.example.Main")
Compile / mainClass := Some("com.example.demo.AgentCollaborationDemo")
