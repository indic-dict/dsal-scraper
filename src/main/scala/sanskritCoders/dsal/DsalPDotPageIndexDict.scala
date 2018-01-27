package sanskritCoders.dsal

import java.io.{File, FileWriter, PrintWriter, StringWriter}

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.jsoup.{Connection, Jsoup}
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import sanskritCoders.dsal.items.DsalDictItem

abstract class DsalPDotPageIndexDict(name: String, browser: JsoupBrowser) {
  private val log = LoggerFactory.getLogger(getClass.getName)

  def getPages: Seq[String] = {
    // Scrape links to entries, as in:
    //  http://dsalsrv02.uchicago.edu/cgi-bin/philologic/search3advanced?dbname=burrow&searchdomain=headwords&display=utf8

    val indexPage = s"http://dsalsrv02.uchicago.edu/cgi-bin/philologic/search3advanced?dbname=$name&searchdomain=headwords&display=utf8"

    log.info(s"Reading $indexPage")
    val doc = browser.get(url = indexPage)
    val itemPageElements = doc.underlying.getElementsByAttributeValueContaining("href", "?p.").toArray().map(_.asInstanceOf[Element])
    val itemPageUrls = itemPageElements.map("http://dsalsrv02.uchicago.edu" + _.attr("href")).distinct
    val likelySize: Int = itemPageUrls.length
    log.info(s"Got about ${likelySize} items.")
    itemPageUrls
  }

  def getItems(pageUrl: String): Seq[DsalDictItem]

  def dump(outfileStr:String, nextPageIndexIn: Int  = 0): Unit = {
    val outFileObj = new File(outfileStr)
    outFileObj.getParentFile.mkdirs()
    val destination = new PrintWriter(new FileWriter(outFileObj, nextPageIndexIn > 0))
    var nextPageIndex = nextPageIndexIn
    val pages = getPages.drop(nextPageIndex-1)
    try{
      pages.map(getItems).foreach(items => {
        items.foreach(_.dump(destination = destination))
        nextPageIndex = nextPageIndex + 1
      })
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

class DsalPDotPageIndexDictWithDiv2Items(name: String, browser: JsoupBrowser) extends DsalPDotPageIndexDict(name=name, browser = browser){
  private val log = LoggerFactory.getLogger(getClass.getName)

  override def getItems(pageUrl: String): Seq[DsalDictItem] = {
    val doc = browser.get(url = pageUrl)
    log.info(s"Read $pageUrl")
    val itemElements = doc.underlying.getElementsByAttributeValueContaining("type", "article").toArray().map(_.asInstanceOf[Element])
    /*
    Strangely, the above fails for some elements in http://dsalsrv02.uchicago.edu/cgi-bin/philologic/getobject.pl?c.0:1.turner :
    It yields elements like:
    <div2 type="article" id="अ_a">
    <span class="head"><span class="hi">अ a</span></span>
    </div2>
    rather than:
    <div2 type="article" id="अ_a">
      <span class="head"><span class="hi">अ a</span></span>
      <p></p><p>&amp;super2;, or <i>aḥ</i>, exclamation expressing (1) pity;  --  (2) disapprobation;  --  (3) indifference.</p>
*/
    itemElements.map(element => {
      val item = new DsalDictItem()
      item.fromDiv2Element(element)
      item
    })
  }
}



class DsalPDotPageIndexDictWithDiv1Items(name: String, browser: JsoupBrowser) extends DsalPDotPageIndexDict(name=name, browser = browser){
  private val log = LoggerFactory.getLogger(getClass.getName)

  override def getItems(pageUrl: String): Seq[DsalDictItem] = {
    val doc = browser.get(url = pageUrl)
    log.info(s"Read $pageUrl")
    val itemElements = doc.underlying.getElementsByTag("div1").toArray().map(_.asInstanceOf[Element])
    itemElements.map(element => {
      val item = new DsalDictItem()
      item.fromDiv1Element(element)
      item
    })
  }
}

class DsalPDotPageIndexDictWithBrSeparatedItems(name: String, browser: JsoupBrowser) extends DsalPDotPageIndexDict(name=name, browser = browser){
  private val log = LoggerFactory.getLogger(getClass.getName)

  override def getItems(pageUrl: String): Seq[DsalDictItem] = {
    val doc = browser.get(url = pageUrl)
    log.info(s"Read $pageUrl")
    val itemElements = doc.underlying.html().split("<br>").tail.map(x => {Jsoup.parse(x)})
    itemElements.map(element => {
      val headwords = Seq(element.getElementsByAttributeValue("class", "head").asScala.head.text())
      element.getElementsByTag("table").asScala.foreach(_.remove())
      element.getElementsByTag("div").asScala.foreach(_.remove())
      val entry = element.text()
      val item = new DsalDictItem(headwords = headwords, entry = entry)
      item
    })
  }
}

class DsalPDotPageIndexDictWithDiv1SeparatedItems(name: String, browser: JsoupBrowser) extends DsalPDotPageIndexDict(name=name, browser = browser){
  private val log = LoggerFactory.getLogger(getClass.getName)

  override def getItems(pageUrl: String): Seq[DsalDictItem] = {
    val doc = browser.get(url = pageUrl)
    log.info(s"Read $pageUrl")
    val itemElements = doc.underlying.html().split("<div1>").tail.map(x => {Jsoup.parse(x)})
    itemElements.map(element => {
      val headwords = Seq(element.getElementsByAttributeValue("class", "head").asScala.head.text())
      element.getElementsByTag("table").asScala.foreach(_.remove())
      element.getElementsByTag("div").asScala.foreach(_.remove())
      val entry = element.text()
      val item = new DsalDictItem(headwords = headwords, entry = entry)
      item
    })
  }
}

object DsalPDotPageIndexDict {
  val browser: JsoupBrowser = new JsoupBrowser() {
    override def defaultRequestSettings(conn: Connection): Connection = {
      super.defaultRequestSettings(conn).timeout(5 * 60 * 1000 /* 5 min*/)
    }
  }
  private val log = LoggerFactory.getLogger(getClass.getName)
  def getNewDict(name: String): DsalPDotPageIndexDict = name match {
    case "turner" => new DsalPDotPageIndexDictWithDiv2Items(name = name, browser = browser)
    case "date" => new DsalPDotPageIndexDictWithDiv1Items(name = name, browser = browser)
    case "bahri" => new DsalPDotPageIndexDictWithBrSeparatedItems(name = name, browser = browser)
    case "praharaj" => new DsalPDotPageIndexDictWithDiv1SeparatedItems(name = name, browser = browser)
    case "molesworth" => new DsalPDotPageIndexDictWithDiv1SeparatedItems(name = name, browser = browser)
    case "vaze" => new DsalPDotPageIndexDictWithDiv2Items(name = name, browser = browser)
    case "caturvedi" => new DsalPDotPageIndexDictWithDiv2Items(name = name, browser = browser)
  }
}