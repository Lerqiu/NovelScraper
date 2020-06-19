import com.share.email.EmailNotification
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import java.util.*

object UpdateData {

    var isRunning = false
    fun runUpdate() { //Co pewien czas
        if (isRunning) return
        isRunning = true
        var timeS: Int = LocalDateTime.now().second

        GlobalScope.launch {
            while (true) {
                val date = Date()
                runOneTime_P()
                if ((Date().getTime() - date.getTime()) > timeS * 1000) {
                    delay((Date().getTime() - date.getTime()) - timeS * 1000)
                }
            }
        }
    }

    private fun runOneTime_P() {
        try {

            for (i in DataManagement.getWebsites()) {
                if (WebSiteScraperManagement.DoExistWebScraper(i.link)) {
                    val scraper = WebSiteScraperManagement.FindWebScraper(i.link)
                    if (scraper.checkDataUpdate(i)) {
                        DataManagement.updateNovel(scraper.getData())
                        addToLog("Nastąpiła aktualizacja: " + i.Title + " Najnowszy rozdział to :" + i.lastNewChapter.Number)
                        if (i.sendEmail)
                            EmailNotification.send(
                                "Nastąpiła aktualizacja: " + i.Title + "<br>" +
                                        "Link do strony głównej" + i.link + "<br>" +
                                        " Najnowszy rozdział to :" + i.lastNewChapter.Number + i.lastNewChapter.Name + "<br>" +
                                        "Link: " + i.lastNewChapter.link,
                                i.Title + " " + i.lastNewChapter.Number + "<br>" +
                                        "Opublikowany przez: " + i.lastNewChapter.PublishedBy
                            )
                    }
                }
            }
        } catch (e: Exception) {
            this.addToLog("Nastąpił error z aktualizacjią \n" + e)
        }
    }

    fun runOneTime() {
        GlobalScope.launch {
            runOneTime_P()
        }
    }

    fun addToLog(s: String) {
        val date = LocalDateTime.now()
        File("log").appendText(date.toString() + " " + s + "\n")
    }

    fun addNewNovel(link: String) {
        GlobalScope.launch {
            if (!DataManagement.isNovelAdded(link)) {
                val scraper = WebSiteScraperManagement.FindWebScraper(link)
                val novel = scraper.getData()
                DataManagement.addNewNovel(novel)
            }
        }
    }
}