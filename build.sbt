organization in ThisBuild := "getVisibility"
version in ThisBuild := "1.0"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.13.0"
//lagomServiceGatewayPort in ThisBuild := 9000
lagomServiceLocatorEnabled in ThisBuild := true

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.10" % "2.2-M1"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.3" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.1.1" % Test

lazy val `myproject` = (project in file("."))
.aggregate(`calculate-api`, `calculate-impl`)

lazy val `calculate-api` = (project in file("calculate-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `calculate-impl` = (project in file("calculate-impl"))
  .enablePlugins(LagomScala)
 // .disablePlugins(LagomAkkaHttpServer)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest,
      filters
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`calculate-api`)
