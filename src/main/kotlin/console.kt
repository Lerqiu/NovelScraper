fun main(r: Array<String>) {

    var command: String?
    DataManagement.loadDataFromDisk()
    println("Wciśnięcie dowolnego przycisku poza wymienionymi w opcjach spowoduje wyjście z programu")
    pętelka@ while (true) {
        println("Wciśnij 0 by wyświetlić dostępne opcje")
        command = readLine()
        when (command) {
            "0" -> {
                println("Menu:")
                println("1 <=> Dodaj novelke")
                println("2 <=> Wypisz wszystkie")
                println("3 <=> Wypisz nieprzeczytane")
                println("4 <=> Usuń novelkę (podaj link)")
                println("5 <=> Oznacz jako przeczytaną (podaj link novelki)")
                println("6 <=> Aktualizuj bazę (jednorazowo)")
                println("6a <=> Aktualizuj bazę (periodycznie)")
                println("6t <=> Ustaw okres sprawdzania aktualizacji")
                println("6tc <=> Sprawdz okres sprawdzania aktualizacji")
                println("7c <=> Zmień adres email")
                println("7g <=> Wypisz adres email")
                println("8 <=> Usuń ostatni rozdział") //Dla testów
                println("9 <=> Wyświetl wszystkie rozdziały") //Dla testów
            }
            "1" -> {
                print("Link: ")
                val line = readLine()
                if (line != null)
                    UpdateData.addNewNovel(line)
            }
            "2" -> {
                println("Lista novelek:")
                val novels = DataManagement.listOfNovels()
                for (i in novels) {
                    println(i)
                }
            }
            "3" -> {
                println("Nieprzeczytane novelki:")
                val novels = DataManagement.listNotReadedNovel()
                for (i in novels) {
                    println(i)
                }
            }
            "4" -> {
                print("Podaj link novelki do usunięcia: ")
                val line = readLine()
                if (line != null && !DataManagement.removeNovel(line))
                    println("Brak novelki o takim linku w bazie")
            }
            "5" -> {
                print("Podaj link przeczytanej novelki: ")
                val line = readLine()
                if (line != null && !DataManagement.markAsReadNovel(line))
                    println("Brak novelki o takim linku w bazie")
            }
            "6" -> {
                println("Sprawdzenie aktualizacji")
                UpdateData.runOneTime()
            }
            "6a" -> {
                UpdateData.runUpdate()
            }
            "6t" -> {
                print("Podaj okres w s: ")
                val line = readLine()
                if (line != null && line.toInt() > 0)
                    DataManagement.setPeriodOfChecking(line.toInt())
                else
                    println("Niepoprawny format czasu")
            }
            "6tc" -> {
                println("Okres wynosi: " + DataManagement.getPeriodOfChecking().toString() + " sekund.")
            }
            "7c" -> {
                print("Podaj email : ")
                val line = readLine()
                if (line != null) {
                    DataManagement.setEmail(line)
                }
            }
            "7g" -> {
                if (DataManagement.data.email.length > 0)
                    println("Email to: " + DataManagement.getEmail())
                else
                    println("Brak adresu email")
            }
            "8" -> {
                print("Podaj link : ")
                val line = readLine()
                if (line != null) {
                    DataManagement.removeLastChapter(line)
                }
            }
            "9" -> {
                for (i in DataManagement.getWebsites()) {
                    println("<------------------------------->" + i.Title)
                    for (g in i.chapters)
                        println(g)
                }

            }
            else -> break@pętelka
        }
    }
    DataManagement.saveDataToDisk()
}