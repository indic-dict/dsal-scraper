package dsal

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import sanskritCoders.dsal.{BernstenDictItem, DsalDictItem}
import org.scalatest.FlatSpec
import org.slf4j.LoggerFactory

class DsalDictItemTest extends FlatSpec {
  private val log = LoggerFactory.getLogger(this.getClass)
  val browser: JsoupBrowser = JsoupBrowser.typed()
  private val testItems = Map{
    "berntsen"-> "http://dsalsrv02.uchicago.edu/cgi-bin/philologic/getobject.pl?c.0:1:3.berntsen"
  }

  "DsalDictItem" should "parse berntsen item correctly" in {
    val item = new BernstenDictItem
    item.fromUrl(url = testItems("berntsen"), browser = browser)
    assert(item.headwords.nonEmpty)
    assert(item.headwords.head == "अकर्मक")
    assert(item.entry.contains("intransitive"))
  }

}
