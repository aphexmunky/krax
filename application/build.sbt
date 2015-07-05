lazy val root = (project in file(".")).
  aggregate(seed)

lazy val seed = project in file("seed")

