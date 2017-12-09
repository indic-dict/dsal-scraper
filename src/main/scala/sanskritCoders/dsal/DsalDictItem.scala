package sanskritCoders.dsal

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.jsoup.nodes.Element
import org.slf4j.{Logger, LoggerFactory}
import sanskritCoders.dsal.marathi.{BernstenDictItem, DateDictItem}

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

object DsalDictItem {
  def getNewDictItem(name: String): DsalDictItem = name match {
    case "berntsen" => new BernstenDictItem
    case "date" => new DateDictItem
  }
}