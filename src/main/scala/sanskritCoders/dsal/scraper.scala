package sanskritCoders.dsal

import org.slf4j.LoggerFactory
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.elementList

object scraper {
  private val log = LoggerFactory.getLogger(getClass.getName)
  private val languageToPath = Map{("marathi", "english") -> "/home/vvasuki/stardict-marathi/ma-head/"}
  private val dictsToLanguagePair = Map{
    "berntsen" -> ("marathi", "english")
  }
  def dumpDict(name: String) = {
    val browser: JsoupBrowser = JsoupBrowser.typed()
    val dict = new DsalDictionary(name=name, browser = browser)
    val outfilePath = languageToPath(dictsToLanguagePair(name))
    dict.makeBabylon(outfileStr = s"$outfilePath/$name/$name.babylon")
  }

  def main(args: Array[String]): Unit = {
    dumpDict(name = "berntsen")
  }
}
