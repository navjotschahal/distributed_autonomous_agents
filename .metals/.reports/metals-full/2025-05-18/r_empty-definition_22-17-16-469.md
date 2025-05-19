error id: file://<WORKSPACE>/akka-agent-system/build.sbt:
file://<WORKSPACE>/akka-agent-system/build.sbt
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -Test.
	 -Test#
	 -Test().
	 -scala/Predef.Test.
	 -scala/Predef.Test#
	 -scala/Predef.Test().
offset: 378
uri: file://<WORKSPACE>/akka-agent-system/build.sbt
text:
```scala
name := "akka-agent-system"
version := "0.1.0"
scalaVersion := "3.2.0"

resolvers += "Akka library repository".at("https://repo.akka.io/maven")
val AkkaVersion = "2.10.5"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-testkit-typed" % AkkaVersion % Te@@st,
  "ch.qos.logback" % "logback-classic" % "1.2.6"
)

fork in run := true

mainClass in Compile := Some("com.example.Main")
```


#### Short summary: 

empty definition using pc, found symbol in pc: 