import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.lang.Exception
import java.util.concurrent.Semaphore


object DataManagement {
    val dataFileName = "data.bin"
    var data: Data = Data()

    private val sharedLock = Semaphore(1, true)

    fun saveDataToDisk() {
        try {
            sharedLock.acquire()
            ObjectOutputStream(FileOutputStream(this.dataFileName)).use { it -> it.writeObject(data) }
            UpdateData.addToLog("Zapisano dane.")
        } finally {
            sharedLock.release()
        }
    }

    fun loadDataFromDisk() {
        try {
            sharedLock.acquire()
            try {
                ObjectInputStream(FileInputStream(dataFileName)).use {
                    val d = it.readObject()
                    if (d is Data)
                        this.data = d
                }
                UpdateData.addToLog("Wczytano dane.")
            } catch (e: Exception) {
                UpdateData.addToLog("Próba wczytania danych nieudana. Brak pliku.")
            }
        } finally {
            sharedLock.release()
        }
    }

    fun getPeriodOfChecking(): Int {
        try {
            sharedLock.acquire()
            return data.time
        } finally {
            sharedLock.release()
        }
    }

    fun setPeriodOfChecking(e: Int) {
        try {
            sharedLock.acquire()
            if (e >= 0)
                data.time = e
        } finally {
            sharedLock.release()
        }
    }

    fun getEmail(): String {
        try {
            sharedLock.acquire()
            return data.email
        } finally {
            sharedLock.release()
        }
    }

    fun setEmail(e: String) {
        try {
            sharedLock.acquire()
            data.email = e
        } finally {
            sharedLock.release()
        }
    }

    fun isEmailAccepted(): Boolean {
        try {
            sharedLock.acquire()
            return this.data.sendEmail
        } finally {
            sharedLock.release()
        }
    }

    fun setEmailAcceptedTo(e: Boolean) {
        try {
            sharedLock.acquire()
            this.data.sendEmail = e
        } finally {
            sharedLock.release()
        }
    }

    fun getWebsites(): MutableList<WebSiteData> {
        try {
            sharedLock.acquire()
            return this.data.WebSites.toMutableList()
        } finally {
            sharedLock.release()
        }
    }

    fun addNewNovel(novel: WebSiteData) {
        try {
            sharedLock.acquire()
            this.data.addNovel(novel)
            UpdateData.addToLog("Dodano novelkę.Link: " + novel.link + " Tytuł: " + novel.Title)
        } finally {
            sharedLock.release()
        }
    }

    fun updateNovel(new: WebSiteData) {
        try {
            sharedLock.acquire()
            for (i in this.data.WebSites)
                if (i.link == new.link) {
                    i.chapters = new.chapters
                    if (i.chapters.size > 0)
                        i.lastNewChapter = i.chapters[0]
                }
        } finally {
            sharedLock.release()
        }
    }

    fun removeNovel(link: String): Boolean {
        try {
            sharedLock.acquire()
            if (isNovelAdded(link)) {
                this.data.WebSites = this.data.WebSites.filter { it -> it.link != link } as MutableList<WebSiteData>
                UpdateData.addToLog("Usunięto: " + link)
                return true
            }
            return false
        } finally {
            sharedLock.release()
        }
    }

    fun isNovelAdded(link: String): Boolean {
        try {
            sharedLock.acquire()
            return isNovelAdded_P(link)
        } finally {
            sharedLock.release()
        }
    }

    private fun isNovelAdded_P(link: String): Boolean {
        for (i in this.data.WebSites)
            if (i.link == link)
                return true
        return false
    }

    fun listOfNovels(): List<String> {
        try {
            sharedLock.acquire()
            val li: MutableList<String> = ArrayList()
            for (i in this.data.WebSites)
                li.add(i.Title)
            return li
        } finally {
            sharedLock.release()
        }
    }

    fun listNotReadedNovel(): List<String> {
        try {
            sharedLock.acquire()
            val li: MutableList<String> = ArrayList()
            for (i in this.data.WebSites)
                if (i.lastReadedChapter.link != i.lastNewChapter.link)
                    li.add(i.Title)
            return li
        } finally {
            sharedLock.release()
        }
    }

    fun markAsReadNovel(link: String): Boolean {
        try {
            sharedLock.acquire()
            for (i in this.data.WebSites)
                if (i.link == link) {
                    UpdateData.addToLog("Zaktualizowano postęp w czytaniu." + i.lastReadedChapter.link + " -> " + i.lastNewChapter.link)
                    i.lastReadedChapter = i.lastNewChapter
                    return true
                }
            return false
        } finally {
            sharedLock.release()
        }
    }

    fun removeLastChapter(link: String) {
        try {
            sharedLock.acquire()
            for (i in this.data.WebSites)
                if (i.link == link) {

                    if (i.chapters.size > 0) {
                        UpdateData.addToLog("Usuwanie ostatniego rozdziału:" + i.Title + " " + i.chapters[0].Number)
                        i.chapters.removeAt(0)
                        i.lastNewChapter = i.chapters[0]
                    }
                }
        } finally {
            sharedLock.release()
        }
    }
}
