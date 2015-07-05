name := "Krax"

scalaVersion := "2.11.7"

resolvers ++= Seq(
	"krasserm at bintray" at "http://dl.bintray.com/krasserm/maven",
	"dnvriend at bintray" at "http://dl.bintray.com/dnvriend/maven"
)

libraryDependencies ++= Seq(
	"com.github.dnvriend" %% "akka-persistence-inmemory" % "1.0.3"
)

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-Ywarn-dead-code", "-Ywarn-unused", "-Ywarn-unused-import")