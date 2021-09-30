import sbt.{IO, enablePlugins}

name := "testing-testing"

version := Version()

libraryDependencies ++= Dependencies()

testFrameworks += new TestFramework("munit.Framework")

inThisBuild(
  List(
    scalaVersion := "2.13.6",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalafixScalaBinaryVersion := scalaBinaryVersion.value,
    scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0"
  )
)

Compile / scalafmtConfig := file("../.scalafmt.conf")
scalafmtOnCompile := true

lazy val credentialsLocation: Seq[File] = (
  sys.props.get("credentials.location").map(file).toList ++
    List(Path.userHome.asFile / ".ivy2" / ".credentials")
  ).filter(_.exists())

scalafixOnCompile := true

Compile / packageBin / publishArtifact := true

addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.13.1" cross CrossVersion.full)
addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1")

lazy val root =
  (project in file("."))
    .configs(IntegrationTest)
    .settings(Defaults.itSettings)

Compile / resourceGenerators += Def.task {
  val file = (Compile / resourceManaged).value / "build.properties"
  val lines = Seq(
    s"build.version=${version.value}",
    s"build.gitVersion=${git.gitHeadCommit.value.getOrElse(
      throw new RuntimeException("Unable to get commit hash")
    )}"
  )
  IO.writeLines(file, lines)
  Seq(file)
}

enablePlugins(GitVersioning)

useCoursier := false