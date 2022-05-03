import com.typesafe.sbt.SbtNativePackager.autoImport.packageName

enablePlugins(JavaAppPackaging)

name := "bank-crud"

version := "0.1"

scalaVersion := "2.12.9"

dockerBaseImage := "openjdk:8-jre-alpine"
packageName in Docker := "bank_crud"

val akkaVersion       = "2.6.8"
val akkaHttpVersion   = "10.2.0"
val circeVersion      = "0.14.1"
val slickVersion      = "3.3.3"
val scalatestVersion  = "3.2.2"
val logbackVersion    = "1.2.3"
val postgresVersion   = "42.3.4"
val prometheusVersion = "0.15.0"

val commonDependencies = Seq("org.scalatest" %% "scalatest" % scalatestVersion % "test")

lazy val root = project
  .in(file("."))
  .dependsOn(rest)

lazy val api = project
  .in(file("api"))
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel"      %% "cats-core"           % "2.0.0",
      "com.typesafe.slick" %% "slick"               % slickVersion,
      "com.typesafe.slick" %% "slick-hikaricp"      % slickVersion,
      "io.prometheus"       % "simpleclient"        % prometheusVersion,
      "io.prometheus"       % "simpleclient_common" % prometheusVersion
    ) ++ commonDependencies
  )

lazy val bank = project
  .in(file("bank"))
  .settings(
    libraryDependencies ++= commonDependencies
  )
  .dependsOn(api)

lazy val rest = project
  .in(file("rest"))
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed"         % akkaVersion,
      "com.typesafe.akka" %% "akka-stream-typed"        % akkaVersion,
      "com.typesafe.akka" %% "akka-http"                % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-testkit"        % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % "test",
      "com.typesafe.akka" %% "akka-http-spray-json"     % akkaHttpVersion,
      "ch.qos.logback"     % "logback-classic"          % logbackVersion,
      "org.postgresql"     % "postgresql"               % postgresVersion,
      "io.circe"          %% "circe-core"               % circeVersion,
      "io.circe"          %% "circe-generic"            % circeVersion,
      "com.h2database"     % "h2"                       % "1.3.148"   % "test"
    ) ++ commonDependencies
  )
  .dependsOn(bank)
