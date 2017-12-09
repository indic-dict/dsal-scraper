package sanskritCoders.dsal.items.marathi

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.jsoup.nodes.Element
import sanskritCoders.dsal.items.DsalDictItem

class DateDictItem extends DsalDictItem {
  override def fromUrl(url: String, browser: JsoupBrowser, pageTitle: Option[String] = None): Unit = {
    val doc = browser.get(url = url)
    //  log.debug(doc.body.text)
    val div1Elements = doc.underlying.getElementsByTag("div1")
    if (div1Elements.size() > 1) {
      log.warn(s"Skipping multi-item page titled ${pageTitle.getOrElse("UNK")} at $url")
    } else {
      val headwordSpan = Option(div1Elements.first()).map(_.getElementsByClass("head").first())

      headwords = headwordSpan.map(e => Seq(e.text())).getOrElse(Seq())
      entry = headwordSpan.map(span => {
        val siblingElements =  span.siblingElements().toArray.map(_.asInstanceOf[Element]).filter(_.tagName() == "d")
        if (siblingElements.length < 1) {
          log.warn(s"$url yielded no definition!")
          ""
        } else {
          siblingElements.head.text()
        }
      }).getOrElse("")
    }
  }
}
