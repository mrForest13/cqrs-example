import sbt._

object Dependencies {

  val config = Seq(
    "com.typesafe"          % "config"      % Version.config,
    "com.github.pureconfig" %% "pureconfig" % Version.pureConfig
  )

  val akka = Seq(
    "com.typesafe.akka" %% "akka-actor"           % Version.akka,
    "com.typesafe.akka" %% "akka-slf4j"           % Version.akka,
    "com.typesafe.akka" %% "akka-stream"          % Version.akka,
    "com.typesafe.akka" %% "akka-http"            % Version.akkaHttp,
    "com.typesafe.akka" %% "akka-http-spray-json" % Version.akkaHttp
  )

  val db = Seq(
    "mysql"              % "mysql-connector-java" % Version.mysql,
    "com.typesafe.slick" %% "slick"               % Version.slick,
    "com.typesafe.slick" %% "slick-hikaricp"      % Version.slick
  )

  val logging = Seq(
    "com.typesafe.scala-logging" %% "scala-logging"  % Version.scalaLogging,
    "ch.qos.logback"             % "logback-classic" % Version.logback
  )

  val test = Seq(
    "org.scalatest" %% "scalatest" % Version.scalaTest % Test
  )

  lazy val all: Seq[ModuleID] = config ++ akka ++ db ++ logging ++ test
}

object Version {
  val scala        = "2.12.6"
  val config       = "1.3.3"
  val pureConfig   = "0.9.2"
  val akka         = "2.5.15"
  val akkaHttp     = "10.1.4"
  val playJson     = "2.6.10"
  val akkaJson     = "1.21.0"
  val mysql        = "5.1.46"
  val slick        = "3.2.3"
  val scalaLogging = "3.9.0"
  val logback      = "1.2.3"
  val scalaTest    = "3.0.4"
}
