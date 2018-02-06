name := "data-service"

organization  := "com.github.zheniatrochun"

version := "0.0.1"

scalaVersion := "2.11.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaStreamVersion = "1.0-RC4"
  val akkaVersion = "2.3.11"
  val slickV = "3.2.0"

  Seq(
    "com.typesafe.akka"   %% "akka-actor"                           % akkaVersion,
    "com.typesafe.akka"   %% "akka-stream-experimental"             % akkaStreamVersion,
    "com.typesafe.akka"   %% "akka-http-experimental"               % akkaStreamVersion,
    "com.typesafe.akka"   %% "akka-http-core-experimental"          % akkaStreamVersion,
    "com.typesafe.akka"   %% "akka-http-testkit-experimental"       % akkaStreamVersion,
    "com.typesafe.akka"	  %% "akka-http-spray-json"	                % "10.0.9",
    "com.typesafe.slick"  %% "slick"                                % slickV,
    "com.typesafe.slick"  %% "slick-hikaricp"                       % slickV,
    "com.byteslounge"     %% "slick-repo"                           % "1.4.3",
    "com.h2database"       % "h2"                                   % "1.3.175",
    "org.scalatest"       %% "scalatest"                            % "2.2.5" % "test",
    "com.typesafe.akka"   %% "akka-testkit"                         % akkaVersion % "test"
  )
}

Revolver.settings
