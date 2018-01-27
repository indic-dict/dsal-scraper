package sanskritCoders.dsal.items

import java.io.{File, FileWriter, PrintWriter, StringWriter}

import me.tongfei.progressbar.ProgressBar
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import sanskritCoders.dsal.scraper.log


class DsalPLinkedDictItemIterator(name: String, browser: JsoupBrowser) extends Iterator[DsalDictItem] {
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

  log.info(s"Got about ${likelySize} items.")

  override def hasNext: Boolean = itemPElementsIterator.hasNext

  override def next(): DsalDictItem = {
    val pElement = itemPElementsIterator.next()
    val anchors = pElement.getElementsByAttribute("href")
    val url =  "http://dsalsrv02.uchicago.edu/" +  anchors.first().attr("href")
    //      log.debug(s"Getting $index out of ${itemPElements.length}.")
    val item = DsalDictItem.getNewDictItem(name = name)
    item.fromUrl(url=url, browser = browser, pageTitle = Some(pElement.text()))
    item
  }

  def dump(outfileStr:String, nextItemIndexIn: Int  = 0): Unit = {
    val outFileObj = new File(outfileStr)
    outFileObj.getParentFile.mkdirs()
    val destination = new PrintWriter(new FileWriter(outFileObj, nextItemIndexIn > 0))
    var nextItemIndex = nextItemIndexIn
    this.take(nextItemIndex)
    val progressBar = new ProgressBar("itemsPb", this.likelySize)
    progressBar.start()
    try{
      this.foreach(item => {
        item.dump(destination = destination)
        nextItemIndex = nextItemIndex + 1
        progressBar.step()
      })
    } catch {
      case ex: Exception => {
        val sw = new StringWriter
        ex.printStackTrace(new PrintWriter(sw))
        log.error("")
        log.error(sw.toString)
        log.error(s"nextItemIndex should be ${nextItemIndex}")
      }
    }
    finally {
      destination.close()
      progressBar.stop()
      log.info(s"Done writing ${nextItemIndex} items!")
    }

  }

}








