name := "data-service"

organization  := "com.github.zheniatrochun"

version := "0.0.1"

scalaVersion := "2.11.8"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaStreamVersion = "10.0.9"
  val akkaVersion = "2.4.19"
  val slickV = "3.2.0"

  Seq(
    "io.jsonwebtoken" % "jjwt" % "0.9.0",
    "com.typesafe.akka"           %% "akka-actor"                           % akkaVersion,
    "com.typesafe.akka"           %% "akka-http"                            % akkaStreamVersion,
    "com.typesafe.akka"           %% "akka-http-core"                       % akkaStreamVersion,
    "com.typesafe.akka"           %% "akka-http-testkit"                    % akkaStreamVersion,
    "com.typesafe.akka"           %% "akka-http-spray-json"                 % "10.0.9",
    "com.typesafe.slick"          %% "slick"                                % slickV,
    "com.typesafe.slick"          %% "slick-hikaricp"                       % slickV,
    "com.byteslounge"             %% "slick-repo"                           % "1.4.3",
    "org.postgresql"               % "postgresql"                           % "42.2.1",
    "com.h2database"               % "h2"                                   % "1.3.175",
    "net.debasishg"               %% "redisclient"                          % "3.5",
    "joda-time"                    % "joda-time"                            % "2.9.9",
    "org.joda"                     % "joda-convert"                         % "1.8.1",
    "com.jason-goodwin"           %% "authentikat-jwt"                      % "0.4.5",
    "com.typesafe.scala-logging"  %% "scala-logging"                        % "3.1.0",
    "ch.qos.logback"               % "logback-classic"                      % "1.1.3",
    "com.rabbitmq"                 % "amqp-client"                          % "3.3.4",
    "org.scalatest"               %% "scalatest"                            % "2.2.5"               % "test",
    "com.typesafe.akka"           %% "akka-testkit"                         % akkaVersion           % "test"
  )
}

enablePlugins(UniversalPlugin)
enablePlugins(DockerPlugin)

// Needed for Heroku deployment, can be removed
enablePlugins(JavaAppPackaging)

Revolver.settings
resolvers += "typesave" at "http://repo.typesafe.com/typesafe/releases"
