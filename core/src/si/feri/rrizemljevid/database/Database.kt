package si.feri.rrizemljevid.database

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object Database {
    @Throws(IOException::class)
    fun fetchString(url: URL): String {
        return fetchString(url.openStream())
    }

    @Throws(IOException::class)
    fun fetchString(`is`: InputStream): String {
        val bis = ByteArrayOutputStream()
        val bytebuff = ByteArray(4096)
        var n: Int
        while (`is`.read(bytebuff).also { n = it } > 0) {
            bis.write(bytebuff, 0, n)
        }
        return bis.toString()
    }

    @Throws(IOException::class)
    fun fetchEvents(): LinkedHashMap<String, EventFirebase> {
        val url = URL("https://imhere-4ade3-default-rtdb.europe-west1.firebasedatabase.app/events.json")
        val jsonString = fetchString(url)
        val gson = Gson()
        val type = object : TypeToken<LinkedHashMap<String?, EventFirebase?>?>() {}.type
        return gson.fromJson(jsonString, type)
    }

    @Throws(IOException::class)
    fun addEvent(eventFirebase: EventFirebase) {
        val url = URL("https://imhere-4ade3-default-rtdb.europe-west1.firebasedatabase.app/events.json")
        val gson = Gson()

        //
        //
        //
        val con = url.openConnection()
        val http = con as HttpURLConnection
        http.setRequestProperty("X-HTTP-Method-Override", "PATCH")
        http.requestMethod = "POST"
        http.doOutput = true
        val out = HashMap<String?, EventFirebase>()
        out[eventFirebase.id] = eventFirebase
        val payload = gson.toJson(out)
        http.setFixedLengthStreamingMode(payload.length)
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
        http.connect()
        http.outputStream.use { os -> os.write(payload.toByteArray()) }

        //
        //
        //
        fetchString(url)
    }
}