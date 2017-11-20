import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._

/**
 * Add your integration spec here.
 * An integration test will fire up a whole play application in a real (or headless) browser.
 */
class IntegrationSpec extends PlaySpec with OneServerPerTest with OneBrowserPerTest with HtmlUnitFactory {

  "Application" should {

    "work from within a browser" in {

      go to ("http://localhost:" + port)

      pageSource must include ("cats")
      pageSource must include ("frisbees")
      pageSource must include ("liquor")
    }
  }
}
