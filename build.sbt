import sbt.Keys.scalacOptions

lazy val commonSettings = Seq(
  organization := "com.example",
  version := sys.env.getOrElse("VERSION", "0.1.0"),
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
  mainClass in assembly := Some("com.cqrs.write.AppLauncher"),
  assemblyJarName in assembly := "cqrs-write.jar",
)

lazy val options = Seq(
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

lazy val cqrs = (project in file("."))
  .settings(
    name := "cqrs",
    commonSettings,
    addCommandAlias("docker", ";cqrs-read/docker;cqrs-write/docker;cqrs-read/docker")
  )
  .aggregate(`cqrs-read`, `cqrs-write`)

lazy val `cqrs-common` = project
  .settings(
    name := "cqrs-common",
    commonSettings,
    scalacOptions ++= options,
    libraryDependencies ++= Dependencies.common
  )

lazy val `cqrs-read` = project
  .dependsOn(`cqrs-common`)
  .enablePlugins(DockerPlugin)
  .settings(
    name := "cqrs-read",
    commonSettings,
    assemblyReadSideSettings,
    dockerSettings,
    scalacOptions ++= options,
    libraryDependencies ++= Dependencies.read,
    parallelExecution in Test := false,
  )

lazy val `cqrs-write` = project
  .dependsOn(`cqrs-common`)
  .enablePlugins(DockerPlugin)
  .settings(
    name := "cqrs-write",
    commonSettings,
    assemblyWriteSideSettings,
    dockerSettings,
    scalacOptions ++= options,
    libraryDependencies ++= Dependencies.write,
    parallelExecution in Test := false,
  )
