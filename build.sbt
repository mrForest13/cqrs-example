import sbt.Keys.scalacOptions

val nameSettings = Seq(
  organization := "com.example",
  name := "cqrs-example",
  version := "latest",
  scalaVersion := Version.scala
)

lazy val dockerSettings = Seq(
  dockerImageCreationTask := docker.value,
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
    ImageName(s"${organization.value}/${name.value}:latest"),
    ImageName(
      namespace = Some(organization.value),
      repository = name.value,
      tag = Some(version.value)
    )
  ),
  buildOptions in docker := BuildOptions(
    cache = false,
    removeIntermediateContainers = BuildOptions.Remove.Always,
    pullBaseImage = BuildOptions.Pull.Always
  ),
)

lazy val assemblySettings = Seq(
  mainClass in assembly := Some("com.cqrs.example.AppLauncher"),
  assemblyJarName in assembly := "cqrs-example.jar",
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

val `cqrs-example` = (project in file("."))
  .enablePlugins(DockerPlugin)
  .enablePlugins(DockerComposePlugin)
  .settings(
    nameSettings,
    assemblySettings,
    dockerSettings,
    scalacOptions ++= options,
    libraryDependencies ++= Dependencies.all
  )
