name := "Krax-common"

scalaVersion := "2.11.7"

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-Ywarn-dead-code", "-Ywarn-unused", "-Ywarn-unused-import")

resolvers ++= Seq(
    "RoundEights" at "http://maven.spikemark.net/roundeights",
    "jBCrypt Repository" at "http://repo1.maven.org/maven2/org/"
)

libraryDependencies ++= Seq(
	    "io.spray"            %%  "spray-json"    % "1.3.2",
	    "com.roundeights"     %%  "hasher"        % "1.0.0",
	    "org.mindrot"          %  "jbcrypt"       % "0.3m",
	    "org.scalatest"       %%  "scalatest"     % "2.2.4" % "test"
)