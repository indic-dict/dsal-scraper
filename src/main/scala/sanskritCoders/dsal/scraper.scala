package sanskritCoders.dsal

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.jsoup.Connection
import org.slf4j.LoggerFactory

object scraper {
  //noinspection ScalaUnusedSymbol
  private val log = LoggerFactory.getLogger(getClass.getName)
  private val languageToPath = Map(
    ("marathi", "english") -> "/home/vvasuki/indic-dict/stardict-marathi/ma-head/other-entries/",
    ("marathi", "marathi") -> "/home/vvasuki/indic-dict/stardict-marathi/ma-head/ma-entries/",
    ("nepali", "english") -> "/home/vvasuki/indic-dict/stardict-nepali/ne-head/en-entries/",
    ("hindi", "english") -> "/home/vvasuki/indic-dict/stardict-hindi/hi-head/en-entries/",
    ("sinhala", "english") -> "/home/vvasuki/indic-dict/stardict-sinhala/si-head/en-entries/",
    ("kashmiri", "english") -> "/home/vvasuki/indic-dict/stardict-kashmiri/ks-head/en-entries/",
    ("assamese", "english") -> "/home/vvasuki/indic-dict/stardict-assamese/as-head/en-entries/",
    ("telugu", "english") -> "/home/vvasuki/indic-dict/stardict-telugu/te-head/en-entries/",
    ("persian", "english") -> "/home/vvasuki/indic-dict/stardict-persian/te-head/en-entries/",
    ("malayalam", "english") -> "/home/vvasuki/indic-dict/stardict-malayalam/ml-head/en-entries/",
    ("panjabi", "english") -> "/home/vvasuki/indic-dict/stardict-panjabi/pa-head/en-entries/",
    ("bengali", "english") -> "/home/vvasuki/indic-dict/stardict-bengali/bn-head/en-entries/",
    ("bengali", "bengali") -> "/home/vvasuki/indic-dict/stardict-bengali/bn-head/bn-entries/",
    ("oriya", "english") -> "/home/vvasuki/indic-dict/stardict-oriya/or-head/"
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
    "singh" -> ("panjabi", "english"),
    "gundert" -> ("malayalam", "english"),
    "gwynn" -> ("telugu", "english"),
    "hayyim" -> ("persian", "english"),
    "steingass" -> ("persian", "english"),
    "biswas-bengali" -> ("bengali", "english"),
    "carter" -> ("sinhala", "english")
  )

  private def dumpDictWithPLinkedIndex(name: String, nextItemIndexIn: Int  = 0): Unit = {
    val outfilePath = languageToPath(dictsToLanguagePair(name))
    //    dict.setItems(limit = Some(5))
    val outfileStr = s"$outfilePath/$name/$name.babylon"
    val dict = DsalPDotPageIndexDict.getNewDict(name=name)
    dict.dump(outfileStr = outfileStr)
  }

  private def dumpQStyleDict(name: String, startPageIndex: String  = "1"): Unit = {
    val browser: JsoupBrowser = new JsoupBrowser() {
      override def defaultRequestSettings(conn: Connection): Connection = {
        super.defaultRequestSettings(conn).timeout(5 * 60 * 1000 /* 5 min*/)
      }
    }
    val outfilePath = languageToPath(dictsToLanguagePair(name))
    //    dict.setItems(limit = Some(5))
    val outfileStr = s"$outfilePath/$name/$name.babylon"
    val dict = DsalQStyleDict(name=name, browser=browser)
    dict.dump(outfileStr = outfileStr, startPageIndex=startPageIndex)
  }

  // TODO: Parse pages like http://dsalsrv02.uchicago.edu/cgi-bin/app/schmidt_query.py?display=utf8def&page=2
  // Or if roman utf definitions are preferred: 
  // https://dsalsrv04.uchicago.edu/cgi-bin/app/biswas-bengali_query.py?page=477&display=romutfdef

  def main(args: Array[String]): Unit = {
//    dumpDictWithPLinkedIndex(name = "date")
//    dumpDictWithPLinkedIndex(name = "turner")
//    dumpDictWithPLinkedIndex(name = "bahri")
//    dumpDictWithPLinkedIndex(name = "praharaj")
//    dumpDictWithPLinkedIndex(name = "molesworth")
//    dumpDictWithPLinkedIndex(name = "vaze")
    //        dumpDictWithPLinkedIndex(name = "caturvedi")
    //            dumpDictWithPLinkedIndex(name = "gundert")
//    dumpDictWithPLinkedIndex(name = "candrakanta")
//    dumpDictWithPLinkedIndex(name = "singh")
//    dumpDictWithPLinkedIndex(name = "carter")
    dumpQStyleDict(name="biswas-bengali")
    // TODO: get the below
    //    dumpDictWithPLinkedIndex(name = "fallon")
    //    dumpDictWithPLinkedIndex(name = "platts")
    //    dumpDictWithPLinkedIndex(name = "shakespear")
  }
}
