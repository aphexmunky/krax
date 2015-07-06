lazy val root = (project in file(".")).
  aggregate(seed, backend)

lazy val seed = project in file("seed")

lazy val backend = project in file("backend")
