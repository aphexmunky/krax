lazy val root = (project in file(".")).
  aggregate(seed, backend, rest)

lazy val common = project in file("common")

lazy val seed = (project in file("seed")).dependsOn(common)

lazy val backend = (project in file("backend")).dependsOn(common)

lazy val rest = (project in file("rest")).dependsOn(common)
