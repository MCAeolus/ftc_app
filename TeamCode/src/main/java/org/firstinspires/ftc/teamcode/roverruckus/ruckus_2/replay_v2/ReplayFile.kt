package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.replay_v2

import android.util.Log
import com.google.gson.*
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.stream.JsonReader
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.common.common_machines.IMU
import org.firstinspires.ftc.teamcode.common.util.math.Pose2d
import java.io.*
import java.lang.reflect.Type
import java.util.*
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.memberProperties

class ReplayFile {
    companion object {
        const val EXTERNAL_DIRECTORY_HEADING = "Replays"
    }

    /**
     * The primary updates over original replay system is that this is saved externally, and that it is saved in a JSON format to be easily read elsewhere.
     */
    class DataStream(private var rawFilePath : String, val hardware : HardwareMap) {

        private val GSON : Gson
        private val file : File

        init {
            val builder = GsonBuilder()

            //add custom serializers/deserializers here
            builder.registerTypeAdapter(Pose2d::class.java, JsonDeserializer<Pose2d> { json, type, context ->

                val j = json.asJsonObject
                Pose2d(j.get("x").asDouble, j.get("y").asDouble, j.get("heading").asDouble)
            })

            GSON = builder.create()

            if(!rawFilePath.toLowerCase().endsWith(".json")) rawFilePath+=".json"

            file = File(hardware.appContext.getExternalFilesDir(EXTERNAL_DIRECTORY_HEADING), rawFilePath)
        }

        private val data = ArrayList<DataPoint>()
        private var iterableData: Iterator<DataPoint>? = null
        private var pointBuffer : DataPoint? = null

        fun load() {

            val jsonReader = GSON.newJsonReader(file.reader())
            jsonReader.beginArray()

            while(jsonReader.hasNext()) {
                try {
                    val dataObject = GSON.fromJson(jsonReader.nextString(), DataPoint::class.java)

                    data.add(dataObject)
                }catch (e : java.lang.Exception) {
                    Log.wtf("REPLAY LOADER", e)
                }
            }

            jsonReader.endArray()
            jsonReader.close()

            /**
            val datastream = ObjectInputStream(file.inputStream())

            do {
                try {
                    data.add(datastream.readObject() as DataPoint)
                } catch ( e : Exception ) { break }
            }while(true)

            datastream.close()
            **/
        }

        fun write() {

            val jArray = JsonArray()

            data.forEach {

                jArray.add(recursiveJsonFormatting(it))

            }

            val writer = PrintWriter(file)

            writer.print(jArray.toString())
            writer.close()

            /**
            val datastream = ObjectOutputStream(file.outputStream())

            data.forEach { datastream.writeObject(it) }



            datastream.close()
            **/
        }

        private fun recursiveJsonFormatting(o : Any) : JsonElement {

            if(o is Collection<*>) {
                val arr = JsonArray()

                for(v in o) arr.add(recursiveJsonFormatting(v!!))

                return arr
            } else {
                val obj = JsonObject()
                obj.addProperty("type", obj::class.java.name)

                val objValues = JsonObject()

                for(field in o::class.memberProperties) objValues.add(field.name, recursiveJsonFormatting(field.getter.call(o)!!))

                obj.add("value", objValues)

                return obj
            }
        }

        fun prepare() {
            iterableData = data.iterator()
        }

        fun nextPoint() : DataPoint? {
            if(iterableData == null && data.isNotEmpty()) prepare()
            else if(iterableData == null) return null

            return when {
                pointBuffer != null -> {
                    val pb = pointBuffer
                    pointBuffer = null
                    pb
                }
                iterableData!!.hasNext() -> iterableData?.next()
                else -> null
            }
        }

        fun pointsUntil(time : Double) : Pair<List<DataPoint>, Boolean> {
            val retList = ArrayList<DataPoint>()

            do {
                val current = nextPoint()
                if(current != null) {
                    if(current.time > time) {
                        pointBuffer = current
                        break
                    }
                    else retList.add(current)
                }else break
            }while(true)

            return Pair(retList, hasFinished())
        }

        private fun hasFinished() : Boolean = (iterableData?.hasNext() != true)

        fun newPoint(time : Double, bytes : ArrayList<DataByte>) : DataPoint {
            val point = DataPoint(time, time getDelta data.lastOrNull(), bytes)
            data.add(point)
            return point
        }

        fun newPoint(time : Double) : DataPoint {
            val point = DataPoint(time, time getDelta data.lastOrNull())
            data.add(point)
            return point
        }

        private infix fun Double.getDelta(point : DataPoint?) : Double = this - (point?.time ?: this)


        fun trim() {
            @Suppress("UNCHECKED_CAST")
            var newData = data.clone() as MutableList<DataPoint>

            var trimPointStart = -1
            var trimPointEnd = -1

            for (i in 0 until data.lastIndex) { // go from beginning to 1 less than the last index so that there can be a 'next' point always
                val current = data[i]
                val next = data[i + 1]

                if (!current.isSimilar(next, IMU.Config.DEVICE_NAME)) {
                    trimPointStart = i //we will trim to this point NOT INCLUSIVE
                    break
                }
            }
            if (trimPointStart > 0)
                newData = newData.subList(trimPointStart, newData.size)

            for (i in newData.lastIndex downTo 1) {
                val current = newData[i]
                val last = newData[i - 1]

                if (!current.isSimilar(last, IMU.Config.DEVICE_NAME)) {
                    trimPointEnd = i
                    break
                }
            }

            if (trimPointEnd > 0)
                newData = newData.subList(0, trimPointEnd+1)

            data.clear()
            var timeT = 0.0
            for(d in newData) {
                data.add(DataPoint(timeT, d.timeDelta, d.bytes))
                timeT += d.timeDelta

            }
        }
    }

    class DataPoint(val time: Double, val timeDelta: Double, val bytes: ArrayList<DataByte> = ArrayList()) : Serializable {

        constructor() : this(0.0, 0.0)

        fun addByte(data: DataByte): DataPoint {
            bytes.add(data)
            return this
        }

        fun isSimilar(point : DataPoint) : Boolean {
            bytes.forEach { if(!point.bytes.contains(it)) return false }
            return true
        }

        fun isSimilar(point : DataPoint, vararg ignore : String) : Boolean {
            bytes.forEach { if(!point.bytes.contains(it) && !ignore.contains(it.name)) return false }
            return true
        }
    }

    class DataByte(val name: String, val data: List<Any>) : Serializable {

        constructor() : this("", listOf())
    }
}