package sanskritCoders.dsal

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.jsoup.Connection
import org.slf4j.LoggerFactory

object scraper {
  //noinspection ScalaUnusedSymbol
  private val log = LoggerFactory.getLogger(getClass.getName)
  private val languageToPath = Map(
    ("telugu", "english") -> "/home/vvasuki/indic-dict/stardict-telugu/te-head/en-entries/",
    ("kannada", "english") -> "/home/vvasuki/indic-dict/stardict-kannada/kn-head/en-entries/",
    ("tamil", "english") -> "/home/vvasuki/indic-dict/stardict-tamil/ta-head/en-entries/",
    ("tamil", "tamil") -> "/home/vvasuki/indic-dict/stardict-tamil/ta-head/ta-entries/",
    ("marathi", "english") -> "/home/vvasuki/indic-dict/stardict-marathi/ma-head/other-entries/",
    ("marathi", "marathi") -> "/home/vvasuki/indic-dict/stardict-marathi/ma-head/ma-entries/",
    ("nepali", "english") -> "/home/vvasuki/indic-dict/stardict-nepali/ne-head/en-entries/",
    ("hindi", "english") -> "/home/vvasuki/indic-dict/stardict-hindi/hi-head/en-entries/",
    ("prakrit", "hindi") -> "/home/vvasuki/indic-dict/stardict-prakrit/prakrit-head/hi-entries/",
    ("sinhala", "english") -> "/home/vvasuki/indic-dict/stardict-sinhala/si-head/en-entries/",
    ("kashmiri", "english") -> "/home/vvasuki/indic-dict/stardict-kashmiri/ks-head/en-entries/",
    ("assamese", "english") -> "/home/vvasuki/indic-dict/stardict-assamese/as-head/en-entries/",
    ("persian", "english") -> "/home/vvasuki/indic-dict/stardict-persian/te-head/en-entries/",
    ("malayalam", "english") -> "/home/vvasuki/indic-dict/stardict-malayalam/ml-head/en-entries/",
    ("panjabi", "english") -> "/home/vvasuki/indic-dict/stardict-panjabi/pa-head/en-entries/",
    ("bengali", "english") -> "/home/vvasuki/indic-dict/stardict-bengali/bn-head/en-entries/",
    ("bengali", "bengali") -> "/home/vvasuki/indic-dict/stardict-bengali/bn-head/bn-entries/",
    ("oriya", "english") -> "/home/vvasuki/indic-dict/stardict-oriya/or-head/",
    ("divehi", "english") -> "/home/vvasuki/indic-dict/stardict-divehi/dv-head/en-entries/",
    ("lushai", "english") -> "/home/vvasuki/indic-dict/stardict-lushai/lushai-head/en-entries/",
    ("manipuri", "english") -> "/home/vvasuki/indic-dict/stardict-manipuri/manipuri-head/en-entries/",
    ("rajasthani", "english") -> "/home/vvasuki/indic-dict/stardict-rajasthani/rajasthani-head/en-entries/",
  )
  private val dictsToLanguagePair = Map(
    "berntsen" -> ("marathi", "english"),
    "molesworth" -> ("marathi", "english"),
    "vaze" -> ("marathi", "english"),
    "date" -> ("marathi", "marathi"),
    "tulpule" -> ("marathi", "marathi"),
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
    "hayyim" -> ("persian", "english"),
    "steingass" -> ("persian", "english"),
    "biswas-bengali" -> ("bengali", "english"),
    "biswas-bangala" -> ("bengali", "bengali"),
    "bhattacharya" -> ("bengali", "bengali"),
    "dasa" -> ("bengali", "bengali"),
    "carter" -> ("sinhala", "english"),
    "kittel" -> ("kannada", "english"),
    "maniku" -> ("divehi", "english"),
    "sheth" -> ("prakrit", "hindi"),
    "lorrain" -> ("lushai", "hindi"),
    "sharma" -> ("manipuri", "english"),
    "gundert" -> ("malayalam", "english"),
    "gwynn" -> ("telugu", "english"),
    "brown" -> ("telugu", "english"),
    "mcalpin" -> ("tamil", "english"),
    "crea" -> ("tamil", "tamil"),
    "tamil-idioms" -> ("tamil", "tamil"),
    "winslow" -> ("tamil", "english"),
  )

