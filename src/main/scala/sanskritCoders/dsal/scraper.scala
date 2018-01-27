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
    ("sinhala", "english") -> "/home/vvasuki/stardict-sinhala/si-head/en-entries/",
    ("kashmiri", "english") -> "/home/vvasuki/stardict-kashmiri/ks-head/en-entries/",
    ("assamese", "english") -> "/home/vvasuki/stardict-assamese/as-head/en-entries/",
    ("telugu", "english") -> "/home/vvasuki/stardict-telugu/te-head/en-entries/",
    ("persian", "english") -> "/home/vvasuki/stardict-persian/te-head/en-entries/",
    ("oriya", "english") -> "/home/vvasuki/stardict-oriya/or-head/"
  )
  private val dictsToLanguagePair = Map(
    "berntsen" -> ("marathi", "english"),
    "molesworth" -> ("marathi", "english"),
    "vaze" -> ("marathi", "english"),
    "date" -> ("marathi", "marathi"),
    "schmidt" -> ("nepali", "english"), // Not PItemDict
    "turner" -> ("nepali", "english"),
    "bahri" -> ("hindi", "english"),
    "caturvedi" -> ("hindi", "english"),
    "fallon" -> ("hindi", "english"),
    "platts" -> ("hindi", "english"),
    "shakespear" -> ("hindi", "english"),
    "praharaj" -> ("oriya", "english"),
    "grierson" -> ("kashmiri", "english"),
    "candrakanta" -> ("assamese", "english"),
    "gwynn" -> ("telugu", "english"),
    "hayyim" -> ("persian", "english"),
    "steingass" -> ("persian", "english"),
    "carter" -> ("sinhala", "english")
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
//    dumpDictWithPLinkedIndex(name = "praharaj")
//    dumpDictWithPLinkedIndex(name = "molesworth")
    dumpDictWithPLinkedIndex(name = "vaze")
    //    dumpDictWithPLinkedIndex(name = "caturvedi")
    //    dumpDictWithPLinkedIndex(name = "fallon")
    //    dumpDictWithPLinkedIndex(name = "platts")
    //    dumpDictWithPLinkedIndex(name = "shakespear")
  }
}
