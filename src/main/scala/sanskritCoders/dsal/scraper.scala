package sanskritCoders.dsal

import java.io.{File, FileWriter, PrintWriter, StringWriter}

import me.tongfei.progressbar.ProgressBar
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.slf4j.LoggerFactory
import sanskritCoders.dsal.items.DsalPLinkedDictItemIterator

object scraper {
  private val log = LoggerFactory.getLogger(getClass.getName)
  private val languageToPath = Map{
    ("marathi", "english") -> "/home/vvasuki/stardict-marathi/ma-head/other-entries/"
    ("marathi", "marathi") -> "/home/vvasuki/stardict-marathi/ma-head/ma-entries/"
    ("nepali", "english") -> "/home/vvasuki/stardict-nepali/ne-head/en-entries/"
  }
  private val dictsToLanguagePair = Map{
    "berntsen" -> ("marathi", "english")
    "date" -> ("marathi", "marathi")
    "schmidt" -> ("nepali", "english")
    "turner" -> ("nepali", "english")
  }

  private def dumpDictWithPLinkedIndex(name: String, nextItemIndexIn: Int  = 0): Unit = {
    val browser: JsoupBrowser = JsoupBrowser.typed()
    val outfilePath = languageToPath(dictsToLanguagePair(name))
//    dict.setItems(limit = Some(5))
    val outfileStr = s"$outfilePath/$name/$name.babylon"
    val dict = new DsalPLinkedPageDict(name=name, browser = browser)
    dict.dump(outfileStr = outfileStr)
  }

  // TODO: Parse pages like http://dsalsrv02.uchicago.edu/cgi-bin/app/schmidt_query.py?display=utf8def&page=2

  def main(args: Array[String]): Unit = {
    //    dumpDictWithPLinkedIndex(name = "date")
        dumpDictWithPLinkedIndex(name = "turner")
  }
}
