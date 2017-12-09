package dsal

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import sanskritCoders.dsal.{DsalDictItem, DsalDictionaryIterator}
import org.scalatest.{Assertion, FlatSpec}
import org.slf4j.LoggerFactory
import org.json4s._
import org.json4s.native.Serialization

import scala.io.Source

case class DictItemTestCase(url: Option[String], headword: Option[String], entryContent: Option[String])

class DsalDictItemTest extends FlatSpec {
  private val log = LoggerFactory.getLogger(this.getClass)
  implicit val formats: DefaultFormats.type = DefaultFormats
  val browser: JsoupBrowser = JsoupBrowser.typed()

  private def testItemParse(name: String, testSpec : DictItemTestCase): Assertion = {
    val item = DsalDictItem.getNewDictItem(name = name)
    item.fromUrl(url = testSpec.url.get, browser = browser)
    log.info("Testcase name: " + name)
    log.info("Test spec: " + testSpec.toString)
    assert(item.headwords.nonEmpty)
    assert(item.headwords.head == testSpec.headword.get)
    assert(item.entry.contains(testSpec.entryContent.get))
    assert(!item.entry.contains("Next entry"))
  }

  "DsalDictItem" should "parse items correctly" in {
    val source = Source.fromResource("DictItemParseTests.json")
    val tests = Serialization.read[Map[String, DictItemTestCase]](source.mkString)
    log.debug(tests.toString)
    tests.foreach{
      case (name: String, testCase: DictItemTestCase) => testItemParse(name=name, testSpec = testCase)
    }
  }
}
