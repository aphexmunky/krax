lazy val root = (project in file(".")).
  aggregate(common, seed, backend, rest)

lazy val common = project in file("common")

lazy val seed = (project in file("seed")).dependsOn(common)

lazy val backend = (project in file("backend")).dependsOn(common)

lazy val rest = (project in file("rest")).dependsOn(common)

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-Ywarn-dead-code", "-Ywarn-unused", "-Ywarn-unused-import", "-feature")
