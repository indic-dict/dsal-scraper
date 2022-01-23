package sanskritCoders.dsal

import java.io.{File, FileWriter, PrintWriter, StringWriter}

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import sanskritCoders.dsal.items.DsalDictItem


case class DsalQStyleDict(name: String, browser: JsoupBrowser, entryEncoding:String="utf8def", hwTags:Seq[String]=Seq("hw"))  {
  // The class for dicts where we need to parse pages like http://dsalsrv02.uchicago.edu/cgi-bin/app/schmidt_query.py?display=utf8def&page=2
  // Or if roman utf definitions are preferred: 
  // https://dsalsrv04.uchicago.edu/cgi-bin/app/biswas-bengali_query.py?page=477&display=romutfdef

  private val log = LoggerFactory.getLogger(getClass.getName)
  private val pageUrlBase = s"https://dsal.uchicago.edu/cgi-bin/app/${name}_query.py?display=${entryEncoding}&page="
  
  def getItemsFromPage(pageUrl: String): (Seq[DsalDictItem], Option[String], Int) = {
    log.info(s"Reading $pageUrl")
    val doc = browser.get(url = pageUrl)
    val nextPageIdOpt = doc.underlying.getElementsByTag("a").toArray().map(_.asInstanceOf[Element]).find(_.text().startsWith("Next Page")).map(_.attr("href").replaceFirst(".+page=", ""))

    if (doc.toHtml.contains("Digital version of the dictionary has content for page")) {
      return (Seq(), nextPageIdOpt, 0)
    }
    val itemElements = doc.underlying.getElementsByTag(hwTags.head.split(",").head).toArray().map(_.asInstanceOf[Element]).map(_.parent())

    val items = itemElements.map(element => {
      val item = new DsalDictItem()
      try {
        item.fromPageDivElement(element, hwTags=hwTags, dictName=Some(name))
        Some(item)
      } catch {
        case ex: NoSuchElementException => {
          None
        }
      }
    })
    val skippedItemsCount = items.count(_.isEmpty)
    val finalItems = items.filter(_.isDefined).map(_.get)
    (finalItems, nextPageIdOpt, skippedItemsCount)
  }

  def dump(outfileStr:String, startPageIndex: String  = "1"): Unit = {
    val outFileObj = new File(outfileStr)
    outFileObj.getParentFile.mkdirs()
    val destination = new PrintWriter(new FileWriter(outFileObj, startPageIndex.toInt > 1))

    var numPages = 0
    var numItems = 0
    var numSkippedItems = 0
    var nextPageIndex = startPageIndex

    var pageItems = Seq[DsalDictItem]()
    try{
      do {
        val (pageItems, nextPageIdOpt, skippedItemsCount) = getItemsFromPage(pageUrl = pageUrlBase + nextPageIndex)
        numItems = numItems + pageItems.length
        numSkippedItems = numSkippedItems + skippedItemsCount
        log.debug(s"Got ${pageItems.length}, Skipped ${skippedItemsCount} from ${pageUrlBase + nextPageIndex}")
        pageItems.foreach(_.dump(destination = destination))
        nextPageIndex = nextPageIdOpt.getOrElse(null)
        numPages = numPages + 1
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
      log.info(s"Done writing ${numPages} pages! ${numItems} items, ${numSkippedItems} skipped")
    }

  }

}
