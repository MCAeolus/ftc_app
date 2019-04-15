package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.replay_v2

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.common.common_machines.IMU
import java.io.*
import java.util.*

class ReplayFile {
    companion object {
        const val EXTERNAL_DIRECTORY_HEADING = "Replays"
        const val REPLAY_FILE_SUFFIX = ".dat"
    }


    class DataStream(rawFilePath : String, val hardware : HardwareMap) {

        var filePath = rawFilePath
            private set
        private val file: File

        init {
            if (!rawFilePath.toLowerCase().endsWith(REPLAY_FILE_SUFFIX)) filePath += REPLAY_FILE_SUFFIX

            file = File(hardware.appContext.getExternalFilesDir(EXTERNAL_DIRECTORY_HEADING), filePath)
        }

        private val data = ArrayList<DataPoint>()
        private var iterableData: Iterator<DataPoint>? = null
        private var pointBuffer: DataPoint? = null

        fun load() {
            try {
                val datastream = ObjectInputStream(file.inputStream())

                do {
                    try {
                        data.add(datastream.readObject() as ReplayFile.DataPoint)
                    } catch (e: Exception) {
                        break
                    }
                } while (true)

                datastream.close()
            }catch (e : EOFException) { }
        }

        fun write() {

            val datastream = ObjectOutputStream(file.outputStream())

            data.forEach { datastream.writeObject(it) }

            datastream.close()
        }

        fun prepare() {
            iterableData = data.iterator()
        }

        fun nextPoint(): DataPoint? {
            if (iterableData == null && data.isNotEmpty()) prepare()
            else if (iterableData == null) return null

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

        fun pointsUntil(time: Double): Pair<List<DataPoint>, Boolean> {
            val retList = ArrayList<DataPoint>()

            do {
                val current = nextPoint()
                if (current != null) {
                    if (current.time > time) {
                        pointBuffer = current
                        break
                    } else retList.add(current)
                } else break
            } while (true)

            return Pair(retList, hasFinished())
        }

        private fun hasFinished(): Boolean = (iterableData?.hasNext() != true)

        fun newPoint(time: Double, bytes: ArrayList<DataByte>): DataPoint {
            val point = DataPoint(time, time getDelta data.lastOrNull(), bytes)
            data.add(point)
            return point
        }

        fun newPoint(time: Double): DataPoint {
            val point = DataPoint(time, time getDelta data.lastOrNull())
            data.add(point)
            return point
        }

        fun getRawData(): List<DataPoint> {
            return data.clone() as List<DataPoint>
        }

        private infix fun Double.getDelta(point: DataPoint?): Double = this - (point?.time ?: this)

        fun trim(from : Double, to : Double) {
            var li = data.clone() as MutableList<DataPoint>

            if(li.last().time <= to || li.last().time <= from || from >= to) throw Error("Time input is not possible.")

            var beginIndex = -1
            var endIndex = -1

            for(i in 0 until li.size) {
                val d = li[i]
                if (d.time >= from) {
                    beginIndex = i
                    break
                }
            }

            for(i in (beginIndex+1)..li.lastIndex) {
                val d = li[i]
                if (d.time >= to) {
                    endIndex = i
                    break
                }
            }

            if(beginIndex == -1 || endIndex == -1) throw Error("This shouldn't have happened!")

            li = li.subList(beginIndex, endIndex)

            data.clear()

            var timeT = 0.0
            for(d in li) {
                data.add(DataPoint(timeT, d.timeDelta, d.bytes))
                timeT += d.timeDelta

            }
        }

        /**
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
         **/
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