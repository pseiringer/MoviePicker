package com.example.canteenchecker.moviepicker.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.File
import java.io.FileReader
import java.io.FileWriter

data class Watchlist(
    val entries: ArrayList<Int>
)

class WatchlistManager {
    companion object {

        val fileName = "watchlist"

        public fun setWatchList(context: Context, watchlist: Watchlist){
            writeWatchlist(context, Gson().toJson(watchlist))
        }
        public fun getWatchList(context: Context): Watchlist{
            return try{
                Gson().fromJson(readWatchlist(context), Watchlist::class.java)
                    ?: Watchlist(ArrayList())
            } catch (e: JsonSyntaxException){
                Watchlist(ArrayList())
            }
        }

        private fun writeWatchlist(context: Context, watchlist: String?) {
            val file = getWatchlistFile(context)

            try {
                val writer = FileWriter(file)
                writer.append(watchlist)
                writer.flush()
                writer.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        private fun readWatchlist(context: Context): String? {
            val file = getWatchlistFile(context)

            var text: String? = null
            try {
                val reader = FileReader(file)
                text = reader.readText()
                reader.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return text
        }

        private fun getWatchlistFile(context: Context): File {
            val file = File(context.filesDir, fileName)
            if (!file.exists()) {
                file.createNewFile()
            }
            return file
        }
    }
}