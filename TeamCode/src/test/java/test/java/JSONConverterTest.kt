package test.java

import android.provider.MediaStore
import android.util.JsonReader
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonArray
import junit.framework.Assert.assertEquals
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.replay_v2.ReplayFile
import org.junit.Test
import java.io.File
import java.io.PrintWriter

class JSONConverterTest {

    @Test
    fun testJsonStringFormatting() {

        val GSON = Gson()

        val convertingPoints = listOf(
                ReplayFile.DataPoint(0.0, 0.1, arrayListOf(ReplayFile.DataByte("d", listOf(1.0)), ReplayFile.DataByte("x", listOf(0.2)))),
                ReplayFile.DataPoint(0.5, 0.1, arrayListOf(ReplayFile.DataByte("d", listOf(0.9)), ReplayFile.DataByte("x", listOf(0.3))))
        )

        val jArray = JsonArray()

        convertingPoints.forEach { jArray.add(GSON.toJson(it)) }

        println("1 " + jArray.toString())



        val file = File("test.json")
        val writer = PrintWriter(file, "UTF-8")
        writer.print(jArray.toString())
        writer.close()

        println("2 " + file.reader().readText())
        file.reader().close()

        val jsonReader = GSON.newJsonReader(file.reader())
        jsonReader.beginArray()

        val data = arrayListOf<ReplayFile.DataPoint>()

        while(jsonReader.hasNext()) {
            try {
                val dataObject = GSON.fromJson(jsonReader.nextString(), ReplayFile.DataPoint::class.java)
                data.add(dataObject)
            }catch (e : java.lang.Exception) {
                Log.wtf("REPLAY LOADER", e)
            }
        }

        jsonReader.endArray()
        jsonReader.close()

        for(i in 0..1) {
            println(convertingPoints[i].bytes)
            println(convertingPoints[i].time)
            println(data[i].bytes)
            println(data[i].time)
        }
    }
}