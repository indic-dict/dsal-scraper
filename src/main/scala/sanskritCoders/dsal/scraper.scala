package sanskritCoders.dsal

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.slf4j.LoggerFactory

object scraper {
  //noinspection ScalaUnusedSymbol
  private val log = LoggerFactory.getLogger(getClass.getName)
  private val languageToPath = Map(
    ("marathi", "english") -> "/home/vvasuki/stardict-marathi/ma-head/other-entries/",
    ("marathi", "marathi") -> "/home/vvasuki/stardict-marathi/ma-head/ma-entries/",
    ("nepali", "english") -> "/home/vvasuki/stardict-nepali/ne-head/en-entries/",
    ("hindi", "english") -> "/home/vvasuki/stardict-hindi/hi-head/en-entries/",
    ("oriya", "english") -> "/home/vvasuki/stardict-oriya/or-head/"
  )
  private val dictsToLanguagePair = Map(
    "berntsen" -> ("marathi", "english"),
    "date" -> ("marathi", "marathi"),
    "schmidt" -> ("nepali", "english"), // Not PItemDict
    "turner" -> ("nepali", "english"),
    "bahri" -> ("hindi", "english"),
    "praharaj" -> ("oriya", "english")
  )

  private def dumpDictWithPLinkedIndex(name: String, nextItemIndexIn: Int  = 0): Unit = {
    val outfilePath = languageToPath(dictsToLanguagePair(name))
//    dict.setItems(limit = Some(5))
    val outfileStr = s"$outfilePath/$name/$name.babylon"
    val dict = DsalPDotPageIndexDict.getNewDict(name=name)
    dict.dump(outfileStr = outfileStr)
  }

  // TODO: Parse pages like http://dsalsrv02.uchicago.edu/cgi-bin/app/schmidt_query.py?display=utf8def&page=2

  def main(args: Array[String]): Unit = {
//    dumpDictWithPLinkedIndex(name = "date")
//    dumpDictWithPLinkedIndex(name = "turner")
//    dumpDictWithPLinkedIndex(name = "bahri")
    dumpDictWithPLinkedIndex(name = "praharaj")
  }
}
