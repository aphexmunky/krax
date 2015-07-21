name := "Krax-Backend"

scalaVersion := "2.11.7"

resolvers ++= Seq(
	"krasserm at bintray" at "http://dl.bintray.com/krasserm/maven",
	"dnvriend at bintray" at "http://dl.bintray.com/dnvriend/maven"
)

libraryDependencies ++= Seq(
      "org.scalaz"          %%  "scalaz-core"                 % "7.1.3",
	    "com.typesafe.akka"   %%  "akka-cluster"                % "2.3.11",
      "com.typesafe.akka"   %%  "akka-contrib"                % "2.3.11",
      "com.roundeights"     %%  "hasher"                      % "1.0.0",
      "com.typesafe.akka"   %%  "akka-testkit"                % "2.3.11" % "test",
      "org.scalatest"       %%  "scalatest"                   % "2.2.4" % "test",
      "com.github.dnvriend" %%  "akka-persistence-inmemory"   % "1.0.3" % "test",
      "com.github.krasserm" %%  "akka-persistence-cassandra"  % "0.3.9"
)

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-Ywarn-dead-code", "-Ywarn-unused", "-Ywarn-unused-import")