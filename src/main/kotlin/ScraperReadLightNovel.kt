import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.time.LocalDateTime

class ReadLightNovel(link: String) : WebScrapers() {
    override var link = link
    override val linkFormat: String = """https://www.readlightnovel.org/"""

    private fun setGenres(doc: Document, data: WebSiteData) {
        val g = doc.select("#seriesgenre a")
        for (i in doc.select(".novel-detail-body").get(1).select("li a")) {
            data.addGenres(i.text())
        }

        val t = doc.select("#showtags a")
        for (i in doc.select(".novel-detail-body").get(2).select("li a")) {
            data.addGenres(i.text())
        }
    }

    override fun getData(): WebSiteData {
        val doc = Jsoup.connect(link).get()
        var data =
            WebSiteData(
                link,
                doc.select(".block-title h1").text(),
                doc.select(".novel-detail-body").get(4).select("ul li").text(),
                LocalDateTime.now()
            )


        for (i in doc.select(".chapter-chs li a")) {
            data.addChapterAtBeginning(Chapter(i.text(), "", "", i.attr("href"), ""))
        }

        setGenres(doc, data)
        return data
    }

    override fun checkDataUpdate(data: WebSiteData): Boolean {
        val doc = Jsoup.connect(link).get()

        val last = doc.select(".chapter-chs li a").last()
        if (last.attr("href") != data.lastNewChapter.link)
            return true
        return false
    }
}
