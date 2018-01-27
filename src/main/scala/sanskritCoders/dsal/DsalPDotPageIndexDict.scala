package sanskritCoders.dsal

import java.io.{File, FileWriter, PrintWriter, StringWriter}

import me.tongfei.progressbar.ProgressBar
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import sanskritCoders.dsal.DsalPDotPageIndexDict.{getClass, log}
import sanskritCoders.dsal.items.DsalDictItem

abstract class DsalPDotPageIndexDict(name: String, browser: JsoupBrowser) {
  private val log = LoggerFactory.getLogger(getClass.getName)

  def getPages: Seq[String] = {
    // Scrape links to entries, as in:
    //  http://dsalsrv02.uchicago.edu/cgi-bin/philologic/search3advanced?dbname=burrow&searchdomain=headwords&display=utf8

    val indexPage = s"http://dsalsrv02.uchicago.edu/cgi-bin/philologic/search3advanced?dbname=$name&searchdomain=headwords&display=utf8"

    val doc = browser.get(url = indexPage)
    log.info(s"Read $indexPage")
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
    val progressBar = new ProgressBar("itemsPb", pages.length)
    progressBar.start()
    try{
      pages.map(getItems).foreach(items => {
        items.foreach(_.dump(destination = destination))
        nextPageIndex = nextPageIndex + 1
        progressBar.step()
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
      progressBar.stop()
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

class DsalPDotPageIndexDictWithHwSiblingItems(name: String, browser: JsoupBrowser) extends DsalPDotPageIndexDict(name=name, browser = browser){
  private val log = LoggerFactory.getLogger(getClass.getName)

  override def getItems(pageUrl: String): Seq[DsalDictItem] = {
    val doc = browser.get(url = pageUrl)
    log.info(s"Read $pageUrl")
    val itemElements = doc.underlying.getElementsByTag("hw").toArray().map(_.asInstanceOf[Element])
    itemElements.map(element => {
      var definitionElements = Seq[Element]()
      var nextSibling = element.nextElementSibling()
      while (!Seq("hw", "table").contains(nextSibling.tagName())) {
        definitionElements = definitionElements :+ nextSibling
        nextSibling = element.nextElementSibling()
      }
      val headwords = Seq(element.text())
      val entry = definitionElements.map(_.text()).mkString(" ")
      val item = new DsalDictItem(headwords = headwords, entry = entry)
      item
    })
  }
}

object DsalPDotPageIndexDict {
  val browser: JsoupBrowser = JsoupBrowser.typed()
  private val log = LoggerFactory.getLogger(getClass.getName)
  def getNewDict(name: String): DsalPDotPageIndexDict = name match {
    case "turner" => new DsalPDotPageIndexDictWithDiv2Items(name = name, browser = browser)
    case "date" => new DsalPDotPageIndexDictWithDiv1Items(name = name, browser = browser)
  }
}