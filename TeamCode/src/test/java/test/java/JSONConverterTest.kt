package test.java

import android.provider.MediaStore
import android.util.JsonReader
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import junit.framework.Assert.assertEquals
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.replay_v2.ReplayFile
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.util.JsonConversionUtil
import org.junit.Test
import java.io.File
import java.io.PrintWriter
import kotlin.reflect.KClass
import kotlin.reflect.full.createType
import kotlin.reflect.full.memberProperties

class JSONConverterTest {

    @Test
    fun testJsonStringFormatting() {

        val convertingPoints = listOf(
                ReplayFile.DataPoint(0.0, 0.1, arrayListOf(ReplayFile.DataByte("d", listOf(1.0)), ReplayFile.DataByte("x", listOf(0.2)))),
                ReplayFile.DataPoint(0.5, 0.1, arrayListOf(ReplayFile.DataByte("d", listOf(0.9)), ReplayFile.DataByte("x", listOf(0.3))))
        )

        val finalArray = JsonArray()

        for(c in convertingPoints) finalArray.add(JsonConversionUtil.recursiveObjectToJson(c))

        println(finalArray.toString())

        val originalObject = JsonConversionUtil.recursiveJsonToObject(finalArray) as List<ReplayFile.DataPoint>

        println(originalObject[0].time)
        println(originalObject[1].time)

        println(originalObject[0].bytes[0].name)
        println(originalObject[0].bytes[1].name)



    }
}