import sbt._

object Dependencies {

  lazy val config = Seq(
    "com.typesafe"          % "config"      % Version.config,
    "com.github.pureconfig" %% "pureconfig" % Version.pureConfig
  )

  lazy val akka = Seq(
    "com.typesafe.akka" %% "akka-actor"   % Version.akka,
    "com.typesafe.akka" %% "akka-cluster" % Version.akka,
    "com.typesafe.akka" %% "akka-slf4j"   % Version.akka,
    "com.typesafe.akka" %% "akka-stream"  % Version.akka,
    "com.typesafe.akka" %% "akka-testkit" % Version.akka % Test
  )

  lazy val akkaHttp = Seq(
    "com.typesafe.akka" %% "akka-http"            % Version.akkaHttp,
    "com.typesafe.akka" %% "akka-http-spray-json" % Version.akkaHttp,
    "com.typesafe.akka" %% "akka-http-testkit"    % Version.akkaHttp % Test
  )

  lazy val mysql = Seq(
    "mysql"              % "mysql-connector-java" % Version.mysql,
    "com.typesafe.slick" %% "slick"               % Version.slick,
    "com.typesafe.slick" %% "slick-hikaricp"      % Version.slick,
    "com.h2database"     % "h2"                   % Version.h2 % Test
  )

  lazy val elastic4s = Seq(
    "com.sksamuel.elastic4s" %% "elastic4s-core"       % Version.elastic4s,
    "com.sksamuel.elastic4s" %% "elastic4s-http"       % Version.elastic4s,
    "com.sksamuel.elastic4s" %% "elastic4s-spray-json" % Version.elastic4s
  )

  lazy val logging = Seq(
    "com.typesafe.scala-logging" %% "scala-logging"  % Version.scalaLogging,
    "ch.qos.logback"             % "logback-classic" % Version.logback
  )

  lazy val wix = Seq(
    "com.wix" %% "accord-core"      % Version.wix,
    "com.wix" %% "accord-scalatest" % Version.wix % Test
  )

  lazy val other = Seq(
    "com.github.swagger-akka-http" %% "swagger-akka-http" % Version.swagger,
    "ch.megard"                    %% "akka-http-cors"    % Version.cors
  )

  lazy val test = Seq(
    "org.scalatest" %% "scalatest" % Version.scalaTest % Test,
    "org.scalamock" %% "scalamock" % Version.scalaMock % Test
  )

  lazy val common: Seq[ModuleID] = akka ++ akkaHttp ++ logging ++ wix ++ test

  lazy val eventSourcing: Seq[ModuleID] = config ++ akka ++ elastic4s ++ logging ++ test

  lazy val read
    : Seq[ModuleID] = config ++ akka ++ akkaHttp ++ elastic4s ++ logging ++ wix ++ other ++ test

  lazy val write
    : Seq[ModuleID] = config ++ akka ++ akkaHttp ++ mysql ++ logging ++ wix ++ other ++ test
}

object Version {
  val scala        = "2.12.6"
  val config       = "1.3.3"
  val pureConfig   = "0.9.2"
  val akka         = "2.5.15"
  val akkaHttp     = "10.1.5"
  val playJson     = "2.6.10"
  val akkaJson     = "1.21.0"
  val mysql        = "5.1.46"
  val slick        = "3.2.3"
  val elastic4s    = "6.3.5"
  val scalaLogging = "3.9.0"
  val logback      = "1.2.3"
  val swagger      = "1.0.0"
  val cors         = "0.3.0"
  val wix          = "0.7.2"
  val scalaTest    = "3.0.4"
  val scalaMock    = "4.0.0"
  val h2           = "1.4.197"
}
