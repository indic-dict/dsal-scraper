package sanskritCoders.dsal

import java.io.{File, PrintWriter}

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import sanskritCoders.dsal.scraper.getClass

class DsalDictionary(name: String, browser: JsoupBrowser) {
// Scrape links to entries, as in:
//  http://dsalsrv02.uchicago.edu/cgi-bin/philologic/search3advanced?dbname=burrow&searchdomain=headwords&display=utf8
//  Visit each page (http://dsalsrv02.uchicago.edu/cgi-bin/philologic/getobject.pl?c.0:1:2396.burrow) and dump an entry.
  // In some comparative dicts, there needs to be extra processing:
  // Interpret bold words (omitting Ta. etc..) as headwords.
  // Tamil (Ta.) Kolami (Kol.) Malayalam (Ma.) Naikṛi (Nk.) Iruḷa (Ir.) Naiki of Chanda (Nk. (Ch.)) Pālu Kuṟumba (PāKu.) Parji (Pa.) Ālu Kuṟumba (ĀlKu.) Gadba (Ga.) Beṭṭa Kuruba (Kurub.) Gondi (Go.) Kota (Ko.) Konḍa Toda (To.) Pengo (Pe.) Kannaḍa (Ka.) Manḍa (Manḍ.) Koḍagu (Koḍ.) Kui Tulu (Tu.) Kuwi Belari (Bel.) Kuṛux (Kur.) Koraga (Kor.) Malto (Malt.) Telugu (Te.) Brahui (Br.)
  private val log = LoggerFactory.getLogger(getClass.getName)

  val indexPage = s"http://dsalsrv02.uchicago.edu/cgi-bin/philologic/search3advanced?dbname=$name&searchdomain=headwords&display=utf8"

  var items: Seq[DsalDictItem] = Seq[DsalDictItem]()

  def setItems(): Unit = {
    val doc = browser.get(url = indexPage)
    val itemPElements = doc.underlying.getElementsByTag("p").toArray().map(_.asInstanceOf[Element])
    log.info(s"Checking about ${itemPElements.length} items")
    items = itemPElements.zipWithIndex.map{case (pElement: Element, index: Int) => {
      val url =  "http://dsalsrv02.uchicago.edu/" +  pElement.getElementsByAttribute("href").first().attr("href")
      log.debug(s"Getting $index out of ${itemPElements.length}.")
      new DsalDictItem(url=url, browser = browser)
    }}.filterNot(_.headwords.isEmpty)
    log.info(s"Got ${items.length} items!")
  }

  def makeBabylon(outfileStr: String): Unit = {
    setItems
    val outFileObj = new File(outfileStr)
    outFileObj.mkdirs()
    val destination = new PrintWriter(outFileObj)
    items.foreach(item => {
        val headersLine = item.headwords.mkString("|")
        val meaningLine =  item.entry.replace("\n", "<BR>")
        destination.println(headersLine)
        destination.println(meaningLine)
        destination.println("")
        // println(line)
        log.debug(headersLine)
        log.debug(meaningLine)
      })
    destination.close()
    log.info(s"Done writing ${items.length} items!")
  }
}

class DsalDictItem(url: String, browser: JsoupBrowser) {
  private val doc = browser.get(url = url)
  protected val article = Option(doc.underlying.getElementById("article"))
  // <hw><d>अलग</d></hw>
  val headwords: Seq[String] = article.map( element => {
    var elements = Array[Element]()
    element.getElementsByTag("hw").toArray(elements)
    elements.map(_.text()).toList
  }).getOrElse(Seq[String]())
  val entry: String = article.map(_.text()).getOrElse("")
}