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

  val elastic4s = Seq(
    "com.sksamuel.elastic4s" %% "elastic4s-core"       % Version.elastic4s,
    "com.sksamuel.elastic4s" %% "elastic4s-http"       % Version.elastic4s,
    "com.sksamuel.elastic4s" %% "elastic4s-spray-json" % Version.elastic4s
  )

  val logging = Seq(
    "com.typesafe.scala-logging" %% "scala-logging"  % Version.scalaLogging,
    "ch.qos.logback"             % "logback-classic" % Version.logback
  )

  val other = Seq(
    "com.github.swagger-akka-http" %% "swagger-akka-http" % Version.swagger,
    "ch.megard"                    %% "akka-http-cors"    % Version.cors,
    "com.wix"                      %% "accord-core"       % Version.wix
  )

  val test = Seq(
    "org.scalatest"            %% "scalatest"          % Version.scalaTest % Test,
    "com.sksamuel.elastic4s"   %% "elastic4s-testkit"  % Version.elastic4s % Test,
    "com.sksamuel.elastic4s"   %% "elastic4s-embedded" % Version.elastic4s % Test,
    "org.apache.logging.log4j" % "log4j-core"          % Version.log4j     % Test,
    "org.apache.logging.log4j" % "log4j-api"           % Version.log4j     % Test,
    "com.typesafe.akka"        %% "akka-testkit"       % Version.akka      % Test,
    "com.wix"                  %% "accord-scalatest"   % Version.wix       % Test,
    "com.h2database"           % "h2"                  % Version.h2        % Test
  )

  lazy val all: Seq[ModuleID] = config ++ akka ++ db ++ elastic4s ++ logging ++ other ++ test
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
  val elastic4s    = "6.3.5"
  val scalaLogging = "3.9.0"
  val logback      = "1.2.3"
  val log4j        = "2.11.1"
  val swagger      = "1.0.0"
  val cors         = "0.3.0"
  val wix          = "0.7.2"
  val scalaTest    = "3.0.4"
  val h2           = "1.4.197"
}
