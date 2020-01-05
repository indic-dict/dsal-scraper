package sanskritCoders.dsal

import java.io.{File, FileWriter, PrintWriter, StringWriter}

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import sanskritCoders.dsal.items.DsalDictItem


// TODO: Parse pages like http://dsalsrv02.uchicago.edu/cgi-bin/app/schmidt_query.py?display=utf8def&page=2
// Or if roman utf definitions are preferred: 
// https://dsalsrv04.uchicago.edu/cgi-bin/app/biswas-bengali_query.py?page=477&display=romutfdef

case class DsalQStyleDict(name: String, browser: JsoupBrowser, entryEncoding:String="romutfdef")  {
  private val log = LoggerFactory.getLogger(getClass.getName)
  private val pageUrlBase = s"https://dsalsrv04.uchicago.edu/cgi-bin/app/biswas-bengali_query.py?display=${entryEncoding}&page="
  
  def getItemsFromPage(pageUrl: String): (Seq[DsalDictItem], Option[String]) = {
    val doc = browser.get(url = pageUrl)
    log.info(s"Read $pageUrl")
    val nextPageIdOpt = doc.underlying.getElementsByTag("a").toArray().map(_.asInstanceOf[Element]).find(_.text().startsWith("Next Page")).map(_.attr("href").replaceFirst(".+page=", ""))

    if (doc.toHtml.contains("Digital version of the dictionary has content for page")) {
      return (Seq(), nextPageIdOpt)
    }
    val itemElements = doc.underlying.getElementsByTag("span").toArray().map(_.asInstanceOf[Element]).map(_.parent())

    val items = itemElements.map(element => {
      val item = new DsalDictItem()
      item.fromPageDivElement(element)
      item
    })
    (items, nextPageIdOpt)
  }

  def dump(outfileStr:String, startPageIndex: String  = "1"): Unit = {
    val outFileObj = new File(outfileStr)
    outFileObj.getParentFile.mkdirs()
    val destination = new PrintWriter(new FileWriter(outFileObj, startPageIndex.toInt > 1))

    var nextPageIndex = startPageIndex

    var pageItems = Seq[DsalDictItem]()
    try{
      do {
        val (pageItems, nextPageIdOpt) = getItemsFromPage(pageUrl = pageUrlBase + nextPageIndex)
        log.debug(s"Got ${pageItems} items.")
        pageItems.foreach(_.dump(destination = destination))
        nextPageIndex = nextPageIdOpt.getOrElse(null)
        // Itermediate pages like https://dsalsrv04.uchicago.edu/cgi-bin/app/biswas-bengali_query.py?page=3 may not contain items.
      } while (nextPageIndex != null)
    } catch {
      case ex: Exception => {
        val sw = new StringWriter
        ex.printStackTrace(new PrintWriter(sw))
        log.error("")
        log.error(sw.toString)
        log.error(s"nextPageIndex should be ${nextPageIndex}")
      }
    }
    finally {
      destination.close()
      log.info(s"Done writing ${nextPageIndex} pages!")
    }

  }

}
