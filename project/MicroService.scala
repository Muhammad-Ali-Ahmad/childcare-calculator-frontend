import com.typesafe.sbt.digest.Import._
import com.typesafe.sbt.uglify.Import._
import com.typesafe.sbt.web.Import._
import net.ground5hark.sbt.concat.Import._
import play.sbt.routes.RoutesKeys
import sbt.Keys._
import sbt.Tests.{Group, SubProcess}
import sbt.{Def, _}
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin._

trait MicroService {

  import uk.gov.hmrc._
  import DefaultBuildSettings._
  import TestPhases._
  import uk.gov.hmrc.SbtAutoBuildPlugin
  import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin

  val appName: String
  val appVersion: String

  lazy val appDependencies : Seq[ModuleID] = ???
  lazy val plugins : Seq[Plugins] = Seq.empty
  lazy val playSettings : Seq[Setting[_]] = Seq.empty

  lazy val scoverageSettings: Seq[Def.Setting[_]] = {
    import scoverage.ScoverageSbtPlugin.ScoverageKeys
    Seq(
      ScoverageKeys.coverageExcludedFiles := "<empty>;Reverse.*;.*filters.*;.*handlers.*;.*components.*;.*models.*;" +
        ".*BuildInfo.*;.*javascript.*;.*FrontendAuditConnector.*;.*Routes.*;.*GuiceInjector;.*DataCacheConnector;" +
        ".*ControllerConfiguration;.*LanguageSwitchController;.*repositories.*",
      ScoverageKeys.coverageMinimum := 85,
      ScoverageKeys.coverageFailOnMinimum := true,
      ScoverageKeys.coverageHighlighting := true,
      parallelExecution in Test := false
    )
  }

  lazy val microservice = Project(appName, file("."))
    .enablePlugins(Seq(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtDistributablesPlugin) ++ plugins : _*)
    .settings(playSettings : _*)
    .settings(version := appVersion)
    .settings(RoutesKeys.routesImport ++= Seq("uk.gov.hmrc.childcarecalculatorfrontend.models._"))
    .settings(scalaSettings: _*)
    .settings(publishingSettings: _*)
    .settings(defaultSettings(): _*)
    .settings(scoverageSettings)
    .settings(
      shellPrompt := ShellPrompt(appVersion),
      scalacOptions ++= Seq("-Xfatal-warnings", "-feature"),
      libraryDependencies ++= appDependencies,
      retrieveManaged := true,
      evictionWarningOptions in update := EvictionWarningOptions.default.withWarnScalaVersionEviction(false)
    )
    .configs(IntegrationTest)
    .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
    .settings(
      Keys.fork in IntegrationTest := false,
      unmanagedSourceDirectories in IntegrationTest <<= (baseDirectory in IntegrationTest)(base => Seq(base / "it")),
      addTestReportOption(IntegrationTest, "int-test-reports"),
      testGrouping in IntegrationTest := oneForkedJvmPerTest((definedTests in IntegrationTest).value),
      parallelExecution in IntegrationTest := false)
      .settings(resolvers ++= Seq(
        Resolver.bintrayRepo("hmrc", "releases"),
        Resolver.jcenterRepo,
        Resolver.bintrayRepo("emueller", "maven")
      ))
    .settings(
      // concatenate js
      Concat.groups := Seq(
        "javascripts/childcarecalculatorfrontend-app.js" -> group(Seq("javascripts/show-hide-content.js", "javascripts/childcarecalculatorfrontend.js"))
      ),
      // prevent removal of unused code which generates warning errors due to use of third-party libs
      UglifyKeys.compressOptions := Seq("unused=false", "dead_code=false"),
      pipelineStages := Seq(digest),
      // below line required to force asset pipeline to operate in dev rather than only prod
      pipelineStages in Assets := Seq(concat,uglify),
      // only compress files generated by concat
      includeFilter in uglify := GlobFilter("childcarecalculatorfrontend-*.js")
    )
}

private object TestPhases {

  def oneForkedJvmPerTest(tests: Seq[TestDefinition]) =
    tests map {
      test => new Group(test.name, Seq(test), SubProcess(ForkOptions(runJVMOptions = Seq("-Dtest.name=" + test.name))))
    }
}
