import sbt._

object Dependencies {
  val enumeratumVersion       = "1.7.0"
  val munitCatsEffectVersion  = "1.0.5"
  val scalaCheckVersion       = "0.7.29"
  val monocleVersion          = "3.0.0"
  val disciplineVersion       = "1.0.9"
  val scalacheckEffectVersion = "0.6.0"
  val catsEffectVersion       = "3.2.9"
  val catsLawsVersion         = "2.6.1"
  val circeVersion            = "0.14.1"
  val fs2Version              = "3.1.1"

  val deps: Seq[ModuleID] = Seq(
    "com.beachape"  %% "enumeratum"    % enumeratumVersion,
    "dev.optics"    %% "monocle-core"  % monocleVersion,
    "dev.optics"    %% "monocle-macro" % monocleVersion,
    "org.typelevel" %% "cats-effect"   % catsEffectVersion
  )

  val testDeps: Seq[ModuleID] = Seq(
    "org.typelevel" %% "munit-cats-effect-3"     % munitCatsEffectVersion,
    "org.scalameta" %% "munit-scalacheck"        % scalaCheckVersion,
    "org.typelevel" %% "scalacheck-effect-munit" % scalacheckEffectVersion,
    "com.beachape"  %% "enumeratum-scalacheck"   % enumeratumVersion,
    "com.beachape"  %% "enumeratum-cats"         % enumeratumVersion,
    "org.typelevel" %% "cats-laws"               % catsLawsVersion,
    "org.typelevel" %% "discipline-munit"        % disciplineVersion,
    "io.circe"      %% "circe-core"              % circeVersion,
    "io.circe"      %% "circe-generic"           % circeVersion,
    "io.circe"      %% "circe-parser"            % circeVersion,
    "com.beachape"  %% "enumeratum-circe"        % enumeratumVersion,
    "co.fs2"        %% "fs2-core"                % fs2Version,
    "co.fs2"        %% "fs2-io"                  % fs2Version
  ).map(_ % s"$Test,$IntegrationTest")

  def apply(): Seq[ModuleID] = deps ++ testDeps
}
