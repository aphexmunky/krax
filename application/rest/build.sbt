name := "Krax-REST"

scalaVersion := "2.11.7"

enablePlugins(DockerPlugin)

resolvers ++= Seq(
	"krasserm at bintray" at "http://dl.bintray.com/krasserm/maven",
	"dnvriend at bintray" at "http://dl.bintray.com/dnvriend/maven",
  "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
)

libraryDependencies ++= {
  val akkaV = "2.4-SNAPSHOT"
  Seq(
    "org.scalaz"          %%  "scalaz-core"                             % "7.1.3",
    "io.spray"            %%  "spray-json"                              % "1.3.2",
    "com.typesafe.akka"   %%  "akka-stream-experimental"                % "1.0",
    "com.typesafe.akka"   %%  "akka-http-spray-json-experimental"       % "1.0",
    "com.typesafe.akka"   %%  "akka-http-core-experimental"             % "1.0",
    "com.typesafe.akka"   %%  "akka-http-experimental"                  % "1.0",
    "com.typesafe.akka"   %%  "akka-cluster"                            % akkaV,
    "com.typesafe.akka"   %%  "akka-cluster-sharding"                   % akkaV, 
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
