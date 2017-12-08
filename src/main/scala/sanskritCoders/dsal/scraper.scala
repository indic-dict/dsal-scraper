package sanskritCoders.dsal

import org.slf4j.LoggerFactory
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.elementList

object scraper {
  private val log = LoggerFactory.getLogger(getClass.getName)
  val browser = JsoupBrowser()
  def main(args: Array[String]): Unit = {

  }
}
