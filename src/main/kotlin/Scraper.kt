import org.jsoup.Connection
import java.lang.NullPointerException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.*
import java.time.LocalDateTime


abstract class WebScrapers {
    abstract val linkFormat: String
    abstract var link:String
    abstract fun getData(): WebSiteData
    abstract fun checkDataUpdate(data: WebSiteData): Boolean
    fun checkLinkFormat(): Boolean {
        return (link.commonPrefixWith(linkFormat).length == linkFormat.length)
    }
}

object WebSiteScraperManagement {
    fun DoExistWebScraper(s: String) :Boolean{
        try {
            this.FindWebScraper(s)
            return true
        }catch (e:Exception){
            return false
        }
    }

    fun FindWebScraper(s: String): WebScrapers {
        if (NovelUpdates(s).checkLinkFormat())
            return NovelUpdates(s)
        if(ReadLightNovel(s).checkLinkFormat())
            return ReadLightNovel(s)
        //Kolejne klasy sprawdzamy podobnie
        throw NullPointerException()
    }
}