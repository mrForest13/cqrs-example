import sbt.Keys.scalacOptions

val commonSettings = Seq(
  organization := "com.example",
  version := "latest",
  scalaVersion := Version.scala
)

lazy val dockerSettings = Seq(
  dockerfile in docker := {
    val artifact: File     = assembly.value
    val artifactTargetPath = s"/app/${artifact.name}"

    new Dockerfile {
      from("openjdk:8-jre")
      add(artifact, artifactTargetPath)
      entryPoint("java", "-jar", artifactTargetPath)
    }
  },
  imageNames in docker := Seq(
    ImageName(
      namespace = Some(organization.value),
      repository = name.value,
      tag = Some(version.value)
    )
  )
)

lazy val assemblyReadSideSettings = Seq(
  mainClass in assembly := Some("com.cqrs.read.AppLauncher"),
  assemblyJarName in assembly := "cqrs-read.jar",
)

lazy val assemblyWriteSideSettings = Seq(
  mainClass in assembly := Some("com.cqrs.wrie.AppLauncher"),
  assemblyJarName in assembly := "cqrs-write.jar",
)

val options = Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-feature",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ypartial-unification",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused",
  "-Ywarn-value-discard"
)

lazy val root = (project in file("."))
  .settings(
    name := "cqrs",
    commonSettings
  )
  .aggregate(`cqrs-read`, `cqrs-write`)

val `cqrs-common` = project
  .settings(
    name := "common",
    commonSettings,
    scalacOptions ++= options,
    libraryDependencies ++= Dependencies.common
  )

val `cqrs-event` = project
  .settings(
    name := "event",
    commonSettings,
    scalacOptions ++= options
  )

val `cqrs-read` = project
  .dependsOn(`cqrs-common`)
  .dependsOn(`cqrs-event`)
  .enablePlugins(DockerPlugin)
  .settings(
    name := "read",
    commonSettings,
    assemblyReadSideSettings,
    dockerSettings,
    scalacOptions ++= options,
    libraryDependencies ++= Dependencies.read,
    parallelExecution in Test := false,
  )

val `cqrs-write` = project
  .dependsOn(`cqrs-common`)
  .dependsOn(`cqrs-event`)
  .enablePlugins(DockerPlugin)
  .settings(
    name := "write",
    commonSettings,
    assemblyWriteSideSettings,
    dockerSettings,
    scalacOptions ++= options,
    libraryDependencies ++= Dependencies.write,
    parallelExecution in Test := false,
  )
