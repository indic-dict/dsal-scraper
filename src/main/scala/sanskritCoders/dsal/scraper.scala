package sanskritCoders.dsal

import java.io.{File, FileWriter, PrintWriter, StringWriter}

import me.tongfei.progressbar.ProgressBar
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import org.slf4j.LoggerFactory

object scraper {
  private val log = LoggerFactory.getLogger(getClass.getName)
  private val languageToPath = Map{
    ("marathi", "english") -> "/home/vvasuki/stardict-marathi/ma-head/"
  }
  private val dictsToLanguagePair = Map{
    "berntsen" -> ("marathi", "english")
  }

  private def dumpDict(name: String, nextItemIndexIn: Int  = 0): Unit = {
    val browser: JsoupBrowser = JsoupBrowser.typed()
    val dict = new DsalDictionaryIterator(name=name, browser = browser)
    val outfilePath = languageToPath(dictsToLanguagePair(name))
//    dict.setItems(limit = Some(5))
    val outfileStr = s"$outfilePath/$name/$name.babylon"
    val outFileObj = new File(outfileStr)
    outFileObj.getParentFile.mkdirs()
    val destination = new PrintWriter(new FileWriter(outFileObj, nextItemIndexIn > 0))
    var nextItemIndex = nextItemIndexIn
    dict.take(nextItemIndex)
    val progressBar = new ProgressBar("itemsPb", dict.likelySize)
    progressBar.start()
    try{
      dict.foreach(item => {
        if (item.headwords.nonEmpty) {
          val headersLine = item.headwords.mkString("|")
          val meaningLine = item.getMeaningLine
          destination.println(headersLine)
          destination.println(meaningLine)
          destination.println("")
        }
        nextItemIndex = nextItemIndex + 1
        progressBar.step()
      })
    } catch {
      case ex: Exception => {
        val sw = new StringWriter
        ex.printStackTrace(new PrintWriter(sw))
        log.error("")
        log.error(sw.toString)
        log.error(s"nextItemIndex should be ${nextItemIndex}")
      }
    }
    finally {
      destination.close()
      progressBar.stop()
      log.info(s"Done writing ${nextItemIndex} items!")
    }
  }

  def main(args: Array[String]): Unit = {
    dumpDict(name = "berntsen")
  }
}
