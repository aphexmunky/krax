lazy val root = (project in file(".")).
  aggregate(seed, backend)

lazy val common = project in file("common")

lazy val seed = project in file("seed") dependsOn(common)

lazy val backend = project in file("backend") dependsOn(common)
