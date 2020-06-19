import java.io.Serializable
import java.time.LocalDateTime

data class Chapter(val Number: String, val Name: String, val PublishedBy: String, val link: String, val timeOfAdd: String):Serializable {}

class WebSiteData(link: String, Title: String, Author: String, time: LocalDateTime):Serializable {
    val link: String = link
    val Title: String = Title
    val Author: String = Author
    var lastCheck: LocalDateTime = time
    var sendEmail = true
    var genres: MutableList<String> = ArrayList()
    var chapters: MutableList<Chapter> = ArrayList()

    var lastReadedChapter = Chapter("","","","","")
    var lastNewChapter = Chapter("","","","","")

    fun addGenres(s:String){
        genres.add(s)
    }

    //index 0 to najnowszy
    fun addChapterAtBeginning(cha: Chapter) {
        chapters.add(0,cha)
    }
    fun addChapterAtEnd(cha: Chapter) {
        chapters.add(cha)
    }
}

class Data :Serializable {

    var email =""
    var sendEmail = true
    var time:Int = 60*10
    var WebSites: MutableList<WebSiteData> = ArrayList()

    fun addNovel(d:WebSiteData){
        d.lastReadedChapter = d.chapters.last()
        d.lastNewChapter = d.chapters[0]
        WebSites.add(d)
    }
}