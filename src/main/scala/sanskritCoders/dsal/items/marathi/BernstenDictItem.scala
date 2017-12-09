package sanskritCoders.dsal.items.marathi

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.jsoup.nodes.Element
import sanskritCoders.dsal.items.DsalDictItem

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