  private def dumpDictWithPLinkedIndex(name: String, nextItemIndexIn: Int  = 0): Unit = {
    val outfilePath = languageToPath(dictsToLanguagePair(name))
    //    dict.setItems(limit = Some(5))
    val outfileStr = s"$outfilePath/$name/$name.babylon"
    val dict = DsalPDotPageIndexDict.getNewDict(name=name)
    dict.dump(outfileStr = outfileStr)
  }

  private def dumpQStyleDict(name: String, startPageIndex: String  = "1", hwTags:Seq[String]=Seq("hw")): Unit = {
    val browser: JsoupBrowser = new JsoupBrowser() {
      override def defaultRequestSettings(conn: Connection): Connection = {
        super.defaultRequestSettings(conn).timeout(5 * 60 * 1000 /* 5 min*/)
      }
    }
    val languagePair = dictsToLanguagePair(name)
    val outfilePath = languageToPath(languagePair)
    //    dict.setItems(limit = Some(5))
    val outfileStr = s"$outfilePath/$name/$name.babylon"
    // if (languagePair._2 == "english") "romutfdef" else "utf8def"
    val entryEncoding:String =  "utf8def"
    val dict = DsalQStyleDict(name=name, browser=browser, entryEncoding=entryEncoding, hwTags=hwTags)
    dict.dump(outfileStr = outfileStr, startPageIndex=startPageIndex)
  }

  def main(args: Array[String]): Unit = {
//    dumpDictWithPLinkedIndex(name = "date")
//    dumpDictWithPLinkedIndex(name = "turner")
//    dumpDictWithPLinkedIndex(name = "bahri")
//    dumpDictWithPLinkedIndex(name = "praharaj")
//    dumpDictWithPLinkedIndex(name = "molesworth")
//    dumpDictWithPLinkedIndex(name = "vaze")
    //        dumpDictWithPLinkedIndex(name = "caturvedi")
    //            dumpDictWithPLinkedIndex(name = "gundert")
//    dumpDictWithPLinkedIndex(name = "singh")
//    dumpDictWithPLinkedIndex(name = "carter")
//        dumpQStyleDict(name="schmidt") // nepali devanAgarI, needs new parser.
    //    dumpQStyleDict(name="sheth")
//        dumpQStyleDict(name="kittel")
//        dumpQStyleDict(name="maniku")
//        dumpQStyleDict(name="dasa")
//        dumpQStyleDict(name="bhattacharya")
//        dumpQStyleDict(name="grierson", hwTags = Seq("p1", "d"))
//        dumpQStyleDict(name="tulpule")
//        dumpQStyleDict(name="brown")
//        dumpQStyleDict(name="crea")
//        dumpQStyleDict(name="winslow")
//            dumpQStyleDict(name="platts", hwTags = Seq("hw", "d")) // TODO:Skipping urdu headwords

    //    dumpDictWithPLinkedIndex(name = "candrakanta")
    //    dumpQStyleDict(name="biswas-bangala")
//        dumpQStyleDict(name="mcalpin", hwTags = Seq("hw", "tam"))
    
    // TODO: get the below
    //    dumpQStyleDict(name="sharma")
    //    dumpQStyleDict(name="macalister")
//    dumpQStyleDict(name="tamil-idioms")
//    dumpQStyleDict(name="gwynn") // Unusual structure. telugu transliteration needed. https://dsal.uchicago.edu/cgi-bin/app/gwynn_query.py?page=26
//        dumpDictWithPLinkedIndex(name = "fallon") // Urdu and IAST script headwords
//        dumpDictWithPLinkedIndex(name = "platts") // Urdu and IAST script headwords  (sometimes devanAgarI roots too) 
    //    dumpDictWithPLinkedIndex(name = "shakespear") // Urdu and IAST script headwords  (sometimes devanAgarI roots too) 
  }
}
