name := "Krax-REST"

scalaVersion := "2.11.7"

enablePlugins(DockerPlugin)

resolvers ++= Seq(
	"krasserm at bintray" at "http://dl.bintray.com/krasserm/maven",
	"dnvriend at bintray" at "http://dl.bintray.com/dnvriend/maven"
)

libraryDependencies ++= {
  val akkaV     = "2.3.12"
  val akkaHttpV = "1.0"
  Seq(
    "org.scalaz"          %%  "scalaz-core"                             % "7.1.3",
    "io.spray"            %%  "spray-json"                              % "1.3.2",
    "com.typesafe.akka"   %%  "akka-stream-experimental"                % akkaHttpV,
    "com.typesafe.akka"   %%  "akka-http-spray-json-experimental"       % akkaHttpV,
    "com.typesafe.akka"   %%  "akka-http-core-experimental"             % akkaHttpV,
    "com.typesafe.akka"   %%  "akka-http-experimental"                  % akkaHttpV,
    "com.typesafe.akka"   %%  "akka-cluster"                            % akkaV,
    "com.typesafe.akka"   %%  "akka-contrib"                            % akkaV,
    "com.typesafe.akka"   %%  "akka-actor"                              % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"                            % akkaV   % "test"
  )  
}

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-Ywarn-dead-code", "-Ywarn-unused", "-Ywarn-unused-import")

docker <<= (docker dependsOn assembly)

dockerfile in docker := {
  val artifact = (outputPath in assembly).value
  val artifactTargetPath = s"/app/${artifact.name}"
  new Dockerfile {
    from("java")
    add(artifact, artifactTargetPath)
    entryPoint("java", "-jar", artifactTargetPath)
  }
}
