package sanskritCoders.dsal

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.jsoup.nodes.Element
import org.slf4j.{Logger, LoggerFactory}

class DsalDictionaryIterator[ItemType <: DsalDictItem](name: String, browser: JsoupBrowser) extends Iterator[DsalDictItem] {
// Scrape links to entries, as in:
//  http://dsalsrv02.uchicago.edu/cgi-bin/philologic/search3advanced?dbname=burrow&searchdomain=headwords&display=utf8
//  Visit each page (http://dsalsrv02.uchicago.edu/cgi-bin/philologic/getobject.pl?c.0:1:2396.burrow) and dump an entry.
  // In some comparative dicts, there needs to be extra processing:
  // Interpret bold words (omitting Ta. etc..) as headwords.
  // Tamil (Ta.) Kolami (Kol.) Malayalam (Ma.) Naikṛi (Nk.) Iruḷa (Ir.) Naiki of Chanda (Nk. (Ch.)) Pālu Kuṟumba (PāKu.) Parji (Pa.) Ālu Kuṟumba (ĀlKu.) Gadba (Ga.) Beṭṭa Kuruba (Kurub.) Gondi (Go.) Kota (Ko.) Konḍa Toda (To.) Pengo (Pe.) Kannaḍa (Ka.) Manḍa (Manḍ.) Koḍagu (Koḍ.) Kui Tulu (Tu.) Kuwi Belari (Bel.) Kuṛux (Kur.) Koraga (Kor.) Malto (Malt.) Telugu (Te.) Brahui (Br.)
  private val log = LoggerFactory.getLogger(getClass.getName)

  val indexPage = s"http://dsalsrv02.uchicago.edu/cgi-bin/philologic/search3advanced?dbname=$name&searchdomain=headwords&display=utf8"

  private val doc = browser.get(url = indexPage)
  log.info(s"Read $indexPage")
  private val itemPEElements = doc.underlying.getElementsByTag("p").toArray().map(_.asInstanceOf[Element])
  val itemPElementsIterator: Iterator[Element] = itemPEElements.iterator

  val likelySize: Int = itemPEElements.length

  def getNewDictItem: DsalDictItem = name match {
    case "berntsen" => new BernstenDictItem
  }

  log.info(s"Got about ${likelySize} items.")

  override def hasNext: Boolean = itemPElementsIterator.hasNext

  override def next(): DsalDictItem = {
    val pElement = itemPElementsIterator.next()
    val anchors = pElement.getElementsByAttribute("href")
    val url =  "http://dsalsrv02.uchicago.edu/" +  anchors.first().attr("href")
    //      log.debug(s"Getting $index out of ${itemPElements.length}.")
    val item = getNewDictItem
    item.fromUrl(url=url, browser = browser, pageTitle = Some(pElement.text()))
    item
  }
}

class DsalDictItem() {
  protected val log: Logger = LoggerFactory.getLogger(getClass.getName)
  var headwords: Seq[String] = Seq()
  var entry: String = _

  def fromUrl(url: String, browser: JsoupBrowser, pageTitle: Option[String] = None): Unit = {
    val doc = browser.get(url = url)
    //  log.debug(doc.body.text)
    headwords = pageTitle.map(Seq(_)).getOrElse(Seq())
    val nonTemplateElements = doc.underlying.body().children().toArray.map(_.asInstanceOf[Element]).filterNot(_.tagName() == "table").toList
    val article = nonTemplateElements.headOption
    entry = article.map(_.text()).getOrElse("")
  }

  def getMeaningLine: String = entry.replace("\n", "<BR>")
}

class BernstenDictItem extends DsalDictItem {
  override def fromUrl(url: String, browser: JsoupBrowser, pageTitle: Option[String] = None): Unit = {
    val doc = browser.get(url = url)
    //  log.debug(doc.body.text)
    val div2Elements = doc.underlying.getElementsByTag("div2")
    if (div2Elements.size() > 1) {
      log.warn(s"Skipping multi-item page titled ${pageTitle.getOrElse("UNK")} at $url")
    } else {
      val article = Option(div2Elements.first()).map(_.getElementsByTag("p").first())
      // <hw><d>अलग</d></hw>

      headwords = article.map(element => {
        var elements = element.getElementsByTag("hw").toArray().map(_.asInstanceOf[Element])
        //    log.debug(elements.mkString("|||"))
        elements.map(_.text()).toList
      }).getOrElse(Seq[String]())
      entry = article.map(_.text()).getOrElse("")
    }
  }
}