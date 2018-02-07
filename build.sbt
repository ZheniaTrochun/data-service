name := "data-service"

organization  := "com.github.zheniatrochun"

version := "0.0.1"

scalaVersion := "2.11.8"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaStreamVersion = "10.0.9"
  val akkaVersion = "2.4.17"
  val slickV = "3.2.0"

  Seq(
    "com.typesafe.akka"   %% "akka-actor"                           % akkaVersion,
    "com.typesafe.akka"   %% "akka-http"               % akkaStreamVersion,
    "com.typesafe.akka"   %% "akka-http-core"          % akkaStreamVersion,
    "com.typesafe.akka"   %% "akka-http-testkit"       % akkaStreamVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.9",
    "com.typesafe.slick"  %% "slick"                                % slickV,
    "com.typesafe.slick"  %% "slick-hikaricp"                       % slickV,
    "com.byteslounge"     %% "slick-repo"                           % "1.4.3",
    "com.h2database"       % "h2"                                   % "1.3.175",
    "joda-time"            % "joda-time"                            % "2.9.9",
    "org.joda"             % "joda-convert"                         % "1.8.1",
    "org.scalatest"       %% "scalatest"                            % "2.2.5" % "test",
    "com.typesafe.akka"   %% "akka-testkit"                         % akkaVersion % "test"
  )
}

Revolver.settings
