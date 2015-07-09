name := "Krax-common"

scalaVersion := "2.11.7"

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-Ywarn-dead-code", "-Ywarn-unused", "-Ywarn-unused-import")

libraryDependencies ++= Seq(
	    "io.spray"            %%  "spray-json"    % "1.3.2"
)