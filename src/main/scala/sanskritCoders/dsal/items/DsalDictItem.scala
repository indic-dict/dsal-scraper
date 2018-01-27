package sanskritCoders.dsal.items

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.jsoup.nodes.{Element, TextNode}
import org.slf4j.{Logger, LoggerFactory}
import sanskritCoders.dsal.items.marathi.{BernstenDictItem, DateDictItem}
import scala.collection.JavaConverters._

case class DsalDictItem(var headwords: Seq[String] = Seq(), var entry: String = "") {
  protected val log: Logger = LoggerFactory.getLogger(getClass.getName)


  def fromUrl(url: String, browser: JsoupBrowser, pageTitle: Option[String] = None): Unit = {
    val doc = browser.get(url = url)
    //  log.debug(doc.body.text)
    headwords = pageTitle.map(Seq(_)).getOrElse(Seq())
    val nonTemplateElements = doc.underlying.body().children().toArray.map(_.asInstanceOf[Element]).filterNot(_.tagName() == "table").toList
    val article = nonTemplateElements.headOption
    entry = article.map(_.text()).getOrElse("")
  }

  def fromPageElement(element: Element): Unit = {
    /*
    <div2 type="article" id="अ_a">
<span class="head"><span class="hi">अ a</span></span>
<p></p><p>&amp;super2;, or <i>aḥ</i>, exclamation expressing (1) pity;  --  (2) disapprobation;  --  (3) indifference.</p>

</div2>
     */
    headwords = Seq(element.attr("id").split("_").toList.head)
    entry = element.text()
//    entry = element.textNodes().asScala.map(_.text()).mkString(" ")
//    log.debug(element.textNodes().toString)
//    log.debug(element.toString)
//    log.debug(entry)
//    System.exit(1)
  }

  def getMeaningLine: String = entry.replace("\n", "<BR>")
}

object DsalDictItem {
  def getNewDictItem(name: String): DsalDictItem = name match {
    case "berntsen" => new BernstenDictItem
    case "date" => new DateDictItem
  }
}