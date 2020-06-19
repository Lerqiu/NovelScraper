import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.time.LocalDateTime

class NovelUpdates(link: String) : WebScrapers() {
    override var link = link
    override val linkFormat: String = """https://www.novelupdates.com/series/"""

    private fun getPartOfData(link: String, data: WebSiteData) {
        val doc = Jsoup.connect(link).get()

        val ele = doc.select("#myTable tr")
            .filter { it != null }
        for (i in 1..(ele.size - 1)) {
            if (ele.get(i).getElementsByTag("td").size == 3) {
                val timeOfAdd = ele.get(i).getElementsByTag("td").get(0).text().toString()
                val PublishedBy = ele.get(i).getElementsByTag("td").get(1).text().toString()
                val Number = ele.get(i).getElementsByTag("td").get(2).children().last().text().toString()
                val Name = ""
                var link =
                    ele.get(i).getElementsByTag("td").get(2).children().last().tagName("a")
                        .attr("href").toString()
                link = link.substring(2, link.length)
                data.addChapterAtEnd(Chapter(Number, Name, PublishedBy, link, timeOfAdd))
            }
        }
        val ele2 = doc.select(".digg_pagination a.next_page").filter { it != null }.map { a -> a.attr("href") }
        for (i in 0..(ele2.size - 1)) {
            if (link.get(ele2.get(i).length - 1) == '/')
                getPartOfData(this.link + ele2.get(i).subSequence(2, ele2.get(i).length), data)
            else getPartOfData(this.link + '/' + ele2.get(i).subSequence(2, ele2.get(i).length), data)
        }
    }

    private fun setGenres(doc: Document, data: WebSiteData) {
        val g = doc.select("#seriesgenre a")
        for (i in 0..(g.size - 1)) {
            data.addGenres(g.get(i).text())
        }

        val t = doc.select("#showtags a")
        for (i in 0..(t.size - 1)) {
            data.addGenres(t.get(i).text())
        }
    }

    override fun getData(): WebSiteData {
        val doc = Jsoup.connect(link).get()
        var data = WebSiteData(link, doc.select(".seriestitlenu").text(), doc.select("#authtag").text(), LocalDateTime.now())
        getPartOfData(link, data)
        setGenres(doc, data)
        return data
    }

    override fun checkDataUpdate(data: WebSiteData): Boolean {
        val doc = Jsoup.connect(link).get()

        val ele = doc.select("#myTable tr")
            .filter { it != null }
        if (ele.size >= 1) {
            try {
                val PublishedBy = ele.get(1).getElementsByTag("td").get(1).text().toString()
                val Number = ele.get(1).getElementsByTag("td").get(2).children().last().text().toString()

                if (!(Number == data.lastNewChapter.Number && PublishedBy == data.lastNewChapter.PublishedBy)) {
                    return true
                }
            } catch (e: Exception) {
                UpdateData.addToLog("Nastąpił error z wprawdzaniem aktualizacji \n" + e)
            }
        }
        return false
    }
}
