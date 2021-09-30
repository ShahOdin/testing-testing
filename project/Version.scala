object Version {
  val baseVersion: String = "0.0.1"
  def apply(): String     = baseVersion + "-" + scala.util.Properties.envOrElse("BUILD_NUMBER", "SNAPSHOT")
}