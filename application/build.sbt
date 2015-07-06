lazy val root = (project in file(".")).
  aggregate(seed, backend, rest)

lazy val seed = project in file("seed")

lazy val backend = project in file("backend")

lazy val rest = project in file("rest")