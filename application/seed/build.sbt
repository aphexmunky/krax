name := "Krax-Seed"

scalaVersion := "2.11.7"

resolvers ++= Seq(
	"krasserm at bintray" at "http://dl.bintray.com/krasserm/maven",
	"dnvriend at bintray" at "http://dl.bintray.com/dnvriend/maven"
)

libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-cluster" % "2.3.11",
      "com.typesafe.akka" %% "akka-contrib" % "2.3.11"
)

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-Ywarn-dead-code", "-Ywarn-unused", "-Ywarn-unused-import")